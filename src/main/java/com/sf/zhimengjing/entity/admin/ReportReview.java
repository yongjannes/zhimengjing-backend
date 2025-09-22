package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * @Title: ReportReview
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: 报告审核记录实体类，用于存储管理员对报告的审核结果和处理信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_reviews")
public class ReportReview extends BaseEntity {

    /** 被审核的报告ID */
    private Long reportId;

    /** 审核人ID（关联管理员或审核人员） */
    private Long reviewerId;

    /** 审核状态（如：PENDING-待审核，APPROVED-通过，REJECTED-驳回） */
    private String reviewStatus;

    /** 审核结果描述（补充审核意见的文字说明） */
    private String reviewResult;

    /** 审核评分（可选，用于质量评价或打分） */
    private Integer reviewScore;

    /** 审核备注/评论 */
    private String reviewComment;

    /** 违规类型（如：涉黄、涉政、虚假信息等） */
    private String violationType;

    /** 违规详细说明 */
    private String violationDetails;

    /** 是否提出申诉（true：已申诉，false：未申诉） */
    private Boolean isAppealed;

    /** 申诉处理结果（如：驳回申诉、申诉成功） */
    private String appealResult;

    /** 审核时间 */
    private LocalDateTime reviewedAt;

    /** 删除标志（逻辑删除，0：正常，1：已删除） */
    @TableLogic
    private Integer deleteFlag;
}
