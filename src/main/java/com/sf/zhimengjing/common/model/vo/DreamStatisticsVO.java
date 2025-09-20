package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Title: DreamStatisticsVO
 * @Author: 殤枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 梦境统计视图对象，用于展示各类梦境数据的汇总信息
 */
@Data
@Schema(description = "梦境统计VO")
public class DreamStatisticsVO {

    /** 总梦境数，包括所有状态的梦境 */
    @Schema(description = "总梦境数")
    private Long totalDreams;

    /** 审核中的梦境数量 */
    @Schema(description = "审核中梦境数")
    private Long pendingDreams;

    /** 已审核通过的梦境数量 */
    @Schema(description = "已审核梦境数")
    private Long approvedDreams;

    /** 已被拒绝的梦境数量 */
    @Schema(description = "已拒绝梦境数")
    private Long rejectedDreams;

    /** 公开展示的梦境数量 */
    @Schema(description = "公开梦境数")
    private Long publicDreams;

    /** 今日新增梦境数量 */
    @Schema(description = "今日新增梦境数")
    private Long todayNewDreams;

    /** 本周新增梦境数量 */
    @Schema(description = "本周新增梦境数")
    private Long weekNewDreams;

    /** 本月新增梦境数量 */
    @Schema(description = "本月新增梦境数")
    private Long monthNewDreams;
}
