package com.sf.zhimengjing.common.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @Title: SystemLogVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 系统日志视图对象
 */
@Data
@Schema(description = "系统日志VO")
public class SystemLogVO {

    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "日志级别")
    private String logLevel;

    @Schema(description = "模块名称")
    private String module;

    @Schema(description = "操作描述")
    private String operation;

    @Schema(description = "请求URL")
    private String requestUrl;

    @Schema(description = "请求方法")
    private String requestMethod;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户IP")
    private String userIp;

    @Schema(description = "执行时间(毫秒)")
    private Integer executionTime;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}