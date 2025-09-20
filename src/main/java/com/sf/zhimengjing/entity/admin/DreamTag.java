package com.sf.zhimengjing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: DreamTag
 * @Author: 殤枫
 * @Package: com.sf.zhimengjing.entity
 * @Description: 梦境标签实体类，用于给梦境打标签，便于分类和搜索
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dream_tags")
public class DreamTag extends BaseEntity {

    /** 标签名称 */
    private String name;

    /** 标签颜色，用于前端显示 */
    private String color;

    /** 标签描述，用于说明标签含义 */
    private String description;

    /** 标签使用次数统计，用于热门标签或排序 */
    private Integer usageCount;

    /** 是否激活（0-否，1-是），用于控制标签是否可用 */
    private Integer isActive;
}
