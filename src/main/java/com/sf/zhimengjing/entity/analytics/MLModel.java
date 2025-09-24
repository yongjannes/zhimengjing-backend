package com.sf.zhimengjing.entity.analytics;

import com.baomidou.mybatisplus.annotation.*;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Title: MLModel
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.analytics
 * @Description: 机器学习模型实体类，用于存储和管理模型的基本信息、训练指标和部署状态
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ml_models")
public class MLModel extends BaseEntity {


    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型类型（如分类、回归、聚类等）
     */
    private String modelType;

    /**
     * 模型版本号（用于迭代和管理不同版本）
     */
    private String modelVersion;

    /**
     * 算法名称（如 XGBoost、RandomForest、CNN 等）
     */
    private String algorithmName;

    /**
     * 模型参数（JSON 字符串格式，存储训练时的超参数配置）
     */
    private String modelParameters;

    /**
     * 训练数据量（记录模型训练所用的数据条数）
     */
    private Long trainingDataSize;

    /**
     * 模型准确率（Accuracy）
     */
    private BigDecimal accuracyScore;

    /**
     * 模型精确率（Precision）
     */
    private BigDecimal precisionScore;

    /**
     * 模型召回率（Recall）
     */
    private BigDecimal recallScore;

    /**
     * 模型 F1 值
     */
    private BigDecimal f1Score;

    /**
     * 模型文件存储路径（如模型文件在服务器或对象存储中的位置）
     */
    private String modelFilePath;

    /**
     * 模型状态（如 training、deployed、archived 等）
     */
    private String modelStatus;

    /**
     * 模型部署时间
     */
    private LocalDateTime deployedAt;

    /**
     * 逻辑删除标志（0：未删除，1：已删除）
     */
    @TableLogic
    private Integer deleteFlag;
}
