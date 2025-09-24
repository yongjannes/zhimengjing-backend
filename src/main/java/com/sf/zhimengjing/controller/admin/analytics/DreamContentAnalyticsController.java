package com.sf.zhimengjing.controller.admin.analytics;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.analytics.DreamContentRequestDTO;
import com.sf.zhimengjing.common.model.vo.analytics.DreamCategoryStatsVO;
import com.sf.zhimengjing.common.model.vo.analytics.DreamContentAnalysisVO;
import com.sf.zhimengjing.common.model.vo.analytics.EmotionAnalysisVO;
import com.sf.zhimengjing.common.model.vo.analytics.KeywordStatsVO;
import com.sf.zhimengjing.service.analytics.DreamContentAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @Title: DreamContentAnalyticsController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin.analytics
 * @Description: 梦境内容分析控制器，提供梦境内容数据分析相关接口
 */
@RestController
@RequestMapping("/admin/analytics/dream-content")
@Tag(name = "梦境内容分析", description = "梦境内容数据分析相关接口")
@RequiredArgsConstructor
@Slf4j
@Validated
public class DreamContentAnalyticsController {

    private final DreamContentAnalyticsService dreamContentAnalyticsService;

    @PostMapping("/analyze/{dreamId}")
    @Operation(summary = "1. 分析梦境内容", description = "对指定梦境进行深度内容分析，包括情感分析、关键词提取、分类等")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public DreamContentAnalysisVO analyzeDreamContent(
            @Parameter(description = "梦境ID", required = true) @PathVariable @NotNull Long dreamId,
            @RequestBody @Validated DreamContentRequestDTO dto) {

        String content = dto.getContent();
        log.info("开始分析梦境内容，梦境ID：{}，内容长度：{}", dreamId, content.length());

        // 参数校验
        if (dreamId <= 0) {
            throw new GeneralBusinessException("梦境ID必须大于0");
        }
        if (content.trim().length() < 10) {
            throw new GeneralBusinessException("梦境内容长度不能少于10个字符");
        }
        if (content.length() > 10000) {
            throw new GeneralBusinessException("梦境内容长度不能超过10000个字符");
        }

        DreamContentAnalysisVO result = dreamContentAnalyticsService.analyzeDreamContent(dreamId, content);
        log.info("梦境内容分析完成，梦境ID：{}，主要情感：{}", dreamId, result.getEmotionAnalysis().getPrimaryEmotion());
        return result;
    }


    @GetMapping("/emotion/{dreamId}")
    @Operation(summary = "2. 获取情感分析结果", description = "获取指定梦境的详细情感分析结果")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public EmotionAnalysisVO getEmotionAnalysis(
            @Parameter(description = "梦境ID", required = true) @PathVariable @NotNull Long dreamId) {
        log.info("获取梦境情感分析结果，梦境ID：{}", dreamId);

        if (dreamId <= 0) {
            throw new GeneralBusinessException("梦境ID必须大于0");
        }

        EmotionAnalysisVO result = dreamContentAnalyticsService.getEmotionAnalysis(dreamId);
        log.info("获取情感分析结果成功，梦境ID：{}，情感：{}", dreamId, result.getPrimaryEmotion());
        return result;
    }

    @PostMapping("/extract-keywords/{dreamId}")
    @Operation(summary = "3. 提取梦境关键词", description = "从梦境内容中提取关键词")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<String> extractKeywords(
            @Parameter(description = "梦境ID", required = true) @PathVariable @NotNull Long dreamId,
            @Parameter(description = "梦境内容文本", required = true) @RequestBody DreamContentRequestDTO dto) {
        String content = dto.getContent();
        log.info("开始提取梦境关键词，梦境ID：{}", dreamId);

        if (dreamId <= 0) {
            throw new GeneralBusinessException("梦境ID必须大于0");
        }
        if (content.trim().length() < 5) {
            throw new GeneralBusinessException("内容长度不能少于5个字符");
        }

        List<String> keywords = dreamContentAnalyticsService.extractKeywords(dreamId, content);
        log.info("关键词提取完成，梦境ID：{}，关键词数量：{}", dreamId, keywords.size());
        return keywords;
    }

    @GetMapping("/category-stats")
    @Operation(summary = "4. 获取梦境分类统计", description = "获取指定时间范围内的梦境分类统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public DreamCategoryStatsVO getDreamCategoryStats(
            @Parameter(description = "开始日期", example = "2025-09-01", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-09-30", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        log.info("获取梦境分类统计，日期范围：{} - {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            throw new GeneralBusinessException("开始日期和结束日期不能为空");
        }
        if (startDate.isAfter(endDate)) {
            throw new GeneralBusinessException("开始日期不能晚于结束日期");
        }
        if (startDate.isBefore(LocalDate.now().minusYears(2))) {
            throw new GeneralBusinessException("查询时间范围不能超过两年");
        }

        DreamCategoryStatsVO result = dreamContentAnalyticsService.getDreamCategoryStats(startDate, endDate);
        log.info("梦境分类统计获取成功，分类数量：{}", result.getCategoryStats().size());
        return result;
    }

    @GetMapping("/keyword-stats")
    @Operation(summary = "5. 获取关键词统计", description = "获取指定时间范围内的关键词统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public IPage<KeywordStatsVO> getKeywordStats(
            @Parameter(description = "开始日期", example = "2025-09-01", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-09-30", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取关键词统计，日期范围：{} - {}，分页：{}/{}", startDate, endDate, current, size);

        if (startDate == null || endDate == null) {
            throw new GeneralBusinessException("开始日期和结束日期不能为空");
        }
        if (startDate.isAfter(endDate)) {
            throw new GeneralBusinessException("开始日期不能晚于结束日期");
        }
        if (current <= 0 || size <= 0) {
            throw new GeneralBusinessException("页码和页面大小必须大于0");
        }
        if (size > 100) {
            throw new GeneralBusinessException("单页数据量不能超过100条");
        }

        IPage<KeywordStatsVO> result = dreamContentAnalyticsService.getKeywordStats(startDate, endDate, current, size);
        log.info("关键词统计获取成功，总数：{}，当前页：{}", result.getTotal(), result.getCurrent());
        return result;
    }

    @GetMapping("/top-keywords")
    @Operation(summary = "6. 获取热门关键词", description = "获取指定时间范围内的热门关键词TOP榜")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<KeywordStatsVO> getTopKeywords(
            @Parameter(description = "开始日期", example = "2025-09-01")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期", example = "2025-09-30")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "返回数量", example = "10")
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取热门关键词，日期范围：{} - {}，数量：{}", startDate, endDate, limit);

        if (limit <= 0 || limit > 50) {
            throw new GeneralBusinessException("返回数量必须在1-50之间");
        }

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        IPage<KeywordStatsVO> result = dreamContentAnalyticsService.getKeywordStats(startDate, endDate, 1, limit);
        List<KeywordStatsVO> topKeywords = result.getRecords();

        log.info("热门关键词获取成功，返回{}个关键词", topKeywords.size());
        return topKeywords;
    }
}
