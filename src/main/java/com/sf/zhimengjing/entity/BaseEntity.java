package com.sf.zhimengjing.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: BaseEntity
 * @Author 殇枫
 * @Package com.sf.zhimengjing.entity
 * @description: 基础实体类，提供id主键以及自动填充创建时间和更新时间
 */
@Data
public class BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id; // 主键

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间，插入时自动填充

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间，插入和更新时自动填充
}
