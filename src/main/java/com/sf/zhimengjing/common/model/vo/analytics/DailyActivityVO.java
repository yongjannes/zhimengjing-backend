package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;



/**
 * @Title: DailyActivityVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 日活跃度 VO，用于展示每天的用户活跃情况，
 *               包含日期、活跃用户数、新增用户数及活跃度评分。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "日活跃度VO")
public class DailyActivityVO {
    @Schema(description = "日期")
    private LocalDate date;

    @Schema(description = "活跃用户数")
    private Long activeUsers;

    @Schema(description = "新增用户数")
    private Long newUsers;

    @Schema(description = "活跃度分数")
    private BigDecimal activityScore;
}