package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * @Title: MLModelVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 机器学习模型 VO，用于展示模型的基本信息、性能指标及状态，
 *               可用于前端展示或管理界面。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "机器学习模型VO")
public class MLModelVO {
    @Schema(description = "模型ID")
    private Long modelId;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "模型类型")
    private String modelType;

    @Schema(description = "模型版本")
    private String modelVersion;

    @Schema(description = "算法名称")
    private String algorithmName;

    @Schema(description = "训练数据量")
    private Long trainingDataSize;

    @Schema(description = "模型性能")
    private ModelPerformanceVO performance;

    @Schema(description = "模型状态")
    private String modelStatus;

    @Schema(description = "部署时间")
    private LocalDateTime deployedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}