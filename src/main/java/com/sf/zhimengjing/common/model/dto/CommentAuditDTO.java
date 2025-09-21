package com.sf.zhimengjing.common.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @Title: CommentAuditDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto.community
 * @description: 社区评论审核DTO，用于封装批量审核评论请求数据
 */
@Data
@ApiModel(description = "评论审核DTO")
public class CommentAuditDTO {

    /**
     * 评论ID列表，必填，用于标识需要审核的评论
     */
    @NotEmpty(message = "评论ID列表不能为空")
    @ApiModelProperty(value = "评论ID列表", required = true)
    private List<Long> commentIds;

    /**
     * 审核状态，必填
     * 1 - 通过
     * 2 - 拒绝
     */
    @NotEmpty(message = "审核状态不能为空")
    @ApiModelProperty(value = "审核状态：1-通过，2-拒绝", required = true)
    private Integer status;

    /**
     * 拒绝原因，仅在 status=2（拒绝）时必填
     */
    @ApiModelProperty("拒绝原因（status=2时必填）")
    private String rejectReason;
}
