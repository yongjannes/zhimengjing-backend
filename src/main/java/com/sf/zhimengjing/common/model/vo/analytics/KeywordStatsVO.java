package com.sf.zhimengjing.common.model.vo.analytics;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;


/**
 * @Title: KeywordStatsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 关键词统计 VO，用于展示梦境或文本中关键词的统计信息，
 *               包括出现频率、用户数量、平均情感分数、关联分类、趋势及相关关键词。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "关键词统计VO")
public class KeywordStatsVO {
    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "出现频率")
    private Long frequency;

    @Schema(description = "用户数量")
    private Long userCount;

    @Schema(description = "平均情感分数")
    private BigDecimal avgEmotionScore;

    @Schema(description = "关联分类")
    private String category;

    @Schema(description = "趋势数据")
    private List<Integer> trendData;

    @Schema(description = "相关关键词")
    private List<String> relatedKeywords;
}