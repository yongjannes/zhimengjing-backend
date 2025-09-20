package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: DreamCategory
 * @Author: 殤枫
 * @Package: com.sf.zhimengjing.entity
 * @Description: 梦境分类实体类，存储梦境的分类信息及相关属性
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dream_categories")
public class DreamCategory extends BaseEntity {

    /** 分类名称 */
    private String name;

    /** 分类描述 */
    private String description;

    /** 分类图标URL或名称 */
    private String icon;

    /** 分类颜色，用于前端显示（如标签颜色） */
    private String color;

    /** 父分类ID，支持多级分类，0或NULL表示顶级分类 */
    private Integer parentId;

    /** 排序序号，值越小越靠前 */
    private Integer sortOrder;

    /** 是否激活（0-否，1-是） */
    private Integer isActive;

    /** 属于该分类的梦境数量，用于统计展示 */
    private Integer dreamCount;
}
