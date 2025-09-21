package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.*;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: SensitiveWord
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.community
 * @description: 敏感词实体类，对应数据库表 sensitive_words
 *              用于存储社区敏感词信息及其处理规则
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sensitive_words")
public class SensitiveWord extends BaseEntity {

    /**
     * 敏感词ID，自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 敏感词文本
     */
    @TableField("word")
    private String word;

    /**
     * 敏感词类型（可根据业务自定义，例如：1-辱骂，2-广告，3-政治敏感等）
     */
    @TableField("word_type")
    private Integer wordType;

    /**
     * 严重等级（数字越大表示敏感程度越高）
     */
    @TableField("severity_level")
    private Integer severityLevel;

    /**
     * 替换词（可用于内容自动替换）
     */
    @TableField("replacement_word")
    private String replacementWord;

    /**
     * 是否生效，true-生效，false-无效
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 创建人ID（填充策略：插入时自动填充）
     */
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private Long createdBy;

    /**
     * 更新人ID（填充策略：插入和更新时自动填充）
     */
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;
}
