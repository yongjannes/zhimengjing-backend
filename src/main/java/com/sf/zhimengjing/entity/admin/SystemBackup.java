package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * @Title: SystemBackup
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: 系统备份实体类，对应数据库表 system_backups
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_backups")
public class SystemBackup extends BaseEntity {

    /** 备份名称 */
    private String backupName;

    /** 备份类型 */
    private String backupType;

    /** 备份大小(字节) */
    private Long backupSize;

    /** 备份文件路径 */
    private String filePath;

    /** 备份状态 */
    private String backupStatus;

    /** 错误信息 */
    private String errorMessage;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 创建人 */
    private Long createdBy;

    /** 删除标志（逻辑删除） */
    @TableLogic
    private Integer deleteFlag;
}