package com.sf.zhimengjing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: AdminRole
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity
 * @description: 后台管理角色实体类，对应表 admin_roles
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("admin_roles")
public class AdminRole extends BaseEntity {
    /**
     * 角色名称（如：管理员、操作员）
     */
    private String roleName;

    /**
     * 角色编码（唯一标识，如：admin、user）
     */
    private String roleCode;

    /**
     * 角色描述（简要说明角色用途）
     */
    private String description;

    /**
     * 权限标识集合（JSON 或逗号分隔的权限列表）
     */
    private String permissions;

    /**
     * 是否系统内置角色（0:否，1:是）
     */
    private Integer isSystem;

    /**
     * 排序值（数字越小优先级越高）
     */
    private Integer sortOrder;

    /**
     * 角色状态（0:禁用，1:启用）
     */
    private Integer status;
}
