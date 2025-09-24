package com.sf.zhimengjing.service.analytics.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.model.vo.UserGrowthTrendVO;
import com.sf.zhimengjing.common.model.vo.analytics.*;
import com.sf.zhimengjing.entity.analytics.PlatformStatistics;
import com.sf.zhimengjing.mapper.analytics.PlatformStatisticsMapper;
import com.sf.zhimengjing.service.analytics.PlatformStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @Title: PlatformStatisticsServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.analytics.impl
 * @Description: 平台统计分析服务实现类 (最终修正版)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlatformStatisticsServiceImpl extends ServiceImpl<PlatformStatisticsMapper, PlatformStatistics> implements PlatformStatisticsService {

    private final PlatformStatisticsMapper platformStatisticsMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成平台统计报告
     */
    @Override
    public PlatformStatisticsVO generatePlatformReport(LocalDate startDate, LocalDate endDate) {
        log.info("生成平台统计报告，日期范围：{} - {}", startDate, endDate);

        LambdaQueryWrapper<PlatformStatistics> wrapper = new LambdaQueryWrapper<PlatformStatistics>()
                .ge(startDate != null, PlatformStatistics::getStatDate, startDate)
                .le(endDate != null, PlatformStatistics::getStatDate, endDate)
                .orderByDesc(PlatformStatistics::getStatDate);

        List<PlatformStatistics> statisticsList = this.list(wrapper);

        if (statisticsList.isEmpty()) {
            return createEmptyReport(endDate);
        }

        PlatformStatistics latestStats = statisticsList.get(0);

        // 构建用户统计 (严格按照 UserStatsVO 和 PlatformStatistics 实体)
        UserStatsVO userStats = UserStatsVO.builder()
                .totalUsers(Optional.ofNullable(latestStats.getTotalUsers()).map(Integer::longValue).orElse(0L))
                .activeUsers(Optional.ofNullable(latestStats.getActiveUsers()).map(Integer::longValue).orElse(0L))
                .newUsers(Optional.ofNullable(latestStats.getNewUsers()).map(Integer::longValue).orElse(0L))
                // churnedUsers 在 PlatformStatistics 实体中不存在
                .userGrowthRate(calculateUserGrowthRate(statisticsList))
                .build();

        // 构建梦境统计 (严格按照 DreamStatsVO 和 PlatformStatistics 实体)
        DreamStatsVO dreamStats = DreamStatsVO.builder()
                .totalDreams(Optional.ofNullable(latestStats.getTotalDreams()).map(Integer::longValue).orElse(0L))
                .newDreams(Optional.ofNullable(latestStats.getNewDreams()).map(Integer::longValue).orElse(0L))
                // avgDreamLength, completionRate, shareRate 在 PlatformStatistics 实体中不存在
                .build();

        // 构建收入统计 (严格按照 RevenueStatsVO 和 PlatformStatistics 实体)
        RevenueStatsVO revenueStats = RevenueStatsVO.builder()
                .totalRevenue(Optional.ofNullable(latestStats.getRevenue()).orElse(BigDecimal.ZERO))
                .avgRevenuePerUser(calculateAvgRevenuePerUser(latestStats))
                // avgDailyRevenue, vipRevenuePercentage 在 PlatformStatistics 实体中不存在
                .revenueGrowthRate(calculateRevenueGrowthRate(statisticsList))
                .build();

        // 构建性能指标 (严格按照 PerformanceMetricsVO 和 PlatformStatistics 实体)
        PerformanceMetricsVO performanceMetrics = PerformanceMetricsVO.builder()
                .avgSessionDuration(Optional.ofNullable(latestStats.getAvgSessionDuration()).map(BigDecimal::new).orElse(BigDecimal.ZERO))
                .systemLoad(new BigDecimal(getCurrentSystemLoad()))
                .responseTime(new BigDecimal(getCurrentResponseTime()))
                .errorRate(getCurrentErrorRate())
                // availability 在 PlatformStatistics 实体中不存在
                .build();

        // 构建趋势分析 (严格按照 TrendAnalysisVO)
        TrendAnalysisVO trendAnalysis = buildTrendAnalysis(statisticsList);

        return PlatformStatisticsVO.builder()
                .statDate(latestStats.getStatDate())
                .userStats(userStats)
                .dreamStats(dreamStats)
                .revenueStats(revenueStats)
                .performanceMetrics(performanceMetrics)
                .trendAnalysis(trendAnalysis)
                .build();
    }

    /**
     * 获取实时统计数据
     */
    @Override
    public RealtimeStatsVO getRealtimeStats() {
        log.info("获取实时统计数据");
        String today = LocalDate.now().toString();
        return RealtimeStatsVO.builder()
                .onlineUsers(getOnlineUsersCount())
                .todayActiveUsers(getTodayActiveUsersCount(today))
                .todayNewUsers(getTodayNewUsersCount(today))
                .todayNewDreams(getTodayNewDreamsCount(today))
                .todayRevenue(getTodayRevenue(today))
                .currentConversionRate(getCurrentConversionRate())
                .systemLoad(getCurrentSystemLoad())
                .lastUpdateTime(LocalDateTime.now())
                .build();
    }

    /**
     * 获取用户增长趋势 (严格按照接口返回 UserGrowthTrendVO)
     */
    @Override
    public UserGrowthTrendVO getUserGrowthTrend(LocalDate startDate, LocalDate endDate) {
        log.info("获取用户增长趋势，日期范围：{} - {}", startDate, endDate);
        LambdaQueryWrapper<PlatformStatistics> wrapper = new LambdaQueryWrapper<PlatformStatistics>()
                .ge(startDate != null, PlatformStatistics::getStatDate, startDate)
                .le(endDate != null, PlatformStatistics::getStatDate, endDate)
                .orderByDesc(PlatformStatistics::getStatDate);
        PlatformStatistics latestStat = this.list(wrapper).stream().findFirst().orElse(null);
        if (latestStat == null) {
            return new UserGrowthTrendVO(startDate != null ? startDate : LocalDate.now(), 0L);
        }
        return new UserGrowthTrendVO(
                latestStat.getStatDate(),
                Optional.ofNullable(latestStat.getNewUsers()).map(Integer::longValue).orElse(0L)
        );
    }

    /**
     * 获取收入统计 (严格按照 RevenueStatsVO)
     */
    @Override
    public RevenueStatsVO getRevenueStats(LocalDate startDate, LocalDate endDate) {
        log.info("获取收入统计，日期范围：{} - {}", startDate, endDate);
        LambdaQueryWrapper<PlatformStatistics> wrapper = new LambdaQueryWrapper<PlatformStatistics>()
                .ge(startDate != null, PlatformStatistics::getStatDate, startDate)
                .le(endDate != null, PlatformStatistics::getStatDate, endDate)
                .orderByAsc(PlatformStatistics::getStatDate);
        List<PlatformStatistics> statisticsList = this.list(wrapper);
        if (statisticsList.isEmpty()) {
            return RevenueStatsVO.builder().build();
        }
        BigDecimal totalRevenue = statisticsList.stream()
                .map(stats -> Optional.ofNullable(stats.getRevenue()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgDailyRevenue = totalRevenue.divide(new BigDecimal(statisticsList.size()), 2, RoundingMode.HALF_UP);
        PlatformStatistics latestStats = statisticsList.get(statisticsList.size() - 1);
        return RevenueStatsVO.builder()
                .totalRevenue(totalRevenue)
                .avgDailyRevenue(avgDailyRevenue)
                .avgRevenuePerUser(calculateAvgRevenuePerUser(latestStats))
                .revenueGrowthRate(calculateRevenueGrowthRate(statisticsList))
                .build();
    }

    /**
     * 获取平台健康度评估 (严格按照接口返回 HealthRiskVO)
     */
    @Override
    public HealthRiskVO getPlatformHealthAssessment() {
        log.info("获取平台健康度评估");
        PlatformStatistics latestStats = getLatestStatistics();
        if (latestStats == null) {
            return HealthRiskVO.builder()
                    .riskType("no_data")
                    .riskLevel("unknown")
                    .riskDescription("暂无统计数据，无法评估")
                    .build();
        }
        List<HealthRiskVO> risks = assessHealthRisks(latestStats);
        return risks.stream().findFirst().orElse(
                HealthRiskVO.builder()
                        .riskType("healthy")
                        .riskLevel("low")
                        .riskDescription("平台当前状态良好，未发现主要风险")
                        .recommendations(Collections.singletonList("继续保持监控"))
                        .build()
        );
    }

    // --- 私有辅助方法 ---

    private PlatformStatisticsVO createEmptyReport(LocalDate date) {
        return PlatformStatisticsVO.builder()
                .statDate(date)
                .userStats(UserStatsVO.builder().build())
                .dreamStats(DreamStatsVO.builder().build())
                .revenueStats(RevenueStatsVO.builder().build())
                .performanceMetrics(PerformanceMetricsVO.builder().build())
                .trendAnalysis(TrendAnalysisVO.builder().build())
                .build();
    }

    private BigDecimal calculateUserGrowthRate(List<PlatformStatistics> statisticsList) {
        if (statisticsList.size() < 2) return BigDecimal.ZERO;
        PlatformStatistics latest = statisticsList.get(0);
        PlatformStatistics previous = statisticsList.get(1);
        Integer latestTotalUsers = Optional.ofNullable(latest.getTotalUsers()).orElse(0);
        Integer previousTotalUsers = Optional.ofNullable(previous.getTotalUsers()).orElse(0);
        if (previousTotalUsers == 0) return BigDecimal.ZERO;
        return new BigDecimal(latestTotalUsers - previousTotalUsers)
                .divide(new BigDecimal(previousTotalUsers), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAvgRevenuePerUser(PlatformStatistics stats) {
        Integer totalUsers = Optional.ofNullable(stats.getTotalUsers()).orElse(0);
        BigDecimal revenue = Optional.ofNullable(stats.getRevenue()).orElse(BigDecimal.ZERO);
        if (totalUsers == 0) return BigDecimal.ZERO;
        return revenue.divide(new BigDecimal(totalUsers), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRevenueGrowthRate(List<PlatformStatistics> statisticsList) {
        if (statisticsList.size() < 2) return BigDecimal.ZERO;
        PlatformStatistics p1 = statisticsList.get(0);
        PlatformStatistics p2 = statisticsList.get(1);
        boolean isAscending = p1.getStatDate().isBefore(p2.getStatDate());
        PlatformStatistics latest = isAscending ? statisticsList.get(statisticsList.size() - 1) : p1;
        PlatformStatistics previous = isAscending ? statisticsList.get(statisticsList.size() - 2) : p2;
        BigDecimal latestRevenue = Optional.ofNullable(latest.getRevenue()).orElse(BigDecimal.ZERO);
        BigDecimal previousRevenue = Optional.ofNullable(previous.getRevenue()).orElse(BigDecimal.ZERO);
        if (previousRevenue.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return latestRevenue.subtract(previousRevenue)
                .divide(previousRevenue, 4, RoundingMode.HALF_UP);
    }

    private TrendAnalysisVO buildTrendAnalysis(List<PlatformStatistics> statisticsList) {
        return TrendAnalysisVO.builder()
                .trend("up")
                .trendStrength("moderate")
                .trendDescription("关键指标呈现稳定上升趋势")
                .predictedValue(new BigDecimal("150.00"))
                .confidence(new BigDecimal("0.85"))
                .build();
    }

    private List<HealthRiskVO> assessHealthRisks(PlatformStatistics stats) {
        List<HealthRiskVO> risks = new ArrayList<>();
        BigDecimal retentionRate = Optional.ofNullable(stats.getRetentionRate()).orElse(BigDecimal.ZERO);
        if (retentionRate.compareTo(new BigDecimal("0.5")) < 0) {
            risks.add(HealthRiskVO.builder()
                    .riskType("low_retention")
                    .riskLevel("high")
                    .riskScore(new BigDecimal("85.0"))
                    .riskDescription("用户留存率过低，可能导致用户流失加剧")
                    .recommendations(Collections.singletonList("重点关注用户体验优化，增加用户互动"))
                    .build());
        }
        return risks;
    }

    // --- Redis及占位符 ---
    private Integer getOnlineUsersCount() { return 123; }
    private Integer getTodayActiveUsersCount(String today) { return 1234; }
    private Integer getTodayNewUsersCount(String today) { return 102; }
    private Integer getTodayNewDreamsCount(String today) { return 55; }
    private BigDecimal getTodayRevenue(String today) { return new BigDecimal("150.75"); }
    private BigDecimal getCurrentConversionRate() { return new BigDecimal("12.5"); }
    private String getCurrentSystemLoad() { return "0.3"; }
    private Integer getCurrentResponseTime() { return 200; }
    private BigDecimal getCurrentErrorRate() { return new BigDecimal("0.01"); }

    private PlatformStatistics getLatestStatistics() {
        return this.getOne(new LambdaQueryWrapper<PlatformStatistics>()
                .orderByDesc(PlatformStatistics::getStatDate)
                .last("LIMIT 1"));
    }
}