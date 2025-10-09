package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.model.dto.TagDTO;
import com.sf.zhimengjing.common.model.dto.TagMergeDTO;
import com.sf.zhimengjing.common.model.dto.TagQueryDTO;
import com.sf.zhimengjing.common.model.vo.TagVO;

import java.util.List;

/**
 * @Title: TagManagementService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @description: 梦境标签管理服务接口
 */
public interface TagManagementService {

    /**
     * 获取分页标签列表
     * @param queryDTO 查询条件
     * @return 分页标签列表
     */
    IPage<TagVO> getTagList(TagQueryDTO queryDTO);

    /**
     * 根据标签ID获取标签详情
     * @param tagId 标签ID
     * @return 标签详情
     */
    TagVO getTagById(Long  tagId);

    /**
     * 创建标签
     * @param createDTO 标签创建信息
     * @return 创建后的标签
     */
    TagVO createTag(TagDTO createDTO);

    /**
     * 更新标签
     * @param tagId 标签ID
     * @param updateDTO 标签更新信息
     * @return 更新后的标签
     */
    TagVO updateTag(Long tagId, TagDTO updateDTO);

    /**
     * 删除一个或多个标签
     * @param tagIds 标签ID列表
     */
    void deleteTags(List<Long> tagIds);

    /**
     * 切换标签启用/禁用状态
     * @param tagId 标签ID
     * @param isActive 是否启用
     * @return 更新后的标签
     */
    TagVO toggleTagStatus(Integer tagId, Boolean isActive);

    /**
     * 获取热门标签
     * @param limit 限制数量
     * @return 热门标签列表
     */
    List<TagVO> getPopularTags(Integer limit);

    /**
     * 搜索标签
     * @param keyword 搜索关键词
     * @return 搜索结果标签列表
     */
    List<TagVO> searchTags(String keyword);

    /**
     * 合并标签
     * @param mergeDTO 合并信息
     */
    void mergeTags(TagMergeDTO mergeDTO);

    /**
     * 更新标签使用次数统计
     * @param tagId 标签ID
     */
    void updateTagUsageCount(Long tagId);
}