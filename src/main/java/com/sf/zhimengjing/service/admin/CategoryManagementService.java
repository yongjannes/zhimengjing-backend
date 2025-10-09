package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.model.dto.CategoryDTO;
import com.sf.zhimengjing.common.model.dto.CategoryQueryDTO;
import com.sf.zhimengjing.common.model.vo.CategoryVO;
import com.sf.zhimengjing.common.model.vo.CategoryStatisticsVO;
import java.util.List;

/**
 * @Title: CategoryManagementService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @description: 梦境分类管理服务接口，提供分类的增删改查、树形结构操作、
 *               排序调整、状态切换及统计信息更新等功能。
 */
public interface CategoryManagementService {

    /**
     * 获取分页分类列表
     * @param queryDTO 查询条件
     * @return 分页分类列表
     */
    IPage<CategoryVO> getCategoryList(CategoryQueryDTO queryDTO);

    /**
     * 获取分类树形结构
     * @param maxDepth 树形最大深度
     * @return 树形分类列表
     */
    List<CategoryVO> getCategoryTree(Integer maxDepth);

    /**
     * 根据分类ID获取分类详情
     * @param categoryId 分类ID
     * @return 分类详情
     */
    CategoryVO getCategoryById(Long categoryId);

    /**
     * 创建分类
     * @param createDTO 分类创建信息
     * @param creatorId 创建人ID
     * @return 创建后的分类
     */
    CategoryVO createCategory(CategoryDTO createDTO, Long creatorId);

    /**
     * 更新分类
     * @param categoryId 分类ID
     * @param updateDTO 分类更新信息
     * @param updaterId 更新人ID
     * @return 更新后的分类
     */
    CategoryVO updateCategory(Long categoryId, CategoryDTO updateDTO, Long updaterId);
    /**
     * 删除一个或多个分类
     * @param categoryIds 分类ID列表
     */
    void deleteCategories(List<Long> categoryIds);

    /**
     * 切换分类启用/禁用状态
     * @param categoryId 分类ID
     * @param isActive 是否启用
     * @param operatorId 操作人ID
     * @return 更新后的分类
     */
    CategoryVO toggleCategoryStatus(Long categoryId, Boolean isActive, Long operatorId);

    /**
     * 移动分类到新的父分类
     * @param categoryId 分类ID
     * @param newParentId 新父分类ID
     * @param operatorId 操作人ID
     * @return 移动后的分类
     */
    CategoryVO moveCategory(Long categoryId, Integer newParentId, Long operatorId);

    /**
     * 批量更新分类排序
     * @param categoryIds 分类ID列表，顺序即排序顺序
     */
    void updateCategorySort(List<Long> categoryIds);

    /**
     * 获取指定父分类的子分类列表
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<CategoryVO> getSubCategories(Integer parentId);

    /**
     * 获取分类路径（从根到指定分类）
     * @param categoryId 分类ID
     * @return 分类路径列表
     */
    List<CategoryVO> getCategoryPath(Long categoryId);

    /**
     * 获取分类统计信息
     * @param categoryId 分类ID
     * @return 分类统计信息
     */
    CategoryStatisticsVO getCategoryStatistics(Long categoryId);

    /**
     * 更新分类统计信息
     * @param categoryId 分类ID
     */
    void updateCategoryStatistics(Long categoryId);

    /**
     * 批量更新分类统计信息
     * @param categoryIds 分类ID列表
     */
    void batchUpdateCategoryStatistics(List<Long> categoryIds);

    /**
     * 获取热门分类
     * @param limit 限制数量
     * @return 热门分类列表
     */
    List<CategoryVO> getPopularCategories(Integer limit);

    /**
     * 搜索分类
     * @param keyword 搜索关键词
     * @return 搜索结果分类列表
     */
    List<CategoryVO> searchCategories(String keyword);
}
