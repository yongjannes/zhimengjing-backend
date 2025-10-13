package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Title: CommunityPostStatisticsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 社区帖子统计VO
 */
@Data
@Schema(description = "社区帖子统计VO")
public class CommunityPostStatisticsVO {

    /** 总帖子数 */
    @Schema(description = "总帖子数")
    private Long totalPosts;

    /** 待审核帖子数 */
    @Schema(description = "待审核帖子数")
    private Long pendingPosts;

    /** 已通过帖子数 */
    @Schema(description = "已通过帖子数")
    private Long approvedPosts;

    /** 已拒绝帖子数 */
    @Schema(description = "已拒绝帖子数")
    private Long rejectedPosts;

    /** 今日新增帖子数 */
    @Schema(description = "今日新增帖子数")
    private Long todayNewPosts;

    /** 本周新增帖子数 */
    @Schema(description = "本周新增帖子数")
    private Long weekNewPosts;
}