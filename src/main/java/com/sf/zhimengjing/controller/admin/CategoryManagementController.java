package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.CategoryDTO;
import com.sf.zhimengjing.common.model.dto.CategoryQueryDTO;
import com.sf.zhimengjing.common.model.vo.CategoryVO;
import com.sf.zhimengjing.common.model.vo.CategoryStatisticsVO;
import com.sf.zhimengjing.common.result.Result;
import com.sf.zhimengjing.service.admin.CategoryManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title: CategoryManagementController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @Description: 分类管理控制器，提供后台梦境分类的增删改查、树形结构操作、
 *               状态切换、排序及统计等接口
 */
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Tag(name = "分类管理", description = "后台梦境分类管理相关接口")
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('category:manage')")
public class CategoryManagementController {

    private final CategoryManagementService categoryService;

    @GetMapping
    @Operation(summary = "获取分类列表", description = "分页查询分类信息")
    @Log(module = "分类管理", operation = "查询列表")
    public Result<IPage<CategoryVO>> getCategoryList(@Valid CategoryQueryDTO queryDTO) {
        return Result.success(categoryService.getCategoryList(queryDTO));
    }

    @GetMapping("/tree")
    @Operation(summary = "获取分类树", description = "获取分类的树形结构")
    @Log(module = "分类管理", operation = "获取分类树")
    public Result<List<CategoryVO>> getCategoryTree(
            @Parameter(description = "最大深度") @RequestParam(required = false) Integer maxDepth) {
        return Result.success(categoryService.getCategoryTree(maxDepth));
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "获取分类详情", description = "根据ID获取分类详细信息")
    @Log(module = "分类管理", operation = "获取详情")
    public Result<CategoryVO> getCategoryById(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        return Result.success(categoryService.getCategoryById(categoryId));
    }

    @PostMapping
    @Operation(summary = "创建分类", description = "创建新的分类")
    @Log(module = "分类管理", operation = "创建分类")
    public Result<CategoryVO> createCategory(@Valid @RequestBody CategoryDTO createDTO) {
        Long creatorId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        CategoryVO category = categoryService.createCategory(createDTO, creatorId);
        return Result.success(category);
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "更新分类", description = "更新分类信息")
    @Log(module = "分类管理", operation = "更新分类")
    public Result<String> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Valid @RequestBody CategoryDTO updateDTO) {

        // 获取当前操作用户ID
        Long updaterId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        // 调用服务更新分类
        categoryService.updateCategory(categoryId, updateDTO, updaterId);

        // 返回成功消息字符串
        return Result.success("更新成功");
    }


    @DeleteMapping
    @Operation(summary = "删除分类", description = "根据ID列表删除一个或多个分类")
    @Log(module = "分类管理", operation = "删除分类")
    public Result<String> deleteCategories(
            @Parameter(description = "分类ID列表，可包含单个或多个ID") @RequestParam("ids") List<Long> categoryIds) {
        categoryService.deleteCategories(categoryIds);
        return Result.success("删除成功");
    }
    @PutMapping("/{categoryId}/status")
    @Operation(summary = "切换分类状态", description = "启用或禁用分类")
    @Log(module = "分类管理", operation = "切换状态")
    public Result<String> toggleCategoryStatus(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Parameter(description = "是否启用") @RequestParam Boolean isActive) {

        // 获取当前操作用户ID
        Long operatorId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        // 调用服务切换分类状态
        categoryService.toggleCategoryStatus(categoryId, isActive, operatorId);

        // 根据状态返回不同的提示消息
        String message = isActive ? "启用成功" : "禁用成功";

        return Result.success(message);
    }


    @PutMapping("/{categoryId}/move")
    @Operation(summary = "移动分类", description = "移动分类到新的父分类下")
    @Log(module = "分类管理", operation = "移动分类")
    public Result<String> moveCategory(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Parameter(description = "新父分类ID") @RequestParam Integer newParentId) {

        // 获取当前操作用户ID
        Long operatorId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        // 调用服务移动分类
        categoryService.moveCategory(categoryId, newParentId, operatorId);

        // 返回成功消息字符串
        return Result.success("移动成功");
    }


    @PutMapping("/sort")
    @Operation(summary = "更新分类排序", description = "批量更新分类排序")
    @Log(module = "分类管理", operation = "更新排序")
    public Result<String> updateCategorySort(
            @Parameter(description = "分类ID列表") @RequestBody List<Long> categoryIds) {
        categoryService.updateCategorySort(categoryIds);
        return Result.success("排序更新成功");
    }

    @GetMapping("/{parentId}/children")
    @Operation(summary = "获取子分类", description = "获取指定父分类下的子分类")
    @Log(module = "分类管理", operation = "获取子分类")
    public Result<List<CategoryVO>> getSubCategories(
            @Parameter(description = "父分类ID") @PathVariable Integer parentId) {
        return Result.success(categoryService.getSubCategories(parentId));
    }

    @GetMapping("/{categoryId}/path")
    @Operation(summary = "获取分类路径", description = "获取分类的完整路径")
    @Log(module = "分类管理", operation = "获取路径")
    public Result<List<CategoryVO>> getCategoryPath(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        return Result.success(categoryService.getCategoryPath(categoryId));
    }

    @GetMapping("/{categoryId}/statistics")
    @Operation(summary = "获取分类统计", description = "获取分类的统计信息")
    @Log(module = "分类管理", operation = "获取统计")
    public Result<CategoryStatisticsVO> getCategoryStatistics(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        return Result.success(categoryService.getCategoryStatistics(categoryId));
    }

    @PutMapping("/{categoryId}/statistics")
    @Operation(summary = "更新分类统计", description = "更新分类的统计信息")
    @Log(module = "分类管理", operation = "更新统计")
    public Result<String> updateCategoryStatistics(
            @Parameter(description = "分类ID") @PathVariable Long categoryId) {
        categoryService.updateCategoryStatistics(categoryId);
        return Result.success("统计更新成功");
    }

    @GetMapping("/popular")
    @Operation(summary = "获取热门分类", description = "获取梦境数量最多的分类")
    @Log(module = "分类管理", operation = "获取热门分类")
    public Result<List<CategoryVO>> getPopularCategories(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(categoryService.getPopularCategories(limit));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索分类", description = "根据关键词搜索分类")
    @Log(module = "分类管理", operation = "搜索分类")
    public Result<List<CategoryVO>> searchCategories(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        return Result.success(categoryService.searchCategories(keyword));
    }
}
