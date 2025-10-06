package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.*;
import com.sf.zhimengjing.common.model.vo.ContentStatisticsVO;
import com.sf.zhimengjing.common.model.vo.ReportStatisticsVO;
import com.sf.zhimengjing.common.result.Result;
import com.sf.zhimengjing.entity.admin.AdminUser;
import com.sf.zhimengjing.entity.admin.CommunityComment;
import com.sf.zhimengjing.entity.admin.CommunityPost;
import com.sf.zhimengjing.entity.admin.ContentReport;
import com.sf.zhimengjing.service.admin.CommunityContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title: CommunityContentController
 * @Author: 殤枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @Description: 社区内容管理控制器，提供后台管理接口，包括帖子、评论、举报的分页查询、审核、删除、统计及导出功能。
 */
@RestController
@RequestMapping("/admin/community")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('community:manage')")
@Tag(name = "社区内容管理接口", description = "后台社区内容管理，包括帖子、评论、举报的管理、统计及导出")
public class CommunityContentController {

    private final CommunityContentService communityContentService;

    /**
     * 获取当前登录管理员ID
     */
    private Long getCurrentAdminId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AdminUser) {
            return ((AdminUser) principal).getId();
        }
        return 1L; // 测试或非安全上下文返回默认管理员ID
    }

    // ========================== 帖子管理 ==========================

    @GetMapping("/posts")
    @Operation(summary = "分页查询帖子列表", description = "根据条件分页查询帖子")
    @Log(module = "社区管理", operation = "查询帖子列表")
    public Result<IPage<CommunityPost>> getPostPage(PostQueryDTO queryDTO) {
        return Result.success(communityContentService.getPostPage(queryDTO));
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "获取帖子详情", description = "根据帖子ID获取帖子详细信息")
    @Log(module = "社区管理", operation = "获取帖子详情")
    public Result<CommunityPost> getPostDetail(@PathVariable Long id) {
        return Result.success(communityContentService.getPostDetail(id));
    }

    @PutMapping("/posts/audit")
    @Operation(summary = "批量审核帖子", description = "根据ID批量审核帖子，可传入拒绝原因")
    @Log(module = "社区管理", operation = "审核帖子")
    public Result<?> auditPosts(@Validated @RequestBody PostAuditDTO auditDTO) {
        communityContentService.batchAuditPosts(
                auditDTO.getPostIds(),
                auditDTO.getStatus(),
                auditDTO.getRejectReason(),
                getCurrentAdminId()
        );
        return Result.success();
    }

    @PutMapping("/posts/update")
    @Operation(summary = "更新帖子信息", description = "更新帖子标题、内容、分类等信息")
    @Log(module = "社区管理", operation = "更新帖子信息")
    public Result<?> updatePost(@Validated @RequestBody PostUpdateDTO updateDTO) {
        return Result.success(communityContentService.updatePost(updateDTO));
    }

    @DeleteMapping("/posts")
    @Operation(summary = "批量删除帖子", description = "根据帖子ID列表批量删除帖子")
    @Log(module = "社区管理", operation = "删除帖子")
    public Result<?> deletePosts(@RequestBody List<Long> postIds) {
        communityContentService.batchDeletePosts(postIds, getCurrentAdminId());
        return Result.success();
    }

    @PutMapping("/posts/toggle-top/{id}")
    @Operation(summary = "切换帖子置顶状态", description = "切换指定帖子置顶或取消置顶")
    @Log(module = "社区管理", operation = "切换帖子置顶")
    public Result<?> togglePostTop(@PathVariable Long id, @RequestParam Boolean isTop) {
        communityContentService.togglePostTop(id, isTop, getCurrentAdminId());
        return Result.success();
    }

    @PutMapping("/posts/toggle-hot/{id}")
    @Operation(summary = "切换帖子热门状态", description = "切换指定帖子热门或取消热门")
    @Log(module = "社区管理", operation = "切换帖子热门")
    public Result<?> togglePostHot(@PathVariable Long id, @RequestParam Boolean isHot) {
        communityContentService.togglePostHot(id, isHot, getCurrentAdminId());
        return Result.success();
    }

    // ========================== 评论管理 ==========================

    @GetMapping("/comments")
    @Operation(summary = "分页查询评论列表", description = "根据条件分页查询评论")
    @Log(module = "社区管理", operation = "查询评论列表")
    public Result<IPage<CommunityComment>> getCommentPage(CommentQueryDTO queryDTO) {
        return Result.success(communityContentService.getCommentPage(queryDTO));
    }

    @GetMapping("/comments/{id}")
    @Operation(summary = "获取评论详情", description = "根据评论ID获取评论详细信息")
    @Log(module = "社区管理", operation = "获取评论详情")
    public Result<CommunityComment> getCommentDetail(@PathVariable Long id) {
        return Result.success(communityContentService.getCommentDetail(id));
    }

    @PutMapping("/comments/audit")
    @Operation(summary = "批量审核评论", description = "根据ID批量审核评论，可传入拒绝原因")
    @Log(module = "社区管理", operation = "审核评论")
    public Result<?> auditComments(@Validated @RequestBody CommentAuditDTO auditDTO) {
        communityContentService.batchAuditComments(
                auditDTO.getCommentIds(),
                auditDTO.getStatus(),
                auditDTO.getRejectReason(),
                getCurrentAdminId()
        );
        return Result.success();
    }

    @DeleteMapping("/comments")
    @Operation(summary = "批量删除评论", description = "根据评论ID列表批量删除评论")
    @Log(module = "社区管理", operation = "删除评论")
    public Result<?> deleteComments(@RequestBody List<Long> commentIds) {
        communityContentService.batchDeleteComments(commentIds, getCurrentAdminId());
        return Result.success();
    }

    // ========================== 举报管理 ==========================

    @GetMapping("/reports")
    @Operation(summary = "分页查询举报列表", description = "根据条件分页查询举报")
    @Log(module = "社区管理", operation = "查询举报列表")
    public Result<IPage<ContentReport>> getReportPage(ReportQueryDTO queryDTO) {
        return Result.success(communityContentService.getReportPage(queryDTO));
    }

    @GetMapping("/reports/{id}")
    @Operation(summary = "获取举报详情", description = "根据举报ID获取举报详细信息")
    @Log(module = "社区管理", operation = "获取举报详情")
    public Result<ContentReport> getReportDetail(@PathVariable Long id) {
        return Result.success(communityContentService.getReportDetail(id));
    }

    @PutMapping("/reports/handle")
    @Operation(summary = "批量处理举报", description = "根据举报ID列表批量处理举报，可设置处理结果")
    @Log(module = "社区管理", operation = "处理举报")
    public Result<?> handleReports(@Validated @RequestBody ReportHandleDTO handleDTO) {
        communityContentService.batchHandleReports(
                handleDTO.getReportIds(),
                handleDTO.getStatus(),
                handleDTO.getHandleResult(),
                getCurrentAdminId()
        );
        return Result.success();
    }

    // ========================== 统计与导出 ==========================

    @GetMapping("/statistics/content")
    @Operation(summary = "获取内容统计数据", description = "获取帖子和评论数量等统计信息")
    @Log(module = "社区管理", operation = "获取内容统计")
    public Result<ContentStatisticsVO> getContentStatistics() {
        return Result.success(communityContentService.getContentStatistics());
    }

    @GetMapping("/statistics/report")
    @Operation(summary = "获取待处理统计数据", description = "获取待处理举报数量等统计信息")
    @Log(module = "社区管理", operation = "获取待处理统计")
    public Result<ReportStatisticsVO> getReportStatistics() {
        return Result.success(communityContentService.getReportStatistics());
    }

    @GetMapping("/export/posts")
    @Operation(summary = "导出帖子数据", description = "导出符合条件的帖子数据")
    @Log(module = "社区管理", operation = "导出帖子")
    public void exportPosts(PostQueryDTO queryDTO, HttpServletResponse response) {
        communityContentService.exportPosts(queryDTO, response);
    }

    @GetMapping("/export/comments")
    @Operation(summary = "导出评论数据", description = "导出符合条件的评论数据")
    @Log(module = "社区管理", operation = "导出评论")
    public void exportComments(CommentQueryDTO queryDTO, HttpServletResponse response) {
        communityContentService.exportComments(queryDTO, response);
    }

    @GetMapping("/export/reports")
    @Operation(summary = "导出举报数据", description = "导出符合条件的举报数据")
    @Log(module = "社区管理", operation = "导出举报")
    public void exportReports(ReportQueryDTO queryDTO, HttpServletResponse response) {
        communityContentService.exportReports(queryDTO, response);
    }
}
