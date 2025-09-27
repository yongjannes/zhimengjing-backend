package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Title: RecordConfigChangeDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 手动记录配置变更历史的数据传输对象
 *               用于向后台提交手动记录的配置变更信息，包括变更前后值、变更类型、修改人及操作相关信息
 */
@Data
@Schema(description = "手动记录配置变更历史的数据传输对象")
public class RecordConfigChangeDTO {

    @NotBlank(message = "配置项的键不能为空")
    @Schema(description = "配置项的键", required = true)
    private String configKey;

    @Schema(description = "旧值")
    private String oldValue;

    @Schema(description = "新值")
    private String newValue;

    @NotBlank(message = "变更类型不能为空")
    @Schema(description = "变更类型 (例如: CREATED, UPDATED, DELETED)", required = true)
    private String changeType;

    @Schema(description = "变更原因，可选")
    private String changeReason;

    @NotNull(message = "修改人ID不能为空")
    @Schema(description = "修改人ID", required = true)
    private Long changedBy;

    @Schema(description = "IP地址 (可选, 不填则自动获取)")
    private String ipAddress;

    @Schema(description = "用户代理 (可选, 不填则自动获取)")
    private String userAgent;
}
