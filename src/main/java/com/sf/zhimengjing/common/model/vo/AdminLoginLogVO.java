package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: AdminLoginLogVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @description: 管理员登录日志VO
 */
@Data
@Schema(description = "管理员登录日志VO")
public class AdminLoginLogVO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "登录IP")
    private String loginIp;

    @Schema(description = "用户代理")
    private String userAgent;

    @Schema(description = "登录时间")
    private LocalDateTime loginTime;

    @Schema(description = "登录状态:0-失败,1-成功")
    private Integer status;

    @Schema(description = "失败原因")
    private String failReason;
}