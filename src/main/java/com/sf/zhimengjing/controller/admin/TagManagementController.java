package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.TagDTO;
import com.sf.zhimengjing.common.model.dto.TagMergeDTO;
import com.sf.zhimengjing.common.model.dto.TagQueryDTO;
import com.sf.zhimengjing.common.model.vo.TagVO;
import com.sf.zhimengjing.common.result.Result;
import com.sf.zhimengjing.service.admin.TagManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title: TagManagementController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @Description: 标签管理控制器
 */
@RestController
@RequestMapping("/admin/tags")
@RequiredArgsConstructor
@Tag(name = "标签管理", description = "后台梦境标签管理相关接口")
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('dream:tag:manage')")
public class TagManagementController {

    private final TagManagementService tagService;

    @GetMapping
    @Operation(summary = "获取标签列表", description = "分页查询标签信息")
    @Log(module = "标签管理", operation = "查询列表")
    public Result<IPage<TagVO>> getTagList(@Valid TagQueryDTO queryDTO) {
        return Result.success(tagService.getTagList(queryDTO));
    }

    @GetMapping("/{tagId}")
    @Operation(summary = "获取标签详情", description = "根据ID获取标签详细信息")
    @Log(module = "标签管理", operation = "获取详情")
    public Result<TagVO> getTagById(
            @Parameter(description = "标签ID") @PathVariable Long  tagId) {
        return Result.success(tagService.getTagById(tagId));
    }

    @PostMapping
    @Operation(summary = "创建标签", description = "创建新的标签")
    @Log(module = "标签管理", operation = "创建标签")
    public Result<TagVO> createTag(@Valid @RequestBody TagDTO createDTO) {
        TagVO tag = tagService.createTag(createDTO);
        return Result.success(tag);
    }

    @PutMapping("/{tagId}")
    @Operation(summary = "更新标签", description = "更新标签信息")
    @Log(module = "标签管理", operation = "更新标签")
    public Result<String> updateTag(
            @Parameter(description = "标签ID") @PathVariable Long tagId,
            @Valid @RequestBody TagDTO updateDTO) {
        tagService.updateTag(tagId, updateDTO);
        return Result.success("更新成功");
    }

    @DeleteMapping
    @Operation(summary = "删除标签", description = "根据ID列表删除一个或多个标签")
    @Log(module = "标签管理", operation = "删除标签")
    public Result<String> deleteTags(
            @Parameter(description = "标签ID列表") @RequestParam("ids") List<Long> tagIds) {
        tagService.deleteTags(tagIds);
        return Result.success("删除成功");
    }

    @PutMapping("/{tagId}/status")
    @Operation(summary = "切换标签状态", description = "启用或禁用标签")
    @Log(module = "标签管理", operation = "切换状态")
    public Result<String> toggleTagStatus(
            @Parameter(description = "标签ID") @PathVariable Integer tagId,
            @Parameter(description = "是否启用") @RequestParam Boolean isActive) {
        tagService.toggleTagStatus(tagId, isActive);
        String message = isActive ? "启用成功" : "禁用成功";
        return Result.success(message);
    }

    @GetMapping("/popular")
    @Operation(summary = "获取热门标签", description = "获取使用次数最多的标签")
    @Log(module = "标签管理", operation = "获取热门标签")
    public Result<List<TagVO>> getPopularTags(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(tagService.getPopularTags(limit));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索标签", description = "根据关键词搜索标签")
    @Log(module = "标签管理", operation = "搜索标签")
    public Result<List<TagVO>> searchTags(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        return Result.success(tagService.searchTags(keyword));
    }

    @PostMapping("/merge")
    @Operation(summary = "合并标签", description = "将多个标签合并为一个标签")
    @Log(module = "标签管理", operation = "合并标签")
    public Result<String> mergeTags(@Valid @RequestBody TagMergeDTO mergeDTO) {
        tagService.mergeTags(mergeDTO);
        return Result.success("合并成功");
    }

    @PutMapping("/{tagId}/usage-count")
    @Operation(summary = "更新标签使用次数", description = "重新统计标签的使用次数")
    @Log(module = "标签管理", operation = "更新使用次数")
    public Result<String> updateTagUsageCount(
            @Parameter(description = "标签ID") @PathVariable Long tagId) {
        tagService.updateTagUsageCount(tagId);
        return Result.success("更新成功");
    }
}