package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Title: EmotionAnalysisVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 情感分析 VO，用于展示文本或梦境的情绪分析结果，
 *               包括主要情感、情感分数、置信度、强度、分布及触发关键词。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "情感分析VO")
public class EmotionAnalysisVO {
    @Schema(description = "主要情感")
    private String primaryEmotion;

    @Schema(description = "情感分数")
    private BigDecimal emotionScore;

    @Schema(description = "置信度")
    private BigDecimal confidence;

    @Schema(description = "情感强度")
    private String intensity;

    @Schema(description = "情感分布")
    private Map<String, BigDecimal> emotionDistribution;

    @Schema(description = "触发关键词")
    private List<String> triggerKeywords;

    @Schema(description = "情感建议")
    private String suggestion;
}