package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


/**
 * @Title: PostAuditDTO
 * @Author: 殇枫
 * @Package: com.dreamanalysis.admin.dto.community
 * @Description: 帖子审核数据传输对象（DTO），用于管理员对帖子进行审核时传输数据，
 *                  包含帖子ID、审核状态、拒绝原因、管理员备注及操作人ID。
 */
@Data
@Schema(description = "帖子审核DTO，用于管理员审核帖子时的数据传输")
public class PostAuditDTO {

    @NotEmpty(message = "帖子ID列表不能为空")
    @Schema(description = "帖子ID列表", required = true)
    private List<Long> postIds;

    @NotNull(message = "审核状态不能为空")
    @Schema(description = "审核状态：1-通过，2-拒绝", required = true)
    private Integer status;

    @Schema(description = "拒绝原因（status=2时必填）")
    private String rejectReason;

    @Schema(description = "管理员备注")
    private String adminRemark;

    @Schema(description = "操作人ID")
    private Long operatorId;
}
