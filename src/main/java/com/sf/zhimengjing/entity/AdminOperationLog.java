package com.sf.zhimengjing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @Title: AdminOperationLog
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity
 * @description: 后台管理操作日志实体类，对应表 admin_operation_logs
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("admin_operation_logs")
public class AdminOperationLog extends BaseEntity {

    /**
     * 屏蔽 BaseEntity 的 updateTime 字段
     */
    @TableField(exist = false)
    private LocalDateTime updateTime;

    /**
     * 管理员用户ID（关联 admin_users 表）
     */
    private Long adminId;

    /**
     * 管理员用户名
     */
    private String adminName;

    /**
     * 模块名称（如：用户管理、角色管理）
     */
    private String module;

    /**
     * 操作内容（如：新增用户、删除角色）
     */
    private String operation;

    /**
     * 请求路径（如：/api/admin/user/add）
     */
    private String requestPath;

    /**
     * 请求方法（GET、POST、PUT、DELETE 等）
     */
    private String requestMethod;

    /**
     * 请求参数（序列化为 JSON 或字符串存储）
     */
    private String requestParams;

    /**
     * 响应结果（序列化为 JSON 或字符串存储）
     */
    private String responseResult;

    /**
     * 请求IP地址
     */
    private String ipAddress;

    /**
     * 执行耗时（单位：毫秒）
     */
    private Integer executeTime;
}
