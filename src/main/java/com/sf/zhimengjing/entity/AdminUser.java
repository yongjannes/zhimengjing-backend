package com.sf.zhimengjing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * @Title: AdminUser
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity
 * @description: 后台管理用户实体类，对应表 admin_users
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("admin_users")
public class AdminUser extends BaseEntity {
    /**
     * 用户名（登录账号）
     */
    private String username;

    /**
     * 登录密码（加密存储）
     */
    private String password;

    /**
     * 用户真实姓名
     */
    private String realName;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 角色ID（关联角色表）
     */
    private Long roleId;

    /**
     * 用户状态（0:禁用，1:正常）
     */
    private Integer status;

    /**
     * 登录失败次数（用于锁定或风控）
     */
    private Integer loginFailCount;

    /**
     * 最后一次登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后一次登录IP
     */
    private String lastLoginIp;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 修改人ID
     */
    private Long updateBy;
}
