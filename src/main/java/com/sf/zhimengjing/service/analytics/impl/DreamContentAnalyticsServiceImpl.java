package com.sf.zhimengjing.service.analytics.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.vo.analytics.*;
import com.sf.zhimengjing.common.util.SecurityUtils;
import com.sf.zhimengjing.entity.analytics.DreamContentAnalytics;
import com.sf.zhimengjing.mapper.analytics.DreamContentAnalyticsMapper;
import com.sf.zhimengjing.service.analytics.DreamContentAnalyticsService;
import com.sf.zhimengjing.service.analytics.MLModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Title: DreamContentAnalyticsServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.analytics.impl
 * @Description: 梦境内容分析服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DreamContentAnalyticsServiceImpl extends ServiceImpl<DreamContentAnalyticsMapper, DreamContentAnalytics> implements DreamContentAnalyticsService {

    private final DreamContentAnalyticsMapper dreamContentMapper;
    private final MLModelService mlModelService;

    /**
     * 分析梦境内容
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DreamContentAnalysisVO analyzeDreamContent(Long dreamId, String content) {
        log.info("开始分析梦境内容，梦境ID：{}，内容长度：{}", dreamId, content.length());

        try {
            // 执行情感分析
            EmotionAnalysisVO emotionAnalysis = performEmotionAnalysis(content);

            // 提取关键词
            List<String> keywords = extractKeywordsFromContent(content);

            // 进行内容分类
            List<String> categories = classifyDreamContent(content, keywords);

            // 生成内容统计
            ContentStatsVO contentStats = generateContentStats(content);

            // 推荐相似梦境
            List<SimilarDreamVO> similarDreams = findSimilarDreams(dreamId, keywords, categories);

            // 保存分析结果
            saveDreamContentAnalysis(dreamId,content, emotionAnalysis, keywords, categories, contentStats);

            return DreamContentAnalysisVO.builder()
                    .dreamId(dreamId)
                    .emotionAnalysis(emotionAnalysis)
                    .keywords(keywords)
                    .categories(categories)
                    .contentStats(contentStats)
                    .similarDreams(similarDreams)
                    .build();

        } catch (Exception e) {
            log.error("梦境内容分析失败，梦境ID：{}", dreamId, e);
            throw new GeneralBusinessException("梦境内容分析失败，请稍后重试");
        }
    }

    /**
     * 获取情感分析结果
     */
    @Override
    public EmotionAnalysisVO getEmotionAnalysis(Long dreamId) {
        log.info("获取梦境情感分析结果，梦境ID：{}", dreamId);

        DreamContentAnalytics dreamContent = this.getById(dreamId);
        if (dreamContent == null) {
            throw new RuntimeException("梦境内容不存在");
        }

        return EmotionAnalysisVO.builder()
                .primaryEmotion(dreamContent.getEmotionLabel())
                .emotionScore(dreamContent.getEmotionScore())
                .confidence(new BigDecimal("0.85")) // 从ML模型获取
                .intensity(getEmotionIntensity(dreamContent.getEmotionScore()))
                .emotionDistribution(parseEmotionDistribution(dreamContent.getKeywordList()))
                .triggerKeywords(parseKeywords(dreamContent.getKeywordList()))
                .suggestion(generateEmotionSuggestion(dreamContent.getEmotionLabel(), dreamContent.getEmotionScore()))
                .build();
    }

    /**
     * 提取关键词
     */
    @Override
    public List<String> extractKeywords(Long dreamId, String content) {
        log.info("开始提取梦境关键词，梦境ID：{}", dreamId);

        // 使用NLP模型提取关键词
        Map<String, Object> inputData = Map.of("content", content);

        try {
            // 调用关键词提取模型
            PredictionResultVO result = mlModelService.predict(getKeywordExtractionModelId(), inputData);

            @SuppressWarnings("unchecked")
            List<String> keywords = (List<String>) result.getPredictionResult().get("keywords");

            return keywords != null ? keywords : Collections.emptyList();

        } catch (Exception e) {
            log.error("关键词提取失败，使用备用方法", e);
            return extractKeywordsFallback(content);
        }
    }

    /**
     * 获取梦境分类统计
     */
    @Override
    public DreamCategoryStatsVO getDreamCategoryStats(LocalDate startDate, LocalDate endDate) {
        log.info("获取梦境分类统计，日期范围：{} - {}", startDate, endDate);

        // 查询指定时间范围内的梦境内容
        LambdaQueryWrapper<DreamContentAnalytics> wrapper = new LambdaQueryWrapper<DreamContentAnalytics>()
                .ge(startDate != null, DreamContentAnalytics::getCreatedDate, startDate)
                .le(endDate != null, DreamContentAnalytics::getCreatedDate, endDate);

        List<DreamContentAnalytics> dreamContents = this.list(wrapper);

        // 统计分类数据
        Map<String, List<DreamContentAnalytics>> categoryGroups = dreamContents.stream()
                .filter(content -> content.getCategoryList() != null)
                .flatMap(content -> parseCategories(content.getCategoryList()).stream()
                        .map(category -> new AbstractMap.SimpleEntry<>(category, content)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        // 构建统计结果
        List<DreamCategoryStatsVO.CategoryStatsItemVO> categoryStats = categoryGroups.entrySet().stream()
                .map(entry -> {
                    String categoryName = entry.getKey();
                    List<DreamContentAnalytics> contents = entry.getValue();

                    Set<Long> uniqueUsers = contents.stream()
                            .map(DreamContentAnalytics::getUserId)
                            .collect(Collectors.toSet());

                    BigDecimal avgEmotionScore = contents.stream()
                            .filter(c -> c.getEmotionScore() != null)
                            .map(DreamContentAnalytics::getEmotionScore)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(new BigDecimal(contents.size()), 2, BigDecimal.ROUND_HALF_UP);

                    BigDecimal percentage = dreamContents.size() > 0 ?
                            new BigDecimal(contents.size()).divide(new BigDecimal(dreamContents.size()), 4, BigDecimal.ROUND_HALF_UP) :
                            BigDecimal.ZERO;

                    return DreamCategoryStatsVO.CategoryStatsItemVO.builder()
                            .categoryName(categoryName)
                            .dreamCount((long) contents.size())
                            .userCount((long) uniqueUsers.size())
                            .avgEmotionScore(avgEmotionScore)
                            .percentage(percentage)
                            .trendDirection(calculateTrendDirection(categoryName, startDate, endDate))
                            .build();
                })
                .sorted(Comparator.comparing(DreamCategoryStatsVO.CategoryStatsItemVO::getDreamCount).reversed())
                .collect(Collectors.toList());

        List<String> topCategories = categoryStats.stream()
                .limit(10)
                .map(DreamCategoryStatsVO.CategoryStatsItemVO::getCategoryName)
                .collect(Collectors.toList());

        return DreamCategoryStatsVO.builder()
                .categoryStats(categoryStats)
                .trendAnalysis(buildCategoryTrendAnalysis(categoryStats))
                .topCategories(topCategories)
                .build();
    }

    /**
     * 获取关键词统计
     */
    @Override
    public IPage<KeywordStatsVO> getKeywordStats(LocalDate startDate, LocalDate endDate, Integer pageNum, Integer pageSize) {
        log.info("获取关键词统计，日期范围：{} - {}，分页参数：{}/{}", startDate, endDate, pageNum, pageSize);

        // 查询梦境内容数据
        LambdaQueryWrapper<DreamContentAnalytics> wrapper = new LambdaQueryWrapper<DreamContentAnalytics>()
                .ge(startDate != null, DreamContentAnalytics::getCreatedDate, startDate)
                .le(endDate != null, DreamContentAnalytics::getCreatedDate, endDate);

        List<DreamContentAnalytics> dreamContents = this.list(wrapper);

        // 统计关键词频率
        Map<String, List<DreamContentAnalytics>> keywordGroups = dreamContents.stream()
                .filter(content -> content.getKeywordList() != null)
                .flatMap(content -> parseKeywords(content.getKeywordList()).stream()
                        .map(keyword -> new AbstractMap.SimpleEntry<>(keyword, content)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        // 构建关键词统计
        List<KeywordStatsVO> keywordStats = keywordGroups.entrySet().stream()
                .map(entry -> {
                    String keyword = entry.getKey();
                    List<DreamContentAnalytics> contents = entry.getValue();

                    Set<Long> uniqueUsers = contents.stream()
                            .map(DreamContentAnalytics::getUserId)
                            .collect(Collectors.toSet());

                    BigDecimal avgEmotionScore = contents.stream()
                            .filter(c -> c.getEmotionScore() != null)
                            .map(DreamContentAnalytics::getEmotionScore)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(new BigDecimal(contents.size()), 2, BigDecimal.ROUND_HALF_UP);

                    String category = getMostCommonCategory(contents);
                    List<Integer> trendData = calculateKeywordTrend(keyword, startDate, endDate);
                    List<String> relatedKeywords = findRelatedKeywords(keyword, keywordGroups.keySet());

                    return KeywordStatsVO.builder()
                            .keyword(keyword)
                            .frequency((long) contents.size())
                            .userCount((long) uniqueUsers.size())
                            .avgEmotionScore(avgEmotionScore)
                            .category(category)
                            .trendData(trendData)
                            .relatedKeywords(relatedKeywords)
                            .build();
                })
                .sorted(Comparator.comparing(KeywordStatsVO::getFrequency).reversed())
                .collect(Collectors.toList());

        // 分页处理
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, keywordStats.size());
        List<KeywordStatsVO> pageData = keywordStats.subList(start, end);

        // 创建分页对象并返回
        Page<KeywordStatsVO> page = new Page<>(pageNum, pageSize, keywordStats.size());
        page.setRecords(pageData);

        return page;
    }

    // 私有辅助方法实现
    private EmotionAnalysisVO performEmotionAnalysis(String content) {
        // 调用情感分析模型
        Map<String, Object> inputData = Map.of("content", content);

        try {
            PredictionResultVO result = mlModelService.predict(getEmotionAnalysisModelId(), inputData);
            Map<String, Object> prediction = result.getPredictionResult();

            return EmotionAnalysisVO.builder()
                    .primaryEmotion((String) prediction.get("emotion"))
                    .emotionScore(new BigDecimal(prediction.get("score").toString()))
                    .confidence(result.getConfidenceScore())
                    .intensity(getEmotionIntensity(new BigDecimal(prediction.get("score").toString())))
                    .emotionDistribution((Map<String, BigDecimal>) prediction.get("distribution"))
                    .triggerKeywords((List<String>) prediction.get("keywords"))
                    .suggestion(generateEmotionSuggestion((String) prediction.get("emotion"), new BigDecimal(prediction.get("score").toString())))
                    .build();

        } catch (Exception e) {
            log.error("情感分析失败", e);
            return createDefaultEmotionAnalysis();
        }
    }

    private List<String> extractKeywordsFromContent(String content) {
        // 关键词提取逻辑
        return Arrays.asList("示例关键词1", "示例关键词2");
    }

    private List<String> classifyDreamContent(String content, List<String> keywords) {
        // 内容分类逻辑
        return Arrays.asList("自然", "积极");
    }

    private ContentStatsVO generateContentStats(String content) {
        return ContentStatsVO.builder()
                .contentLength(content.length())
                .wordCount(content.split("\\s+").length)
                .sentenceCount(content.split("[.!?]").length)
                .build();
    }

    private List<SimilarDreamVO> findSimilarDreams(Long dreamId, List<String> keywords, List<String> categories) {
        // 查找相似梦境的逻辑
        return new ArrayList<>();
    }

    private void saveDreamContentAnalysis(Long dreamId,String content, EmotionAnalysisVO emotion, List<String> keywords, List<String> categories, ContentStatsVO stats) {
        // 保存分析结果到数据库
        DreamContentAnalytics entity = new DreamContentAnalytics();

        entity.setUserId(SecurityUtils.getUserId());
        entity.setUserId(dreamId);
        entity.setContentText(content);
        entity.setEmotionScore(emotion.getEmotionScore());
        entity.setEmotionLabel(emotion.getPrimaryEmotion());
        entity.setKeywordList("[\"" + String.join("\",\"", keywords) + "\"]");
        entity.setCategoryList("[\"" + String.join("\",\"", categories) + "\"]");

        entity.setContentLength(stats.getContentLength());
        entity.setCreatedDate(LocalDate.now());

        this.saveOrUpdate(entity);
    }

    // 其他辅助方法...
    private Long getEmotionAnalysisModelId() { return 1L; }
    private Long getKeywordExtractionModelId() { return 2L; }

    private String getEmotionIntensity(BigDecimal score) {
        if (score.abs().compareTo(new BigDecimal("0.8")) >= 0) return "very_high";
        if (score.abs().compareTo(new BigDecimal("0.6")) >= 0) return "high";
        if (score.abs().compareTo(new BigDecimal("0.4")) >= 0) return "medium";
        if (score.abs().compareTo(new BigDecimal("0.2")) >= 0) return "low";
        return "very_low";
    }

    private Map<String, BigDecimal> parseEmotionDistribution(String keywordList) {
        return Map.of("positive", new BigDecimal("0.7"), "negative", new BigDecimal("0.2"), "neutral", new BigDecimal("0.1"));
    }

    private List<String> parseKeywords(String keywordList) {
        return keywordList != null ? Arrays.asList(keywordList.split(",")) : Collections.emptyList();
    }

    private List<String> parseCategories(String categoryList) {
        return categoryList != null ? Arrays.asList(categoryList.split(",")) : Collections.emptyList();
    }

    private String generateEmotionSuggestion(String emotion, BigDecimal score) {
        if ("positive".equals(emotion)) {
            return "您的梦境充满积极情感，建议继续保持乐观心态";
        } else if ("negative".equals(emotion)) {
            return "梦境中存在一些负面情感，建议多关注心理健康";
        }
        return "梦境情感较为平和，保持当前的生活状态";
    }

    private EmotionAnalysisVO createDefaultEmotionAnalysis() {
        return EmotionAnalysisVO.builder()
                .primaryEmotion("neutral")
                .emotionScore(BigDecimal.ZERO)
                .confidence(new BigDecimal("0.5"))
                .intensity("medium")
                .emotionDistribution(Map.of("neutral", BigDecimal.ONE))
                .triggerKeywords(Collections.emptyList())
                .suggestion("暂无分析结果")
                .build();
    }

    private List<String> extractKeywordsFallback(String content) {
        // 简单的关键词提取备用方法
        return Arrays.asList(content.split("\\s+")).stream()
                .filter(word -> word.length() > 2)
                .limit(10)
                .collect(Collectors.toList());
    }

    private String calculateTrendDirection(String category, LocalDate startDate, LocalDate endDate) {
        // 计算趋势方向
        return "stable";
    }

    private List<CategoryTrendVO> buildCategoryTrendAnalysis(List<DreamCategoryStatsVO.CategoryStatsItemVO> categoryStats) {
        return new ArrayList<>();
    }

    private String getMostCommonCategory(List<DreamContentAnalytics> contents) {
        return "通用";
    }

    private List<Integer> calculateKeywordTrend(String keyword, LocalDate startDate, LocalDate endDate) {
        return Arrays.asList(10, 12, 15, 18, 20, 22, 25);
    }

    private List<String> findRelatedKeywords(String keyword, Set<String> allKeywords) {
        return allKeywords.stream()
                .filter(k -> !k.equals(keyword))
                .limit(5)
                .collect(Collectors.toList());
    }
}