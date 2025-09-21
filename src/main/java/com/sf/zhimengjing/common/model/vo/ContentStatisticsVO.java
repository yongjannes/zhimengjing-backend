package com.sf.zhimengjing.common.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Title: ContentStatisticsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.community
 * @description: 内容统计VO，用于展示社区帖子和评论的统计信息
 */
@Data
@ApiModel(description = "内容统计VO")
public class ContentStatisticsVO {

    /**
     * 今日新增帖子数量
     */
    @ApiModelProperty("今日新增帖子")
    private Long todayPosts;

    /**
     * 昨日新增帖子数量
     */
    @ApiModelProperty("昨日新增帖子")
    private Long yesterdayPosts;

    /**
     * 帖子总数
     */
    @ApiModelProperty("帖子总数")
    private Long totalPosts;

    /**
     * 今日新增评论数量
     */
    @ApiModelProperty("今日新增评论")
    private Long todayComments;

    /**
     * 昨日新增评论数量
     */
    @ApiModelProperty("昨日新增评论")
    private Long yesterdayComments;

    /**
     * 评论总数
     */
    @ApiModelProperty("评论总数")
    private Long totalComments;
}
