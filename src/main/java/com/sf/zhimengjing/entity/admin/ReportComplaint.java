package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * @Title: ReportComplaint
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: 报告投诉实体类，用于存储用户对报告的投诉信息及处理情况
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("report_complaints")
public class ReportComplaint extends BaseEntity {

    /** 被投诉的报告ID */
    private Long reportId;

    /** 投诉人ID（关联用户表） */
    private Long complainantId;

    /** 投诉类型（如：虚假内容、违规信息、抄袭等） */
    private String complaintType;

    /** 投诉原因（详细描述投诉理由） */
    private String complaintReason;

    /** 投诉状态（如：PENDING-待处理，RESOLVED-已处理，REJECTED-已驳回） */
    private String complaintStatus;

    /** 处理人ID（关联管理员或审核人员） */
    private Long handlerId;

    /** 处理结果（如：投诉成立、投诉驳回、进一步调查） */
    private String handleResult;

    /** 处理时间 */
    private LocalDateTime handledAt;

    /** 删除标志（逻辑删除：0-正常，1-已删除） */
    @TableLogic
    private Integer deleteFlag;
}
