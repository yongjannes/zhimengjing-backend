package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * @Title: FunnelStepVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 漏斗步骤 VO，用于展示转化漏斗中的每个步骤，
 *               包含步骤名称、顺序、用户数量、转化率及流失率。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "漏斗步骤VO")
public class FunnelStepVO {
    @Schema(description = "步骤名称")
    private String stepName;

    @Schema(description = "步骤顺序")
    private Integer stepOrder;

    @Schema(description = "用户数量")
    private Long userCount;

    @Schema(description = "转化率")
    private BigDecimal conversionRate;

    @Schema(description = "流失率")
    private BigDecimal dropOffRate;
}