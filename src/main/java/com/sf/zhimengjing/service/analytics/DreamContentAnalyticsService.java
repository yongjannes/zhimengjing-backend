package com.sf.zhimengjing.service.analytics;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.model.vo.analytics.DreamCategoryStatsVO;
import com.sf.zhimengjing.common.model.vo.analytics.DreamContentAnalysisVO;
import com.sf.zhimengjing.common.model.vo.analytics.EmotionAnalysisVO;
import com.sf.zhimengjing.common.model.vo.analytics.KeywordStatsVO;

import java.time.LocalDate;
import java.util.List;

/**
 * @Title: DreamContentAnalyticsService
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.analytics
 * @description: 梦境内容分析服务接口
 *               提供对用户提交的梦境内容进行情感分析、关键词提取、
 *               分类统计以及关键词统计等功能。
 */
public interface DreamContentAnalyticsService {
    /**
     * 分析梦境内容
     */
    DreamContentAnalysisVO analyzeDreamContent(Long dreamId, String content);

    /**
     * 获取情感分析结果
     */
    EmotionAnalysisVO getEmotionAnalysis(Long dreamId);

    /**
     * 提取关键词
     */
    List<String> extractKeywords(Long dreamId, String content);

    /**
     * 获取梦境分类统计
     */
    DreamCategoryStatsVO getDreamCategoryStats(LocalDate startDate, LocalDate endDate);

    /**
     * 获取关键词统计
     */
    IPage<KeywordStatsVO> getKeywordStats(LocalDate startDate, LocalDate endDate, Integer pageNum, Integer pageSize);
}