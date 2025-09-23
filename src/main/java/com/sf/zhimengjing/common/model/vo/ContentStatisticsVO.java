package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Title: ContentStatisticsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.community
 * @description: 内容统计VO，用于展示社区帖子和评论的统计信息
 */
@Data
@Schema(description = "内容统计VO")
public class ContentStatisticsVO {

    /**
     * 今日新增帖子数量
     */
    @Schema(description = "今日新增帖子")
    private Long todayPosts;

    /**
     * 昨日新增帖子数量
     */
    @Schema(description = "昨日新增帖子")
    private Long yesterdayPosts;

    /**
     * 帖子总数
     */
    @Schema(description = "帖子总数")
    private Long totalPosts;

    /**
     * 今日新增评论数量
     */
    @Schema(description = "今日新增评论")
    private Long todayComments;

    /**
     * 昨日新增评论数量
     */
    @Schema(description = "昨日新增评论")
    private Long yesterdayComments;

    /**
     * 评论总数
     */
    @Schema(description = "评论总数")
    private Long totalComments;
}