package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Title: CommunityPostAuditDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 社区帖子审核DTO
 */
@Data
@Schema(description = "社区帖子审核DTO")
public class CommunityPostAuditDTO {

    /** 帖子ID列表 */
    @Schema(description = "帖子ID列表")
    @NotEmpty(message = "帖子ID列表不能为空")
    private List<Long> postIds;

    /** 审核状态：1-已通过，2-已拒绝 */
    @Schema(description = "审核状态:1-已通过,2-已拒绝")
    @NotNull(message = "审核状态不能为空")
    private Integer status;

    /** 拒绝原因 */
    @Schema(description = "拒绝原因")
    private String rejectReason;

    /** 管理员备注 */
    @Schema(description = "管理员备注")
    private String adminRemark;
}