package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Title: ReportStatisticsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.community
 * @description: 待处理统计VO，用于展示社区后台待审核/待处理内容数量
 */
@Data
@Schema(description = "待处理统计VO")
public class ReportStatisticsVO {

    /**
     * 待审核帖子数量
     */
    @Schema(description = "待审核帖子数")
    private Long pendingPosts;

    /**
     * 待审核评论数量
     */
    @Schema(description = "待审核评论数")
    private Long pendingComments;

    /**
     * 待处理举报数量
     */
    @Schema(description = "待处理举报数")
    private Long pendingReports;
}