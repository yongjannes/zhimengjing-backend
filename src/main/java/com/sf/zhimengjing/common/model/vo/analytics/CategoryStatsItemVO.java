package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * @Title: CategoryStatsItemVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 分类统计项 VO，用于展示单个梦境分类的统计信息，
 *               包含分类名称、梦境数量、占比及平均情感分数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分类统计项VO")
public class CategoryStatsItemVO {
    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "梦境数量")
    private Long dreamCount;

    @Schema(description = "占比")
    private BigDecimal percentage;

    @Schema(description = "平均情感分数")
    private BigDecimal avgEmotionScore;
}