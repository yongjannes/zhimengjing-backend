package com.sf.zhimengjing.controller.admin.analytics;

import com.sf.zhimengjing.common.model.vo.UserGrowthTrendVO;
import com.sf.zhimengjing.common.model.vo.analytics.HealthRiskVO;
import com.sf.zhimengjing.common.model.vo.analytics.PlatformStatisticsVO;
import com.sf.zhimengjing.common.model.vo.analytics.RealtimeStatsVO;
import com.sf.zhimengjing.common.model.vo.analytics.RevenueStatsVO;
import com.sf.zhimengjing.service.analytics.PlatformStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * @Title: PlatformStatisticsController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin.analytics
 * @Description: 平台统计分析控制器 (已移除Result包装，适配ResultAdvice)
 */
@Tag(name = "平台统计分析")
@RestController
@RequestMapping("/admin/analytics/platform")
@RequiredArgsConstructor
public class PlatformStatisticsController {

    private final PlatformStatisticsService platformStatisticsService;

    @Operation(summary = "1. 获取平台总体统计报告")
    @GetMapping("/report")
    public PlatformStatisticsVO getPlatformReport(
            @Parameter(description = "开始日期 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return platformStatisticsService.generatePlatformReport(startDate, endDate);
    }

    @Operation(summary = "2. 获取实时统计数据")
    @GetMapping("/realtime")
    public RealtimeStatsVO getRealtimeStats() {
        return platformStatisticsService.getRealtimeStats();
    }

    @Operation(summary = "3. 获取用户增长趋势")
    @GetMapping("/user-growth-trend")
    public UserGrowthTrendVO getUserGrowthTrend(
            @Parameter(description = "开始日期 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return platformStatisticsService.getUserGrowthTrend(startDate, endDate);
    }

    @Operation(summary = "4. 获取收入统计")
    @GetMapping("/revenue-stats")
    public RevenueStatsVO getRevenueStats(
            @Parameter(description = "开始日期 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return platformStatisticsService.getRevenueStats(startDate, endDate);
    }

    @Operation(summary = "5. 获取平台健康度评估")
    @GetMapping("/health-assessment")
    public HealthRiskVO getPlatformHealthAssessment() {
        return platformStatisticsService.getPlatformHealthAssessment();
    }
}