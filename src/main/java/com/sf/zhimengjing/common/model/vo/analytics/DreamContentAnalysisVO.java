package com.sf.zhimengjing.common.model.vo.analytics;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @Title: DreamContentAnalysisVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 梦境内容分析 VO，用于展示梦境的多维度分析结果，
 *               包括情感分析、关键词、分类、内容统计及相似梦境推荐。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "梦境内容分析VO")
public class DreamContentAnalysisVO {
    @Schema(description = "梦境ID")
    private Long dreamId;

    @Schema(description = "情感分析结果")
    private EmotionAnalysisVO emotionAnalysis;

    @Schema(description = "关键词列表")
    private List<String> keywords;

    @Schema(description = "分类结果")
    private List<String> categories;

    @Schema(description = "内容统计")
    private ContentStatsVO contentStats;

    @Schema(description = "相似梦境推荐")
    private List<SimilarDreamVO> similarDreams;
}