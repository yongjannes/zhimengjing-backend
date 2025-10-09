package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.*;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Title: DreamTag
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @description: 梦境标签实体类，对应表 dream_tags，
 * 用于存储标签的基本信息和使用统计。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dream_tags")
public class DreamTag extends BaseEntity implements Serializable {

    /**
     * 标签名称（唯一）
     */
    @TableField("name")
    private String name;

    /**
     * 标签颜色
     */
    @TableField("color")
    private String color;

    /**
     * 标签描述
     */
    @TableField("description")
    private String description;

    /**
     * 使用次数（统计字段）
     */
    @TableField("usage_count")
    private Integer usageCount;

    /**
     * 是否启用（true-启用，false-禁用）
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 删除标志（0-正常，1-删除）
     */
    @TableField("delete_flag")
    @TableLogic
    private Integer deleteFlag;
}