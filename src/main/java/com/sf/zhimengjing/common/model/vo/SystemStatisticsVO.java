package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Title: SystemStatisticsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 系统统计信息视图对象
 */
@Data
@Schema(description = "系统统计信息VO")
public class SystemStatisticsVO {

    @Schema(description = "配置项总数")
    private Long totalSettings;

    @Schema(description = "系统配置数")
    private Long systemSettings;

    @Schema(description = "用户配置数")
    private Long userSettings;

    @Schema(description = "日志总数")
    private Long totalLogs;

    @Schema(description = "错误日志数")
    private Long errorLogs;

    @Schema(description = "警告日志数")
    private Long warningLogs;

    @Schema(description = "备份总数")
    private Long totalBackups;

    @Schema(description = "成功备份数")
    private Long successBackups;

    @Schema(description = "失败备份数")
    private Long failedBackups;

    @Schema(description = "第三方服务总数")
    private Long totalServices;

    @Schema(description = "激活服务数")
    private Long activeServices;
}