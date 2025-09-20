package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: UserTag
 * @Author 殇枫
 * @Package com.sf.zhimengjing.entity
 * @description: 用户标签表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_tags")
public class UserTag extends BaseEntity {

    /** 标签名称 */
    private String tagName;

    /** 标签编码（唯一） */
    private String tagCode;

    /** 标签类型:1-系统标签,2-自定义标签 */
    private Integer tagType;

    /** 标签颜色 */
    private String color;

    /** 标签描述 */
    private String description;

    /** 是否系统标签:0-否,1-是 */
    private Integer isSystem;

    /** 排序 */
    private Integer sortOrder;

    /** 状态:0-禁用,1-启用 */
    private Integer status;

    /** 创建人ID */
    private Long createBy;

    /** 更新人ID */
    private Long updateBy;
}
