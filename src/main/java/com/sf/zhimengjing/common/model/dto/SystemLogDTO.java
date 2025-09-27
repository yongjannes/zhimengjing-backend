package com.sf.zhimengjing.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sf.zhimengjing.common.model.BasePageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * @Title: SystemLogDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 系统日志数据传输对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统日志DTO")
public class SystemLogDTO extends BasePageDTO {

    @Schema(description = "日志级别")
    private String logLevel;

    @Schema(description = "模块名称")
    private String module;

    @Schema(description = "操作描述")
    private String operation;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户IP")
    private String userIp;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "关键词搜索")
    private String keyword;
}