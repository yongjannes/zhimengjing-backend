package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.model.dto.ReportReviewDTO;
import com.sf.zhimengjing.common.result.Result;
import com.sf.zhimengjing.entity.admin.ReportReview;
import com.sf.zhimengjing.service.admin.ReportReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * @Title: ReportReviewController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @description: 报告审核管理控制器，提供待审核报告列表、审核详情、提交审核、批量分配任务、
 *              申诉管理、审核统计及自动审核等接口
 */
@RestController
@RequestMapping("/admin/report/review")
@RequiredArgsConstructor
@Tag(name = "报告审核管理接口")
@PreAuthorize("hasAuthority('ops:report:view')")
public class ReportReviewController {

    private final ReportReviewService reportReviewService;

    /** 获取待审核报告列表 */
    @GetMapping("/pending")
    @Operation(summary = "1. 获取待审核报告列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    public Result<IPage<ReportReviewDTO>> getPendingReviews(
            @Parameter(description = "页码，默认为1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页数量，默认为10") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "指定审核员ID，可选") @RequestParam(required = false) Long reviewerId) {

        Page<ReportReviewDTO> page = new Page<>(current, size);
        return Result.success(reportReviewService.getPendingReviews(page, reviewerId));
    }

    /** 获取单条审核记录详情 */
    @GetMapping("/{reviewId}")
    @Operation(summary = "2. 获取审核详情")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    public Result<ReportReviewDTO> getReviewDetail(
            @Parameter(description = "审核记录ID") @PathVariable Long reviewId) {
        return Result.success(reportReviewService.getReviewDetail(reviewId));
    }

    /** 提交审核结果 */
    @PostMapping("/submit")
    @Operation(summary = "3. 提交审核结果")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    public Result<Boolean> submitReview(
            @Parameter(description = "审核请求数据") @Validated @RequestBody ReportReviewDTO.ReviewRequestDTO requestDTO) {

        Long reviewerId = getCurrentUserId();
        return Result.success(reportReviewService.submitReview(requestDTO, reviewerId));
    }

    /** 批量分配审核任务 */
    @PostMapping("/assign")
    @Operation(summary = "4. 批量分配审核任务")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> assignReviewTasks(
            @Parameter(description = "批量分配任务请求数据") @Validated @RequestBody ReportReviewDTO.BatchAssignDTO assignDTO) {

        Long operatorId = getCurrentUserId();
        return Result.success(reportReviewService.assignReviewTasks(assignDTO, operatorId));
    }

    /** 提交申诉 */
    @PostMapping("/{reviewId}/appeal")
    @Operation(summary = "5. 提交申诉")
    public Result<Boolean> submitAppeal(
            @Parameter(description = "审核记录ID") @PathVariable Long reviewId,
            @Parameter(description = "申诉原因") @RequestParam String appealReason) {

        Long userId = 201L; // 模拟申诉用户ID
        return Result.success(reportReviewService.submitAppeal(reviewId, appealReason, userId));
    }

    /** 处理申诉 */
    @PutMapping("/{reviewId}/appeal/handle")
    @Operation(summary = "6. 处理申诉")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> handleAppeal(
            @Parameter(description = "审核记录ID") @PathVariable Long reviewId,
            @Parameter(description = "申诉处理结果") @RequestParam String appealResult,
            @Parameter(description = "处理意见") @RequestParam String comment) {

        Long handlerId = getCurrentUserId();
        return Result.success(reportReviewService.handleAppeal(reviewId, appealResult, comment, handlerId));
    }

    /** 获取审核统计信息 */
    @GetMapping("/stats")
    @Operation(summary = "7. 获取审核统计")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    public Result<ReportReviewDTO.ReviewStatsVO> getReviewStats(
            @Parameter(description = "审核员ID，可选") @RequestParam(required = false) Long reviewerId,
            @Parameter(description = "开始日期，可选") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期，可选") @RequestParam(required = false) LocalDate endDate) {

        return Result.success(reportReviewService.getReviewStats(reviewerId, startDate, endDate));
    }

    /** 触发自动审核检测 */
    @PostMapping("/auto-check/{reportId}")
    @Operation(summary = "8. 触发自动审核检测")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ReportReview> autoReviewCheck(
            @Parameter(description = "报告ID") @PathVariable Long reportId) {

        // Service 层现在会在异常情况下抛出错误，由 GlobalExceptionHandler 处理
        // 成功时会返回更新后的 review 对象
        ReportReview updatedReview = reportReviewService.autoReviewCheck(reportId);
        return Result.success(updatedReview);
    }

    /** 获取当前审核员的审核任务列表 */
    @GetMapping("/my-reviews")
    @Operation(summary = "9. 获取我的审核任务")
    @PreAuthorize("hasRole('REVIEWER')")
    public Result<IPage<ReportReviewDTO>> getMyReviews(
            @Parameter(description = "页码，默认为1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页数量，默认为10") @RequestParam(defaultValue = "10") long size) {

        Long reviewerId = getCurrentUserId();
        Page<ReportReviewDTO> page = new Page<>(current, size);
        return Result.success(reportReviewService.getMyReviews(page, reviewerId));
    }

    /** 根据报告ID获取审核记录 */
    @GetMapping("/by-report/{reportId}")
    @Operation(summary = "10. 根据报告ID获取审核记录")
    public Result<ReportReviewDTO> getReviewByReportId(
            @Parameter(description = "报告ID") @PathVariable Long reportId) {

        return Result.success(reportReviewService.getReviewByReportId(reportId));
    }

    /** 获取当前登录用户ID (模拟) */
    private Long getCurrentUserId() {
        // 实际项目中可通过 SecurityContextHolder 获取登录用户ID
        return 501L;
    }
}
