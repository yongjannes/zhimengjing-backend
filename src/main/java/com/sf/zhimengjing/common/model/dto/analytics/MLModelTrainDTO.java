package com.sf.zhimengjing.common.model.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

/**
 * @Title: MLModelTrainDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto.analytics
 * @Description: 机器学习模型训练DTO，用于接收模型训练的参数
 */
@Data
@Schema(description = "机器学习模型训练参数")
public class MLModelTrainDTO {

    @NotBlank(message = "模型名称不能为空")
    @Schema(description = "模型名称", example = "情感分析模型")
    private String modelName;

    @NotBlank(message = "模型类型不能为空")
    @Schema(description = "模型类型", example = "EMOTION_ANALYSIS")
    private String modelType;

    @Schema(description = "模型描述", example = "用于分析用户梦境文本的情感倾向")
    private String description;

    @NotNull(message = "训练数据集配置不能为空")
    @Schema(description = "训练数据集来源及筛选条件")
    private Map<String, Object> datasetConfig;

    @NotNull(message = "模型超参数不能为空")
    @Schema(description = "模型训练的超参数", example = "{\"learning_rate\": 0.01, \"epochs\": 10}")
    private Map<String, Object> hyperparameters;

    @Schema(description = "特征工程配置")
    private Map<String, Object> featureEngineering;

    @NotNull(message = "评估指标不能为空")
    @Schema(description = "用于评估模型性能的指标", example = "[\"accuracy\", \"precision\", \"recall\"]")
    private String[] evaluationMetrics;
}