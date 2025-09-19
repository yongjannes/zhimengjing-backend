package com.sf.zhimengjing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Title: AdminLoginLog
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity
 * @description: 后台管理用户登录日志实体类，对应表 admin_login_logs
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("admin_login_logs")
public class AdminLoginLog extends BaseEntity {
    /**
     * 管理员用户ID（关联 admin_users 表）
     */
    private Long adminId;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录IP地址
     */
    private String loginIp;

    /**
     * 用户代理信息（浏览器、操作系统等）
     */
    private String userAgent;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 登录状态（0:失败，1:成功）
     */
    private Integer status;

    /**
     * 登录失败原因（仅在失败时记录，如密码错误、账户锁定）
     */
    private String failReason;

    /**
     * 创建时间（数据库表中不存在，仅BaseEntity使用）
     */
    @TableField(exist = false)
    private LocalDateTime createTime;

    /**
     * 更新时间（数据库表中不存在，仅BaseEntity使用）
     */
    @TableField(exist = false)
    private LocalDateTime updateTime;
}
