package com.sf.zhimengjing.common.model.vo.analytics;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Title: UserRetentionAnalysisVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 用户留存分析 VO（视图对象），用于统计和展示用户在一段时间内的留存情况。
 *               留存率是衡量用户活跃和产品粘性的重要指标。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户留存分析VO")
public class UserRetentionAnalysisVO {
    @Schema(description = "统计开始日期")
    private LocalDate startDate;

    @Schema(description = "统计结束日期")
    private LocalDate endDate;

    @Schema(description = "次日留存率")
    private BigDecimal dayOneRetention;

    @Schema(description = "7日留存率")
    private BigDecimal daySevenRetention;

    @Schema(description = "30日留存率")
    private BigDecimal dayThirtyRetention;

    @Schema(description = "留存率趋势")
    private List<RetentionDataVO> retentionTrend;

    @Schema(description = "用户群组分析")
    private List<CohortAnalysisVO> cohortAnalysis;

    @Schema(description = "总体分析")
    private RetentionSummaryVO summary;
}