package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Title: DreamCategoryAttribute
 * @Author: 殇枫
 * @Package: com.dreamanalysis.admin.entity
 * @description: 梦境分类属性实体类，对应表 dream_categories_attributes，
 *               用于存储梦境分类的属性信息。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dream_category_attributes")
public class DreamCategoryAttribute extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 分类ID（关联 dream_categories 表）
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 属性名称
     */
    @TableField("attribute_name")
    private String attributeName;

    /**
     * 属性类型（如文本、数字、枚举等）
     */
    @TableField("attribute_type")
    private String attributeType;

    /**
     * 属性值（枚举或默认值等）
     */
    @TableField("attribute_value")
    private String attributeValue;

    /**
     * 是否必填（true-必填，false-非必填）
     */
    @TableField("is_required")
    private Boolean isRequired;

    /**
     * 是否可搜索（true-可搜索，false-不可搜索）
     */
    @TableField("is_searchable")
    private Boolean isSearchable;

    /**
     * 排序序号（越小越靠前）
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 所属分类对象（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private DreamCategory category;
}
