package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Title: PredictionResultVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 预测结果 VO，用于展示机器学习模型的预测输出，
 *               包含预测值、置信度、预测时间、解释信息及操作建议。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "预测结果VO")
public class PredictionResultVO {
    @Schema(description = "预测ID")
    private Long predictionId;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "预测类型")
    private String predictionType;

    @Schema(description = "预测结果")
    private Map<String, Object> predictionResult;

    @Schema(description = "置信度")
    private BigDecimal confidenceScore;

    @Schema(description = "预测时间")
    private LocalDateTime predictionTime;

    @Schema(description = "结果解释")
    private String explanation;

    @Schema(description = "建议操作")
    private List<String> recommendedActions;
}