package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Title: DreamAuditDTO
 * @Author: 殤枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 梦境审核DTO
 */
@Data
@Schema(description = "梦境审核DTO")
public class DreamAuditDTO {

    @NotEmpty(message = "梦境ID列表不能为空")
    @Schema(description = "梦境ID列表", required = true)
    private List<Long> dreamIds;

    @NotNull(message = "审核状态不能为空")
    @Schema(description = "审核状态：3-通过，4-拒绝", required = true)
    private Integer status;

    @Schema(description = "拒绝原因（status=4时必填）")
    private String rejectReason;

    @Schema(description = "审核备注")
    private String reviewNotes;
}