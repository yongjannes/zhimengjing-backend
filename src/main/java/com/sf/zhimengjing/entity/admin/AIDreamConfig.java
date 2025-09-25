package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: AIDreamConfig
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: AI梦境解析配置实体类，用于存储不同AI模型的梦境解析配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_dream_configs")
public class AIDreamConfig extends BaseEntity {

    /** 关联的AI模型编码 */
    private String modelCode;

    /** 解析模式（如：psychology-心理学，symbolic-象征意义，emotion-情感分析，comprehensive-综合分析） */
    private String analysisMode;

    /** 解析深度（如：basic-基础，detailed-详细，professional-专业） */
    private String analysisDepth;

    /** 语言风格（如：friendly-友好亲切，professional-专业严谨，casual-轻松随意，warm-温暖关怀） */
    private String languageStyle;

    /** 解析长度（如：short-简短，medium-中等，long-详细） */
    private String analysisLength;

    /** 是否启用情感分析 */
    private Boolean enableEmotionAnalysis;

    /** 是否启用标签生成 */
    private Boolean enableTagGeneration;

    /** 是否启用建议生成 */
    private Boolean enableSuggestion;

    /** 自定义提示词 */
    private String customPrompt;

    /** 是否激活 */
    private Boolean isActive;

    /** 删除标志（逻辑删除：0-正常，1-已删除） */
    @TableLogic
    private Integer deleteFlag;
}