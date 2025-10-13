package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.CommunityPostAuditDTO;
import com.sf.zhimengjing.common.model.dto.CommunityPostQueryDTO;
import com.sf.zhimengjing.common.model.vo.CommunityPostListVO;
import com.sf.zhimengjing.common.model.vo.CommunityPostStatisticsVO;
import com.sf.zhimengjing.entity.admin.CommunityPost;

import java.util.List;

/**
 * @Title: CommunityPostService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: 社区帖子服务接口
 */
public interface CommunityPostService extends IService<CommunityPost> {

    /**
     * 分页查询帖子列表
     *
     * @param queryDTO 查询条件
     * @return 帖子列表
     */
    IPage<CommunityPostListVO> getPostListPage(CommunityPostQueryDTO queryDTO);

    /**
     * 查询帖子统计信息
     *
     * @return 统计信息
     */
    CommunityPostStatisticsVO getPostStatistics();

    /**
     * 根据ID查询帖子详情
     *
     * @param id 帖子ID
     * @return 帖子详情
     */
    CommunityPostListVO getPostDetail(Long id);

    /**
     * 审核帖子
     *
     * @param auditDTO 审核信息
     */
    void auditPosts(CommunityPostAuditDTO auditDTO);

    /**
     * 批量删除帖子
     *
     * @param ids 帖子ID列表
     */
    void deletePosts(List<Long> ids);

    /**
     * 切换置顶状态
     *
     * @param id 帖子ID
     */
    void toggleTop(Long id);

    /**
     * 切换热门状态
     *
     * @param id 帖子ID
     */
    void toggleHot(Long id);
}