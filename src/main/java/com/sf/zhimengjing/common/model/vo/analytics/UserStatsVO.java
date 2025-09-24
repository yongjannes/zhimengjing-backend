package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * @Title: UserStatsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 用户统计 VO，用于展示平台用户的整体统计信息，
 *               包含总用户数、活跃用户数、新增用户数、流失用户数及用户增长率。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户统计VO")
public class UserStatsVO {
    @Schema(description = "总用户数")
    private Long totalUsers;

    @Schema(description = "活跃用户数")
    private Long activeUsers;

    @Schema(description = "新增用户数")
    private Long newUsers;

    @Schema(description = "流失用户数")
    private Long churnedUsers;

    @Schema(description = "用户增长率")
    private BigDecimal userGrowthRate;
}