package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * @Title: ConfigChangeHistory
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: 配置变更历史实体类，对应数据库表 config_change_history
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("config_change_history")
public class ConfigChangeHistory extends BaseEntity {

    /** 配置键名 */
    private String configKey;

    /** 旧值 */
    private String oldValue;

    /** 新值 */
    private String newValue;

    /** 变更类型 */
    private String changeType;

    /** 变更原因 */
    private String changeReason;

    /** 变更人 */
    private Long changedBy;

    /** 变更时间 */
    private LocalDateTime changeTime;

    /** IP地址 */
    private String ipAddress;

    /** 用户代理 */
    private String userAgent;

    /** 删除标志（逻辑删除） */
    @TableLogic
    private Integer deleteFlag;
}