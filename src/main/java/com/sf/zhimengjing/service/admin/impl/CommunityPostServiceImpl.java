package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.CommunityPostAuditDTO;
import com.sf.zhimengjing.common.model.dto.CommunityPostQueryDTO;
import com.sf.zhimengjing.common.model.vo.CommunityPostListVO;
import com.sf.zhimengjing.common.model.vo.CommunityPostStatisticsVO;
import com.sf.zhimengjing.entity.admin.CommunityPost;
import com.sf.zhimengjing.mapper.admin.CommunityPostMapper;
import com.sf.zhimengjing.service.admin.CommunityPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: CommunityPostServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @Description: 社区帖子服务实现类
 */
@Service
@RequiredArgsConstructor
public class CommunityPostServiceImpl extends ServiceImpl<CommunityPostMapper, CommunityPost> implements CommunityPostService {

    private final CommunityPostMapper communityPostMapper;

    @Override
    public IPage<CommunityPostListVO> getPostListPage(CommunityPostQueryDTO queryDTO) {
        Page<CommunityPostListVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return communityPostMapper.selectPostListPage(page, queryDTO);
    }

    @Override
    public CommunityPostStatisticsVO getPostStatistics() {
        return communityPostMapper.selectPostStatistics();
    }

    @Override
    public CommunityPostListVO getPostDetail(Long id) {
        CommunityPost post = this.getById(id);
        if (post == null) {
            throw new GeneralBusinessException("帖子不存在");
        }

        // 构建VO（实际项目中可以使用MapStruct或BeanUtils）
        CommunityPostListVO vo = new CommunityPostListVO();
        vo.setId(post.getId());
        vo.setUserId(post.getUserId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setContentText(post.getContentText());
        vo.setImages(post.getImages());
        vo.setTags(post.getTags());
        vo.setCategoryId(post.getCategoryId());
        vo.setViewCount(post.getViewCount());
        vo.setLikeCount(post.getLikeCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setShareCount(post.getShareCount());
        vo.setCollectCount(post.getCollectCount());
        vo.setIsTop(post.getIsTop());
        vo.setIsHot(post.getIsHot());
        vo.setIsAnonymous(post.getIsAnonymous());
        vo.setStatus(post.getStatus());
        vo.setRejectReason(post.getRejectReason());
        vo.setAdminRemark(post.getAdminRemark());
        vo.setPublishedAt(post.getPublishedAt());
        vo.setLastCommentedAt(post.getLastCommentedAt());
        vo.setCreateTime(post.getCreateTime());
        vo.setUpdateTime(post.getUpdateTime());

        // 设置状态文本
        switch (post.getStatus()) {
            case 0:
                vo.setStatusText("待审核");
                break;
            case 1:
                vo.setStatusText("已通过");
                break;
            case 2:
                vo.setStatusText("已拒绝");
                break;
            case 3:
                vo.setStatusText("已删除");
                break;
            default:
                vo.setStatusText("未知");
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditPosts(CommunityPostAuditDTO auditDTO) {
        List<Long> postIds = auditDTO.getPostIds();
        Integer status = auditDTO.getStatus();

        if (postIds == null || postIds.isEmpty()) {
            throw new GeneralBusinessException("帖子ID列表不能为空");
        }

        if (status == null || (status != 1 && status != 2)) {
            throw new GeneralBusinessException("审核状态只能是1(已通过)或2(已拒绝)");
        }

        // 批量更新
        for (Long postId : postIds) {
            CommunityPost post = this.getById(postId);
            if (post == null) {
                continue;
            }

            post.setStatus(status);
            post.setRejectReason(auditDTO.getRejectReason());
            post.setAdminRemark(auditDTO.getAdminRemark());
            post.setUpdateTime(LocalDateTime.now());

            // 如果审核通过，设置发布时间
            if (status == 1 && post.getPublishedAt() == null) {
                post.setPublishedAt(LocalDateTime.now());
            }

            this.updateById(post);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePosts(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new GeneralBusinessException("帖子ID列表不能为空");
        }

        // 逻辑删除
        this.removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleTop(Long id) {
        CommunityPost post = this.getById(id);
        if (post == null) {
            throw new GeneralBusinessException("帖子不存在");
        }

        // 切换置顶状态
        post.setIsTop(post.getIsTop() == 1 ? 0 : 1);
        post.setUpdateTime(LocalDateTime.now());
        this.updateById(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleHot(Long id) {
        CommunityPost post = this.getById(id);
        if (post == null) {
            throw new GeneralBusinessException("帖子不存在");
        }

        // 切换热门状态
        post.setIsHot(post.getIsHot() == 1 ? 0 : 1);
        post.setUpdateTime(LocalDateTime.now());
        this.updateById(post);
    }
}