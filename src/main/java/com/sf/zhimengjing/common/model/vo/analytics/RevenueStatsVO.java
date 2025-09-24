package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * @Title: RevenueStatsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 收入统计 VO，用于展示平台收入相关的统计信息，
 *               包含总收入、平均日收入、平均每用户收入、VIP收入占比及收入增长率。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收入统计VO")
public class RevenueStatsVO {
    @Schema(description = "总收入")
    private BigDecimal totalRevenue;

    @Schema(description = "平均日收入")
    private BigDecimal avgDailyRevenue;

    @Schema(description = "平均每用户收入")
    private BigDecimal avgRevenuePerUser;

    @Schema(description = "VIP收入占比")
    private BigDecimal vipRevenuePercentage;

    @Schema(description = "收入增长率")
    private BigDecimal revenueGrowthRate;
}