package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * @Title: CommunityRule
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.community
 * @description: 社区规则实体类，对应数据库表 community_rules
 *              用于存储社区发布的各类规则信息及其生效时间
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("community_rules")
public class CommunityRule extends BaseEntity {

    /**
     * 规则ID，自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 规则标题
     */
    @TableField("rule_title")
    private String ruleTitle;

    /**
     * 规则内容
     */
    @TableField("rule_content")
    private String ruleContent;

    /**
     * 规则类型（可根据业务自定义，例如：1-社区管理规则，2-发帖规则，3-评论规则等）
     */
    @TableField("rule_type")
    private Integer ruleType;

    /**
     * 优先级，数字越小优先级越高
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 是否生效，true-生效，false-无效
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 生效开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("effective_start_time")
    private LocalDateTime effectiveStartTime;

    /**
     * 生效结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("effective_end_time")
    private LocalDateTime effectiveEndTime;

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
