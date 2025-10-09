package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.TagDTO;
import com.sf.zhimengjing.common.model.dto.TagMergeDTO;
import com.sf.zhimengjing.common.model.dto.TagQueryDTO;
import com.sf.zhimengjing.common.model.vo.TagVO;
import com.sf.zhimengjing.entity.admin.DreamTag;
import com.sf.zhimengjing.entity.admin.DreamTagRelation;
import com.sf.zhimengjing.mapper.admin.DreamTagMapper;
import com.sf.zhimengjing.mapper.admin.DreamTagRelationMapper;
import com.sf.zhimengjing.service.admin.TagManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: TagManagementServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @description: 梦境标签管理服务实现类
 */
@Service
@RequiredArgsConstructor
public class TagManagementServiceImpl implements TagManagementService {

    private final DreamTagMapper tagMapper;
    private final DreamTagRelationMapper tagRelationMapper;

    /**
     * 分页查询标签列表
     */
    @Override
    public IPage<TagVO> getTagList(TagQueryDTO queryDTO) {
        Page<DreamTag> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<DreamTag> queryWrapper = new LambdaQueryWrapper<>();

        // 模糊查询标签名称
        queryWrapper.like(StringUtils.hasText(queryDTO.getName()),
                DreamTag::getName, queryDTO.getName());

        // 状态筛选
        queryWrapper.eq(queryDTO.getIsActive() != null,
                DreamTag::getIsActive, queryDTO.getIsActive());

        // 使用次数范围筛选
        queryWrapper.ge(queryDTO.getMinUsageCount() != null,
                DreamTag::getUsageCount, queryDTO.getMinUsageCount());
        queryWrapper.le(queryDTO.getMaxUsageCount() != null,
                DreamTag::getUsageCount, queryDTO.getMaxUsageCount());

        // 排序
        if ("usageCount".equals(queryDTO.getSortField())) {
            if ("ASC".equalsIgnoreCase(queryDTO.getSortDirection())) {
                queryWrapper.orderByAsc(DreamTag::getUsageCount);
            } else {
                queryWrapper.orderByDesc(DreamTag::getUsageCount);
            }
        } else {
            queryWrapper.orderByDesc(DreamTag::getCreateTime);
        }

        IPage<DreamTag> tagPage = tagMapper.selectPage(page, queryWrapper);
        return tagPage.convert(this::convertToTagVO);
    }

    /**
     * 根据ID获取标签信息
     */
    @Override
    public TagVO getTagById(Long  tagId) {
        DreamTag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new GeneralBusinessException("标签不存在");
        }
        return convertToTagVO(tag);
    }

    /**
     * 创建标签
     */
    @Override
    @Transactional
    public TagVO createTag(TagDTO createDTO) {
        // 校验标签名称唯一性
        validateTagNameUnique(createDTO.getName(), null);

        DreamTag tag = new DreamTag();
        BeanUtils.copyProperties(createDTO, tag);
        tag.setUsageCount(0); // 初始化使用次数为0

        tagMapper.insert(tag);
        return convertToTagVO(tag);
    }

    /**
     * 更新标签
     */
    @Override
    @Transactional
    public TagVO updateTag(Long tagId, TagDTO updateDTO) {
        DreamTag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new GeneralBusinessException("标签不存在");
        }

        // 如果名称有变化，校验唯一性
        if (updateDTO.getName() != null && !updateDTO.getName().equals(tag.getName())) {
            validateTagNameUnique(updateDTO.getName(),  tagId);
        }

        BeanUtils.copyProperties(updateDTO, tag, getNullPropertyNames(updateDTO));
        tagMapper.updateById(tag);

        return convertToTagVO(tag);
    }

    /**
     * 删除一个或多个标签
     */
    @Override
    @Transactional
    public void deleteTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            throw new GeneralBusinessException("标签ID列表不能为空");
        }

        for (Long tagId : tagIds) {
            DreamTag tag = tagMapper.selectById(tagId);
            if (tag == null) {
                continue;
            }

            // 检查是否有梦境使用该标签
            Long count = tagRelationMapper.selectCount(
                    new LambdaQueryWrapper<DreamTagRelation>()
                            .eq(DreamTagRelation::getTagId, tagId)
            );

            if (count > 0) {
                throw new GeneralBusinessException(
                        String.format("标签 \"%s\" 正在被 %d 个梦境使用，无法删除", tag.getName(), count)
                );
            }

            tagMapper.deleteById(tagId);
        }
    }

    /**
     * 切换标签启用/禁用状态
     */
    @Override
    @Transactional
    public TagVO toggleTagStatus(Integer tagId, Boolean isActive) {
        DreamTag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new GeneralBusinessException("标签不存在");
        }

        tag.setIsActive(isActive);
        tagMapper.updateById(tag);

        return convertToTagVO(tag);
    }

    /**
     * 获取热门标签
     */
    @Override
    public List<TagVO> getPopularTags(Integer limit) {
        LambdaQueryWrapper<DreamTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DreamTag::getIsActive, true)
                .orderByDesc(DreamTag::getUsageCount)
                .last("LIMIT " + (limit != null ? limit : 10));

        List<DreamTag> tags = tagMapper.selectList(queryWrapper);
        return tags.stream()
                .map(this::convertToTagVO)
                .collect(Collectors.toList());
    }

    /**
     * 搜索标签
     */
    @Override
    public List<TagVO> searchTags(String keyword) {
        LambdaQueryWrapper<DreamTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(keyword), DreamTag::getName, keyword)
                .orderByDesc(DreamTag::getUsageCount);

        List<DreamTag> tags = tagMapper.selectList(queryWrapper);
        return tags.stream()
                .map(this::convertToTagVO)
                .collect(Collectors.toList());
    }

    /**
     * 合并标签
     */
    @Override
    @Transactional
    public void mergeTags(TagMergeDTO mergeDTO) {
        // 校验目标标签存在
        DreamTag targetTag = tagMapper.selectById(mergeDTO.getTargetTagId());
        if (targetTag == null) {
            throw new GeneralBusinessException("目标标签不存在");
        }

        // 校验源标签不包含目标标签
        if (mergeDTO.getSourceTagIds().contains(mergeDTO.getTargetTagId())) {
            throw new GeneralBusinessException("源标签列表不能包含目标标签");
        }

        // 将源标签的关联关系转移到目标标签
        for (Long sourceTagId : mergeDTO.getSourceTagIds()) {
            DreamTag sourceTag = tagMapper.selectById(sourceTagId);
            if (sourceTag == null) {
                continue;
            }

            // 查询源标签的所有关联
            List<DreamTagRelation> relations = tagRelationMapper.selectList(
                    new LambdaQueryWrapper<DreamTagRelation>()
                            .eq(DreamTagRelation::getTagId, sourceTagId)
            );

            // 更新关联到目标标签
            for (DreamTagRelation relation : relations) {
                // 检查目标标签是否已经关联该梦境
                Long existCount = tagRelationMapper.selectCount(
                        new LambdaQueryWrapper<DreamTagRelation>()
                                .eq(DreamTagRelation::getDreamId, relation.getDreamId())
                                .eq(DreamTagRelation::getTagId, mergeDTO.getTargetTagId())
                );

                if (existCount == 0) {
                    // 不存在则新增
                    relation.setTagId(mergeDTO.getTargetTagId());
                    tagRelationMapper.updateById(relation);
                } else {
                    // 已存在则删除源标签的关联
                    tagRelationMapper.deleteById(relation.getId());
                }
            }

            // 删除源标签
            tagMapper.deleteById(sourceTagId);
        }

        // 更新目标标签的使用次数
        updateTagUsageCount(mergeDTO.getTargetTagId());
    }

    /**
     * 更新标签使用次数统计
     */
    @Override
    @Transactional
    public void updateTagUsageCount(Long tagId) {
        Long count = tagRelationMapper.selectCount(
                new LambdaQueryWrapper<DreamTagRelation>()
                        .eq(DreamTagRelation::getTagId, tagId)
        );

        DreamTag tag = tagMapper.selectById(tagId);
        if (tag != null) {
            tag.setUsageCount(count.intValue());
            tagMapper.updateById(tag);
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 校验标签名称唯一性
     */
    private void validateTagNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<DreamTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DreamTag::getName, name);
        if (excludeId != null) {
            queryWrapper.ne(DreamTag::getId, excludeId);
        }

        Long count = tagMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new GeneralBusinessException("标签名称已存在");
        }
    }

    /**
     * 转换为 TagVO
     */
    private TagVO convertToTagVO(DreamTag tag) {
        TagVO vo = new TagVO();
        BeanUtils.copyProperties(tag, vo);
        return vo;
    }

    /**
     * 获取对象中为 null 的属性名称
     */
    private String[] getNullPropertyNames(Object source) {
        final org.springframework.beans.BeanWrapper src =
                new org.springframework.beans.BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        java.util.Set<String> emptyNames = new java.util.HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}