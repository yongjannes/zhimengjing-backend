package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * @Title: TrendAnalysisVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 趋势分析 VO，用于展示平台关键指标的趋势分析结果，
 *               包含趋势方向、趋势强度、趋势描述、预测值及置信度。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "趋势分析VO")
public class TrendAnalysisVO {
    @Schema(description = "趋势方向")
    private String trend;

    @Schema(description = "趋势强度")
    private String trendStrength;

    @Schema(description = "趋势描述")
    private String trendDescription;

    @Schema(description = "预测值")
    private BigDecimal predictedValue;

    @Schema(description = "置信度")
    private BigDecimal confidence;
}