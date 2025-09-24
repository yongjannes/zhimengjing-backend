package com.sf.zhimengjing.common.model.vo.analytics;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Title: DreamCategoryStatsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 梦境分类统计 VO，用于展示不同梦境分类的统计信息及趋势分析。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "梦境分类统计VO")
public class DreamCategoryStatsVO {
    @Schema(description = "分类统计列表")
    private List<CategoryStatsItemVO> categoryStats;

    @Schema(description = "趋势分析")
    private List<CategoryTrendVO> trendAnalysis;

    @Schema(description = "热门分类排行")
    private List<String> topCategories;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "分类统计项VO")
    public static class CategoryStatsItemVO {
        @Schema(description = "分类名称")
        private String categoryName;

        @Schema(description = "梦境数量")
        private Long dreamCount;

        @Schema(description = "用户数量")
        private Long userCount;

        @Schema(description = "平均情感分数")
        private BigDecimal avgEmotionScore;

        @Schema(description = "占比")
        private BigDecimal percentage;

        @Schema(description = "趋势方向")
        private String trendDirection;
    }
}