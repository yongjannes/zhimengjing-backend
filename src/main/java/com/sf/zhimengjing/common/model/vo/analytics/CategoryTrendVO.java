package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * @Title: CategoryTrendVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 分类趋势 VO，用于展示特定梦境分类在不同日期的数量变化趋势，
 *               包含分类名称、日期、梦境数量及趋势变化幅度。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分类趋势VO")
public class CategoryTrendVO {
    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "日期")
    private LocalDate date;

    @Schema(description = "梦境数量")
    private Long dreamCount;

    @Schema(description = "趋势变化")
    private BigDecimal trendChange;
}