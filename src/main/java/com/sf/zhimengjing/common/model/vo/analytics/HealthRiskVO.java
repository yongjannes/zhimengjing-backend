package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


/**
 * @Title: HealthRiskVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 健康风险 VO，用于展示用户健康风险分析结果，
 *               包含风险类型、风险等级、风险分数、风险描述及建议措施。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "健康风险VO")
public class HealthRiskVO {
    @Schema(description = "风险类型")
    private String riskType;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "风险分数")
    private BigDecimal riskScore;

    @Schema(description = "风险描述")
    private String riskDescription;

    @Schema(description = "建议措施")
    private List<String> recommendations;
}