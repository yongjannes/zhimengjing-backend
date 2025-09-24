package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Title: ModelPerformanceVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 模型性能 VO，用于展示机器学习模型的各项性能指标，
 *               包括准确率、精确率、召回率、F1 值、AUC、损失值及改进建议。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "模型性能VO")
public class ModelPerformanceVO {
    @Schema(description = "准确率")
    private BigDecimal accuracyScore;

    @Schema(description = "精确率")
    private BigDecimal precisionScore;

    @Schema(description = "召回率")
    private BigDecimal recallScore;

    @Schema(description = "F1分数")
    private BigDecimal f1Score;

    @Schema(description = "AUC分数")
    private BigDecimal aucScore;

    @Schema(description = "损失值")
    private BigDecimal lossValue;

    @Schema(description = "性能等级")
    private String performanceGrade;

    @Schema(description = "推荐改进措施")
    private List<String> improvementSuggestions;
}