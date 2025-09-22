package com.sf.zhimengjing.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sf.zhimengjing.common.enumerate.ReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: ReportReviewDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 报告审核相关的数据传输对象集合
 */
@Data
@Schema(description="报告审核信息DTO")
public class ReportReviewDTO implements Serializable {

    @Schema(description = "审核记录ID（主键ID）")
    private Long id;

    @Schema(description = "被审核的报告ID")
    private Long reportId;

    @Schema(description = "审核人ID（关联审核员表）")
    private Long reviewerId;

    @Schema(description = "审核人姓名（通过关联查询得到）")
    private String reviewerName;

    @Schema(description = "审核状态，可选值：PENDING-待审核，UNDER_REVIEW-审核中，REVIEWED-已审核，APPEALED-已申诉，APPEAL_RESOLVED-申诉已处理")
    private ReviewStatus reviewStatus;

    @Schema(description = "审核结果描述（对审核结论的说明）")
    private String reviewResult;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "审核时间")
    private LocalDateTime reviewedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间（记录生成的时间戳）")
    private LocalDateTime createTime;


    /**
     * 审核请求 DTO —— 接收前端提交的审核数据
     */
    @Data
    @Schema(description = "审核请求DTO")
    public static class ReviewRequestDTO implements Serializable {

        @Schema(description = "被审核的报告ID")
        @NotNull
        private Long reportId;

        @Schema(description = "审核结果描述")
        @NotNull
        private String reviewResult;

        @Schema(description = "审核评分（用于质量评价，可选）")
        private Integer reviewScore;

        @Schema(description = "审核备注/评论")
        private String reviewComment;

        @Schema(description = "违规类型（如涉黄、涉政等，可选）")
        private String violationType;

        @Schema(description = "违规详情说明（可选）")
        private String violationDetails;
    }


    /**
     * 批量分配审核任务 DTO —— 用于管理员批量指定审核员
     */
    @Data
    @Schema(description = "批量分配审核任务DTO")
    public static class BatchAssignDTO implements Serializable {

        @Schema(description = "待分配的报告ID集合")
        @NotNull
        private List<Long> reportIds;

        @Schema(description = "被分配的审核员ID")
        @NotNull
        private Long reviewerId;
    }


    /**
     * 审核统计 VO —— 用于返回审核数据统计结果
     */
    @Data
    @Schema(description = "审核统计VO")
    public static class ReviewStatsVO implements Serializable {

        @Schema(description = "审核总数")
        private Long totalReviews;

        @Schema(description = "审核通过数")
        private Long approvedCount;

        @Schema(description = "审核驳回数")
        private Long rejectedCount;

        @Schema(description = "违规报告数")
        private Long violationCount;

        @Schema(description = "待审核数")
        private Long pendingCount;

        @Schema(description = "申诉数")
        private Long appealCount;

        @Schema(description = "审核通过率（approvedCount / totalReviews）")
        private Double approvalRate;
    }
}
