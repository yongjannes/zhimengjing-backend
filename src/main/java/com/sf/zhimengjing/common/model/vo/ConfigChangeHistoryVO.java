package com.sf.zhimengjing.common.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: ConfigChangeHistoryVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 配置变更历史视图对象
 */
@Data
@Schema(description = "配置变更历史视图对象")
public class ConfigChangeHistoryVO {

    @Schema(description = "历史记录ID")
    private Long id;

    @Schema(description = "配置项的键")
    private String configKey;

    @Schema(description = "旧值")
    private String oldValue;

    @Schema(description = "新值")
    private String newValue;

    @Schema(description = "修改人")
    private String changedBy;

    @Schema(description = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime changeTime;

    @Schema(description = "变更类型 (e.g., CREATED, UPDATED, DELETED)")
    private String changeType;

    @Schema(description = "变更原因")
    private String changeReason;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "用户代理")
    private String userAgent;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "描述")
    private String description;
}