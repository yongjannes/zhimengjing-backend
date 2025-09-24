package com.sf.zhimengjing.service.analytics;

import com.sf.zhimengjing.common.model.vo.UserGrowthTrendVO;
import com.sf.zhimengjing.common.model.vo.analytics.HealthRiskVO;
import com.sf.zhimengjing.common.model.vo.analytics.PlatformStatisticsVO;
import com.sf.zhimengjing.common.model.vo.analytics.RealtimeStatsVO;
import com.sf.zhimengjing.common.model.vo.analytics.RevenueStatsVO;

import java.time.LocalDate;

/**
 * @Title: PlatformStatisticsService
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.analytics
 * @description: 平台统计服务接口
 *               提供平台级的数据统计、用户增长趋势、收入统计、
 *               实时数据以及平台健康度评估功能。
 */
public interface PlatformStatisticsService {
    /**
     * 生成平台统计报告
     */
    PlatformStatisticsVO generatePlatformReport(LocalDate startDate, LocalDate endDate);

    /**
     * 获取实时统计数据
     */
    RealtimeStatsVO getRealtimeStats();

    /**
     * 获取用户增长趋势
     */
    UserGrowthTrendVO getUserGrowthTrend(LocalDate startDate, LocalDate endDate);

    /**
     * 获取收入统计
     */
    RevenueStatsVO getRevenueStats(LocalDate startDate, LocalDate endDate);

    /**
     * 获取平台健康度评估
     */
    HealthRiskVO getPlatformHealthAssessment();
}