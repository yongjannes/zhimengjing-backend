package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @Title: ReportComplaintDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 报告投诉相关的数据传输对象，用于前后端交互
 */
@Data
@Schema(description = "报告投诉DTO")
public class ReportComplaintDTO implements Serializable {

    @Schema(description = "投诉记录ID（主键ID）")
    private Long id;

    @Schema(description = "被投诉的报告ID")
    private Long reportId;

    @Schema(description = "投诉人ID（关联用户表）")
    private Long complainantId;

    @Schema(description = "投诉类型（如：虚假内容、违规信息、抄袭等）")
    private String complaintType;

    @Schema(description = "投诉原因（用户填写的详细理由）")
    private String complaintReason;

    @Schema(description = "投诉状态（如：PENDING-待处理，RESOLVED-已处理，REJECTED-驳回）")
    private String complaintStatus;

    @Schema(description = "处理人ID（关联审核员或管理员）")
    private Long handlerId;

    @Schema(description = "处理结果（如：投诉成立、投诉驳回、进一步调查）")
    private String handleResult;


    /**
     * 投诉请求 DTO —— 前端提交投诉时使用
     */
    @Data
    @Schema(description = "投诉请求DTO")
    public static class ComplaintRequestDTO implements Serializable {

        @Schema(description = "被投诉的报告ID")
        @NotNull
        private Long reportId;

        @Schema(description = "投诉类型（如虚假内容、违规信息等）")
        @NotNull
        private String complaintType;

        @Schema(description = "投诉原因（不能为空）")
        @NotBlank
        private String complaintReason;
    }
}
