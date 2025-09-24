package com.sf.zhimengjing.common.model.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Schema(description = "预测结果DTO")
public class PredictionResultDTO {
    @Schema(description = "模型ID")
    private Long modelId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "预测类型")
    private String predictionType;

    @Schema(description = "输入数据")
    private Map<String, Object> inputData;

    @Schema(description = "预测结果")
    private Map<String, Object> predictionResult;

    @Schema(description = "置信度分数")
    private BigDecimal confidenceScore;

    @Schema(description = "准确率分数")
    private BigDecimal accuracyScore;
}