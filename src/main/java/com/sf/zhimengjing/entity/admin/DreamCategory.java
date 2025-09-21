package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @Title: DreamCategory
 * @Author: 殇枫
 * @Package: com.dreamanalysis.admin.entity
 * @description: 梦境分类实体类，对应表 dream_categories，
 * 用于存储梦境分类的基本信息和层级结构。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dream_categories")
public class DreamCategory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long  id;

    /**
     * 分类名称
     */
    @TableField("name")
    private String name;

    /**
     * 分类描述
     */
    @TableField("description")
    private String description;

    /**
     * 分类图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 分类颜色
     */
    @TableField("color")
    private String color;

    /**
     * 父分类ID（0 表示顶级分类）
     */
    @TableField("parent_id")
    private Integer parentId;

    /**
     * 分类层级（1-顶级，2-二级，以此类推）
     */
    @TableField("level")
    private Integer level;

    /**
     * 排序序号（越小越靠前）
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 是否启用（true-启用，false-禁用）
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 是否系统内置分类（true-系统分类，false-用户自建）
     */
    @TableField("is_system")
    private Boolean isSystem;

    /**
     * 梦境数量（统计字段）
     */
    @TableField("dream_count")
    private Integer dreamCount;

    /**
     * 子分类数量（统计字段）
     */
    @TableField("sub_category_count")
    private Integer subCategoryCount;
    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 修改人ID
     */
    private Long updatedBy;
    /**
     * 父分类对象（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private DreamCategory parent;

    /**
     * 子分类列表（非数据库字段，用于层级展示）
     */
    @TableField(exist = false)
    private List<DreamCategory> children;

    /**
     * 分类属性列表（非数据库字段，用于拓展属性）
     */
    @TableField(exist = false)
    private List<DreamCategoryAttribute> attributes;

    /**
     * 分类统计信息（非数据库字段）
     */
    @TableField(exist = false)
    private DreamCategoryStatistics statistics;
}