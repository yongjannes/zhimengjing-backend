package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Title: AIModel
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: AI模型实体类，用于存储AI模型的基本配置信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_models")
public class AIModel extends BaseEntity {

    /** AI模型编码（唯一标识） */
    private String modelCode;

    /** AI模型名称 */
    private String modelName;

    /** 提供商（如：DeepSeek、ZhipuAI、AliBaiLian） */
    private String provider;

    /** API端点地址 */
    private String apiEndpoint;

    /** 模型类型（如：text、image、embedding） */
    private String modelType;

    /** 最大令牌数 */
    private Integer maxTokens;

    /** 每千令牌成本 */
    @TableField("cost_per_1k_tokens")
    private BigDecimal costPer1kTokens;

    /** 温度参数（控制输出的随机性） */
    private BigDecimal temperature;

    /** 是否可用 */
    private Boolean isAvailable;

    /** 是否为默认模型 */
    private Boolean isDefault;

    /** 模型描述 */
    private String description;

    /** 删除标志（逻辑删除：0-正常，1-已删除） */
    @TableLogic
    private Integer deleteFlag;
}