package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: AIPromptTemplate
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: AI提示词模板实体类，用于存储和管理AI提示词模板
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_prompt_templates")
public class AIPromptTemplate extends BaseEntity {

    /** 模板编码（唯一标识） */
    private String templateCode;

    /** 模板名称 */
    private String templateName;

    /** 模板内容 */
    private String templateContent;

    /** 模板类型（如：system_prompt-系统提示词，dream_analysis-梦境解析，emotion_analysis-情感分析） */
    private String templateType;

    /** 变量定义（JSON格式） */
    private String variables;

    /** 关联模型编码（可为空，表示通用模板） */
    private String modelCode;

    /** 版本号 */
    private Integer version;

    /** 是否激活 */
    private Boolean isActive;

    /** 创建人ID */
    private Long createdBy;

    /** 删除标志（逻辑删除：0-正常，1-已删除） */
    @TableLogic
    private Integer deleteFlag;
}