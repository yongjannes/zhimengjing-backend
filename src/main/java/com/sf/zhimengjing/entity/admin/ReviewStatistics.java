package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

/**
 * @Title: ReviewStatistics
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: 审核统计实体类，用于存储审核员在某一天的审核统计数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("review_statistics")
public class ReviewStatistics extends BaseEntity {

    /** 统计日期（按天存储数据，如：2025-09-22） */
    private LocalDate statDate;

    /** 审核员ID（关联 Reviewer 表） */
    private Long reviewerId;

    /** 当日审核总数 */
    private Integer reviewedCount;

    /** 当日审核通过数 */
    private Integer approvedCount;

    /** 当日审核驳回数 */
    private Integer rejectedCount;

    /** 当日违规报告数（如违规类型的判定结果数量） */
    private Integer violationCount;

    /** 平均审核时长（单位：秒，可用于衡量审核效率） */
    private Integer avgReviewTime;
}
