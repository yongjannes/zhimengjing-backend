package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


/**
 * @Title: SimilarDreamVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 相似梦境 VO，用于展示与当前梦境内容相似的梦境列表，
 *               包含梦境 ID、相似度分数、梦境标题及共同关键词。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "相似梦境VO")
public class SimilarDreamVO {
    @Schema(description = "梦境ID")
    private Long dreamId;

    @Schema(description = "相似度分数")
    private BigDecimal similarityScore;

    @Schema(description = "梦境标题")
    private String dreamTitle;

    @Schema(description = "共同关键词")
    private List<String> commonKeywords;
}