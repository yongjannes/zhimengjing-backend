package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * @Title: CommentQueryDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto.community
 * @description: 社区评论查询DTO，用于封装评论查询条件，实现分页、过滤和模糊搜索功能
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社区评论查询DTO")
public class CommentQueryDTO extends UserQueryDTO {

    /**
     * 所属帖子ID，用于按帖子过滤评论
     */
    @Schema(description = "帖子ID")
    private Long postId;

    /**
     * 评论内容，支持模糊查询
     */
    @Schema(description = "评论内容（模糊查询）")
    private String content;

    /**
     * 评论状态：
     * 0 - 待审核
     * 1 - 已通过
     * 2 - 已拒绝
     * 3 - 已删除
     */
    @Schema(description = "状态：0-待审核，1-已通过，2-已拒绝，3-已删除")
    private Integer status;

    /** 评论开始时间 */
    @Schema(description = "评论开始时间")
    private LocalDateTime startDate;

    /** 评论结束时间 */
    @Schema(description = "评论结束时间")
    private LocalDateTime endDate;
}