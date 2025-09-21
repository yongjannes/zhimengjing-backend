package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Title: DreamCategoryRelation
 * @Author: 殇枫
 * @Package: com.dreamanalysis.admin.entity
 * @description: 梦境分类关系实体类，对应表 dream_category_relations，
 *               用于存储分类之间的祖先-后代关系，用于支持多级分类树结构。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dream_category_relations")
public class DreamCategoryRelation extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 祖先分类ID（关联 dream_categories 表）
     */
    @TableField("ancestor_id")
    private Integer ancestorId;

    /**
     * 后代分类ID（关联 dream_categories 表）
     */
    @TableField("descendant_id")
    private Integer descendantId;

    /**
     * 距离层级（祖先到后代的层级距离，0表示自身）
     */
    @TableField("distance")
    private Integer distance;

    /**
     * 祖先分类对象（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private DreamCategory ancestor;

    /**
     * 后代分类对象（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private  DreamCategory descendant;
}
