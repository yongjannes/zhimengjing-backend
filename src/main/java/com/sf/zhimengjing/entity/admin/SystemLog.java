package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: SystemLog
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: 系统日志实体类，对应数据库表 system_logs
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_logs")
public class SystemLog extends BaseEntity {

    /** 日志级别 */
    private String logLevel;

    /** 模块名称 */
    private String module;

    /** 操作描述 */
    private String operation;

    /** 请求URL */
    private String requestUrl;

    /** 请求方法 */
    private String requestMethod;

    /** 请求参数 */
    private String requestParams;

    /** 响应结果 */
    private String responseResult;

    /** 错误信息 */
    private String errorMessage;

    /** 用户ID */
    private Long userId;

    /** 用户IP */
    private String userIp;

    /** 用户代理 */
    private String userAgent;

    /** 执行时间(毫秒) */
    private Integer executionTime;

    /** 删除标志（逻辑删除） */
    @TableLogic
    private Integer deleteFlag;
}