package com.sf.zhimengjing.controller.admin.analytics;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.analytics.UserBehaviorAnalyticsDTO;
import com.sf.zhimengjing.common.model.vo.analytics.ConversionFunnelVO;
import com.sf.zhimengjing.common.model.vo.analytics.UserActivityAnalysisVO;
import com.sf.zhimengjing.common.model.vo.analytics.UserBehaviorStatsVO;
import com.sf.zhimengjing.common.model.vo.analytics.UserRetentionAnalysisVO;
import com.sf.zhimengjing.entity.analytics.UserBehavior;
import com.sf.zhimengjing.service.analytics.UserBehaviorAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: UserBehaviorAnalyticsController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin.analytics
 * @Description: 用户行为分析控制器，提供用户行为数据分析相关接口
 */
@RestController
@RequestMapping("/admin/analytics/user-behavior")
@Tag(name = "用户行为分析", description = "用户行为数据分析相关接口")
@RequiredArgsConstructor
@Slf4j
@Validated
@PreAuthorize("hasAuthority('ops:stats:view')")
public class UserBehaviorAnalyticsController {

    private final UserBehaviorAnalyticsService userBehaviorAnalyticsService;

    @GetMapping("/stats")
    @Operation(summary = "1. 获取用户行为统计", description = "根据条件查询用户行为统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public IPage<UserBehaviorStatsVO> getUserBehaviorStats(
            @Valid @ModelAttribute UserBehaviorAnalyticsDTO dto) {
        log.info("获取用户行为统计，参数：{}", dto);
        IPage<UserBehaviorStatsVO> result = userBehaviorAnalyticsService.getUserBehaviorStats(dto);
        log.info("用户行为统计查询成功，返回{}条记录", result.getTotal());
        return result;
    }

    @GetMapping("/activity/{userId}")
    @Operation(summary = "2. 获取用户活跃度分析", description = "分析指定用户在指定时间范围内的活跃度")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public UserActivityAnalysisVO getUserActivityAnalysis(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "开始时间", example = "2025-09-01 00:00:00")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间", example = "2025-09-30 23:59:59")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        log.info("获取用户活跃度分析，用户ID：{}，时间范围：{} - {}", userId, startTime, endTime);

        if (userId == null || userId <= 0) {
            throw new GeneralBusinessException("用户ID不能为空且必须大于0");
        }

        UserActivityAnalysisVO result = userBehaviorAnalyticsService.getUserActivityAnalysis(userId, startTime, endTime);
        log.info("用户活跃度分析完成，用户{}活跃度等级：{}", userId, result.getActivityLevel());
        return result;
    }

    @GetMapping("/retention")
    @Operation(summary = "3. 获取用户留存率分析", description = "分析指定时间范围内的用户留存率")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public UserRetentionAnalysisVO getUserRetentionAnalysis(
            @Parameter(description = "开始日期", example = "2025-09-01", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-09-30", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        log.info("获取用户留存率分析，日期范围：{} - {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            throw new GeneralBusinessException("开始日期和结束日期不能为空");
        }
        if (startDate.isAfter(endDate)) {
            throw new GeneralBusinessException("开始日期不能晚于结束日期");
        }
        if (startDate.isBefore(LocalDate.now().minusYears(1))) {
            throw new GeneralBusinessException("查询时间范围不能超过一年");
        }

        UserRetentionAnalysisVO result = userBehaviorAnalyticsService.getUserRetentionAnalysis(startDate, endDate);
        log.info("用户留存率分析完成，次日留存率：{}%", result.getDayOneRetention());
        return result;
    }

    @GetMapping("/conversion-funnel")
    @Operation(summary = "4. 获取转化漏斗分析", description = "分析指定类型的转化漏斗数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ConversionFunnelVO getConversionFunnelAnalysis(
            @Parameter(description = "漏斗类型", example = "vip_conversion", required = true)
            @RequestParam String funnelType,
            @Parameter(description = "开始日期", example = "2025-09-01", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-09-30", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        log.info("获取转化漏斗分析，类型：{}，日期范围：{} - {}", funnelType, startDate, endDate);

        if (funnelType == null || funnelType.trim().isEmpty()) {
            throw new GeneralBusinessException("漏斗类型不能为空");
        }
        if (startDate == null || endDate == null) {
            throw new GeneralBusinessException("开始日期和结束日期不能为空");
        }
        if (startDate.isAfter(endDate)) {
            throw new GeneralBusinessException("开始日期不能晚于结束日期");
        }

        ConversionFunnelVO result = userBehaviorAnalyticsService.getConversionFunnelAnalysis(funnelType, startDate, endDate);
        log.info("转化漏斗分析完成，总体转化率：{}%", result.getOverallConversionRate());
        return result;
    }

    @PostMapping("/batch-save")
    @Operation(summary = "5. 批量保存用户行为数据", description = "批量保存用户行为数据，用于数据导入")
    @PreAuthorize("hasRole('ADMIN')")
    public String batchSaveUserBehavior(
            @Parameter(description = "用户行为数据列表") @RequestBody @Valid List<UserBehavior> behaviors) {

        log.info("开始批量保存用户行为数据，数量：{}", behaviors.size());

        if (behaviors == null || behaviors.isEmpty()) {
            throw new GeneralBusinessException("用户行为数据不能为空");
        }
        if (behaviors.size() > 1000) {
            throw new GeneralBusinessException("单次批量保存数据不能超过1000条");
        }

        userBehaviorAnalyticsService.batchSaveUserBehavior(behaviors);
        log.info("批量保存用户行为数据成功，数量：{}", behaviors.size());
        return "批量保存用户行为数据成功";
    }
}