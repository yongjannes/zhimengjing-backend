package com.sf.zhimengjing.service.analytics;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.model.dto.analytics.UserBehaviorAnalyticsDTO;
import com.sf.zhimengjing.common.model.vo.analytics.ConversionFunnelVO;
import com.sf.zhimengjing.common.model.vo.analytics.UserActivityAnalysisVO;
import com.sf.zhimengjing.common.model.vo.analytics.UserBehaviorStatsVO;
import com.sf.zhimengjing.common.model.vo.analytics.UserRetentionAnalysisVO;
import com.sf.zhimengjing.entity.analytics.UserBehavior;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: UserBehaviorAnalyticsService
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.analytics
 * @Description: 用户行为分析服务接口，提供用户行为统计、活跃度分析、留存分析及转化漏斗分析等功能
 */
public interface UserBehaviorAnalyticsService {
    /**
     * 获取用户行为统计
     */
    IPage<UserBehaviorStatsVO> getUserBehaviorStats(UserBehaviorAnalyticsDTO dto);

    /**
     * 获取用户活跃度分析
     */
    UserActivityAnalysisVO getUserActivityAnalysis(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户留存率分析
     */
    UserRetentionAnalysisVO getUserRetentionAnalysis(LocalDate startDate, LocalDate endDate);

    /**
     * 获取用户转化漏斗分析
     */
    ConversionFunnelVO getConversionFunnelAnalysis(String funnelType, LocalDate startDate, LocalDate endDate);

    /**
     * 批量保存用户行为数据
     */
    void batchSaveUserBehavior(List<UserBehavior> behaviors);
}