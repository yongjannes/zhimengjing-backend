package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * @Title: RetentionSummaryVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 留存汇总 VO，用于展示用户留存分析的总体指标，
 *               包含平均留存率、最高留存率、最低留存率及留存趋势方向。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "留存汇总VO")
public class RetentionSummaryVO {
    @Schema(description = "平均留存率")
    private BigDecimal avgRetentionRate;

    @Schema(description = "最高留存率")
    private BigDecimal maxRetentionRate;

    @Schema(description = "最低留存率")
    private BigDecimal minRetentionRate;

    @Schema(description = "留存趋势")
    private String trendDirection;
}