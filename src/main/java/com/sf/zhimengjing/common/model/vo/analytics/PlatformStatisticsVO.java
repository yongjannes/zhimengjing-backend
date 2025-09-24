package com.sf.zhimengjing.common.model.vo.analytics;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

/**
 * @Title: PlatformStatisticsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 平台统计 VO，用于展示平台在指定日期的整体统计信息，
 *               包括用户统计、梦境统计、收入统计、性能指标及趋势分析。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "平台统计VO")
public class PlatformStatisticsVO {
    @Schema(description = "统计日期")
    private LocalDate statDate;

    @Schema(description = "用户统计")
    private UserStatsVO userStats;

    @Schema(description = "梦境统计")
    private DreamStatsVO dreamStats;

    @Schema(description = "收入统计")
    private RevenueStatsVO revenueStats;

    @Schema(description = "性能指标")
    private PerformanceMetricsVO performanceMetrics;

    @Schema(description = "趋势分析")
    private TrendAnalysisVO trendAnalysis;
}