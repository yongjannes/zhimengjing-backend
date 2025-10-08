package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;

/**
 * @Title: DreamReviewLog
 * @Author 殇枫
 * @Package com.sf.zhimengjing.entity.admin
 * @description: 梦境审核日志实体
 */
@Data
@TableName("dream_review_logs")
public class DreamReviewLog extends BaseEntity {
    private Long dreamId;
    private Long reviewerId;
    private String action;
    private Integer previousStatus;
    private Integer newStatus;
    private String reason;
}