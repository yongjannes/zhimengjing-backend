package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.CategoryDTO;
import com.sf.zhimengjing.common.model.dto.CategoryQueryDTO;
import com.sf.zhimengjing.common.model.vo.CategoryStatisticsVO;
import com.sf.zhimengjing.common.model.vo.CategoryVO;
import com.sf.zhimengjing.common.util.BeanUtilsEx;
import com.sf.zhimengjing.entity.admin.DreamCategory;
import com.sf.zhimengjing.entity.admin.DreamCategoryRelation;
import com.sf.zhimengjing.entity.admin.DreamCategoryStatistics;
import com.sf.zhimengjing.entity.admin.DreamRecord;
import com.sf.zhimengjing.mapper.admin.DreamCategoryMapper;
import com.sf.zhimengjing.mapper.admin.DreamCategoryRelationMapper;
import com.sf.zhimengjing.mapper.admin.DreamCategoryStatisticsMapper;
import com.sf.zhimengjing.mapper.admin.DreamRecordMapper;
import com.sf.zhimengjing.service.admin.CategoryManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: CategoryManagementServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @description: 梦境分类管理服务实现类，实现 CategoryManagementService 接口，
 * 提供分类的增删改查、树形结构构建、排序调整、状态切换以及统计信息维护等功能。
 */
@Service
@RequiredArgsConstructor
public class CategoryManagementServiceImpl implements CategoryManagementService {

    private final DreamCategoryMapper categoryMapper;
    private final DreamCategoryRelationMapper relationMapper;
    private final DreamCategoryStatisticsMapper statisticsMapper;
    private final DreamRecordMapper recordMapper;

    /**
     * 分页查询分类列表
     */
    @Override
    public IPage<CategoryVO> getCategoryList(CategoryQueryDTO queryDTO) {
        Page<DreamCategory> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        LambdaQueryWrapper<DreamCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(queryDTO.getName()), DreamCategory::getName, queryDTO.getName());
        queryWrapper.eq(queryDTO.getIsActive() != null, DreamCategory::getIsActive, queryDTO.getIsActive());
        queryWrapper.orderByAsc(DreamCategory::getSortOrder).orderByDesc(DreamCategory::getCreateTime);

        IPage<DreamCategory> categoryPage = categoryMapper.selectPage(page, queryWrapper);
        return categoryPage.convert(this::convertToCategoryVO);
    }

    /**
     * 构建分类树形结构
     */
    @Override
    public List<CategoryVO> getCategoryTree(Integer maxDepth) {
        LambdaQueryWrapper<DreamCategory> queryWrapper = new LambdaQueryWrapper<DreamCategory>()
                .eq(DreamCategory::getIsActive, true)
                .orderByAsc(DreamCategory::getSortOrder);
        List<DreamCategory> allCategories = categoryMapper.selectList(queryWrapper);
        return buildTree(allCategories, 0, 1, maxDepth);
    }

    /**
     * 根据ID获取分类信息
     */
    @Override
    public CategoryVO getCategoryById(Long categoryId) {
        DreamCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new GeneralBusinessException("分类不存在");
        }
        return convertToCategoryVO(category);
    }

    /**
     * 创建分类
     */
    @Override
    @Transactional
    public CategoryVO createCategory(CategoryDTO createDTO, Long creatorId) {
        validateCategoryNameUnique(createDTO.getName(), null);
        DreamCategory category = new DreamCategory();
        BeanUtils.copyProperties(createDTO, category);
        category.setCreatedBy(creatorId);
        category.setUpdatedBy(creatorId);
        category.setLevel(calculateCategoryLevel(category.getParentId()));
        categoryMapper.insert(category);

        createCategoryRelations(category);

        return convertToCategoryVO(category);
    }

    /**
     * 更新分类
     */
    @Override
    @Transactional
    public CategoryVO updateCategory(Long categoryId, CategoryDTO updateDTO, Long updaterId) {
        // 1. 查询现有分类
        DreamCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new GeneralBusinessException("分类不存在");
        }

        // 2. 校验分类名称唯一性
        validateCategoryNameUnique(updateDTO.getName(), categoryId);

        // 3. 拷贝 DTO 中非空字段到实体，保证数据库其他字段不被覆盖
        org.springframework.beans.BeanUtils.copyProperties(
                updateDTO,
                category,
                BeanUtilsEx.getNullPropertyNames(updateDTO)
        );

        // 4. 设置更新人
        category.setUpdatedBy(updaterId);

        // 5. 更新数据库
        categoryMapper.updateById(category);

        // 6. 返回 VO 对象
        return convertToCategoryVO(category);
    }

    /**
     * 删除分类
     */
    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        if (categoryMapper.selectCount(new LambdaQueryWrapper<DreamCategory>().eq(DreamCategory::getParentId, categoryId)) > 0) {
            throw new GeneralBusinessException("该分类下存在子分类，无法删除");
        }
        categoryMapper.deleteById(categoryId);
        relationMapper.delete(new LambdaQueryWrapper<DreamCategoryRelation>().eq(DreamCategoryRelation::getDescendantId, categoryId));
    }

    // ---------------- 已实现的方法 ----------------

    @Override
    @Transactional
    public void batchDeleteCategories(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new GeneralBusinessException("分类ID列表不能为空");
        }
        for (Long categoryId : categoryIds) {
            deleteCategory(categoryId);
        }
    }

    @Override
    @Transactional
    public CategoryVO toggleCategoryStatus(Long categoryId, Boolean isActive, Long operatorId) {
        DreamCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new GeneralBusinessException("分类不存在");
        }
        category.setIsActive(isActive);
        category.setUpdatedBy(operatorId);
        categoryMapper.updateById(category);
        return convertToCategoryVO(category);
    }

    @Override
    @Transactional
    public CategoryVO moveCategory(Long categoryId, Integer newParentId, Long operatorId) {
        DreamCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new GeneralBusinessException("分类不存在");
        }

        // 不允许移动到自身或其子分类下
        List<DreamCategoryRelation> descendantRelations = relationMapper.selectList(
                new LambdaQueryWrapper<DreamCategoryRelation>().eq(DreamCategoryRelation::getAncestorId, category.getId())
        );
        List<Integer> descendantIds = descendantRelations.stream()
                .map(DreamCategoryRelation::getDescendantId)
                .collect(Collectors.toList());
        if (descendantIds.contains(newParentId)) {
            throw new GeneralBusinessException("不能将分类移动到其子分类下");
        }

        // 删除旧的关系
        relationMapper.delete(new LambdaQueryWrapper<DreamCategoryRelation>().eq(DreamCategoryRelation::getDescendantId, categoryId));
        // 更新父ID和层级
        category.setParentId(newParentId);
        category.setLevel(calculateCategoryLevel(newParentId));
        category.setUpdatedBy(operatorId);
        categoryMapper.updateById(category);
        // 创建新的关系
        createCategoryRelations(category);

        return convertToCategoryVO(category);
    }

    @Override
    @Transactional
    public void updateCategorySort(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        for (int i = 0; i < categoryIds.size(); i++) {
            DreamCategory category = new DreamCategory();
            category.setId((long) Math.toIntExact(categoryIds.get(i)));
            category.setSortOrder(i);
            categoryMapper.updateById(category);
        }
    }

    @Override
    public List<CategoryVO> getSubCategories(Integer parentId) {
        LambdaQueryWrapper<DreamCategory> queryWrapper = new LambdaQueryWrapper<DreamCategory>()
                .eq(DreamCategory::getParentId, parentId)
                .orderByAsc(DreamCategory::getSortOrder);
        List<DreamCategory> categories = categoryMapper.selectList(queryWrapper);
        return categories.stream().map(this::convertToCategoryVO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getCategoryPath(Long categoryId) {
        List<CategoryVO> path = new ArrayList<>();
        DreamCategory current = categoryMapper.selectById(categoryId);
        while (current != null) {
            path.add(convertToCategoryVO(current));
            if (current.getParentId() == 0) {
                break;
            }
            current = categoryMapper.selectById(current.getParentId());
        }
        Collections.reverse(path);
        return path;
    }

    @Override
    public CategoryStatisticsVO getCategoryStatistics(Long categoryId) {
        // 查询统计信息
        DreamCategoryStatistics statistics = statisticsMapper.selectOne(
                new LambdaQueryWrapper<DreamCategoryStatistics>().eq(DreamCategoryStatistics::getCategoryId, categoryId)
        );

        // 如果没有找到，返回空或者默认对象
        if (statistics == null) {
            return new CategoryStatisticsVO();
        }

        // 将查询结果映射到 VO
        CategoryStatisticsVO vo = new CategoryStatisticsVO();
        // 手动赋值方式：
        vo.setId(statistics.getId());
        vo.setCategoryId(statistics.getCategoryId());
        vo.setTotalDreams(statistics.getTotalDreams());
        vo.setPublicDreams(statistics.getPublicDreams());
        vo.setPrivateDreams(statistics.getPrivateDreams());
        vo.setApprovedDreams(statistics.getApprovedDreams());
        vo.setPendingDreams(statistics.getPendingDreams());
        vo.setRejectedDreams(statistics.getRejectedDreams());
        vo.setAvgSleepQuality(statistics.getAvgSleepQuality());
        vo.setAvgLucidityLevel(statistics.getAvgLucidityLevel());
        vo.setLastCalculated(statistics.getLastCalculated());

        return vo;
    }


    @Override
    @Transactional
    public void updateCategoryStatistics(Long categoryId) {
        // 1. 查询该分类下的所有梦境
        List<DreamRecord> dreamRecords = recordMapper.selectList(
                new LambdaQueryWrapper<DreamRecord>()
                        .eq(DreamRecord::getCategoryId, categoryId)
                        .eq(DreamRecord::getDeleteFlag, 0) // 仅统计未删除的
        );

        // 2. 初始化统计数据
        int totalDreams = dreamRecords.size();
        int publicDreams = 0;
        int approvedDreams = 0;
        int pendingDreams = 0;
        int rejectedDreams = 0;
        double totalSleepQuality = 0;
        int sleepQualityCount = 0;
        double totalLucidityLevel = 0;
        int lucidityLevelCount = 0;

        // 3. 遍历计算
        for (DreamRecord dream : dreamRecords) {
            if (dream.getIsPublic() == 1) publicDreams++;
            switch (dream.getStatus()) {
                case 3: approvedDreams++; break;
                case 2: pendingDreams++; break;
                case 4: rejectedDreams++; break;
            }
            if (dream.getSleepQuality() != null) {
                totalSleepQuality += dream.getSleepQuality();
                sleepQualityCount++;
            }
            if (dream.getLucidityLevel() != null) {
                totalLucidityLevel += dream.getLucidityLevel();
                lucidityLevelCount++;
            }
        }

        // 4. 计算平均值
        BigDecimal avgSleepQuality = (sleepQualityCount > 0)
                ? new BigDecimal(totalSleepQuality / sleepQualityCount).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal avgLucidityLevel = (lucidityLevelCount > 0)
                ? new BigDecimal(totalLucidityLevel / lucidityLevelCount).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 5. 查询或创建统计实体
        DreamCategoryStatistics statistics = statisticsMapper.selectOne(
                new LambdaQueryWrapper<DreamCategoryStatistics>().eq(DreamCategoryStatistics::getCategoryId, categoryId)
        );
        if (statistics == null) {
            statistics = new DreamCategoryStatistics();
            statistics.setCategoryId((long) Math.toIntExact(categoryId));
        }

        // 6. 更新统计数据
        statistics.setTotalDreams(totalDreams);
        statistics.setPublicDreams(publicDreams);
        statistics.setPrivateDreams(totalDreams - publicDreams);
        statistics.setApprovedDreams(approvedDreams);
        statistics.setPendingDreams(pendingDreams);
        statistics.setRejectedDreams(rejectedDreams);
        statistics.setAvgSleepQuality(avgSleepQuality);
        statistics.setAvgLucidityLevel(avgLucidityLevel);
        statistics.setLastCalculated(LocalDateTime.now());

        // 7. 保存或更新
        if (statistics.getId() == null) {
            statisticsMapper.insert(statistics);
        } else {
            statisticsMapper.updateById(statistics);
        }
    }

    @Override
    public void batchUpdateCategoryStatistics(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        for (Long categoryId : categoryIds) {
            updateCategoryStatistics(categoryId);
        }
    }

    @Override
    public List<CategoryVO> getPopularCategories(Integer limit) {
        LambdaQueryWrapper<DreamCategory> queryWrapper = new LambdaQueryWrapper<DreamCategory>()
                .orderByDesc(DreamCategory::getDreamCount)
                .last("LIMIT " + limit);
        List<DreamCategory> categories = categoryMapper.selectList(queryWrapper);
        return categories.stream().map(this::convertToCategoryVO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> searchCategories(String keyword) {
        LambdaQueryWrapper<DreamCategory> queryWrapper = new LambdaQueryWrapper<DreamCategory>()
                .like(DreamCategory::getName, keyword)
                .or()
                .like(DreamCategory::getDescription, keyword);
        List<DreamCategory> categories = categoryMapper.selectList(queryWrapper);
        return categories.stream().map(this::convertToCategoryVO).collect(Collectors.toList());
    }


    // ---------------- 私有辅助方法 ----------------

    /**
     * 校验分类名称唯一性
     */
    private void validateCategoryNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<DreamCategory> queryWrapper = new LambdaQueryWrapper<DreamCategory>()
                .eq(DreamCategory::getName, name);
        if(excludeId != null) {
            queryWrapper.ne(DreamCategory::getId, excludeId);
        }
        if (categoryMapper.selectCount(queryWrapper) > 0) {
            throw new GeneralBusinessException("分类名称已存在: " + name);
        }
    }

    /**
     * 计算分类层级
     */
    private int calculateCategoryLevel(Integer parentId) {
        if (parentId == null || parentId == 0) {
            return 1;
        }
        DreamCategory parent = categoryMapper.selectById(parentId);
        if (parent == null) {
            throw new GeneralBusinessException("父分类不存在");
        }
        return parent.getLevel() + 1;
    }

    /**
     * 创建分类关系（祖先-后代）
     */
    private void createCategoryRelations(DreamCategory category) {
        DreamCategoryRelation selfRelation = new DreamCategoryRelation();
        selfRelation.setAncestorId(Math.toIntExact(category.getId()));
        selfRelation.setDescendantId(Math.toIntExact(category.getId()));
        selfRelation.setDistance(0);
        relationMapper.insert(selfRelation);

        if (category.getParentId() != null && category.getParentId() > 0) {
            List<DreamCategoryRelation> parentRelations = relationMapper.selectList(
                    new LambdaQueryWrapper<DreamCategoryRelation>().eq(DreamCategoryRelation::getDescendantId, category.getParentId())
            );
            for (DreamCategoryRelation parentRelation : parentRelations) {
                DreamCategoryRelation newRelation = new DreamCategoryRelation();
                newRelation.setAncestorId(parentRelation.getAncestorId());
                newRelation.setDescendantId(Math.toIntExact(category.getId()));
                newRelation.setDistance(parentRelation.getDistance() + 1);
                relationMapper.insert(newRelation);
            }
        }
    }

    /**
     * 构建树形结构
     */
    private List<CategoryVO> buildTree(List<DreamCategory> all, int parentId, int currentDepth, Integer maxDepth) {
        if (maxDepth != null && currentDepth > maxDepth) {
            return null;
        }
        return all.stream()
                .filter(c -> c.getParentId() == parentId)
                .map(c -> {
                    CategoryVO vo = convertToCategoryVO(c);
                    vo.setChildren(buildTree(all, Math.toIntExact(c.getId()), currentDepth + 1, maxDepth));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 转换实体到VO
     */
    private CategoryVO convertToCategoryVO(DreamCategory category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
}