package com.sf.zhimengjing.common.model.vo.analytics;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Title: ConversionFunnelVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 转化漏斗分析 VO（视图对象）
 *               用于展示用户在不同转化步骤中的数量、转化率和流失率，
 *               帮助分析业务流程中的用户转化情况。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "转化漏斗分析VO")
public class ConversionFunnelVO {
    @Schema(description = "漏斗类型")
    private String funnelType;

    @Schema(description = "漏斗步骤")
    private List<FunnelStepVO> steps;

    @Schema(description = "总体转化率")
    private BigDecimal overallConversionRate;

    @Schema(description = "漏斗分析摘要")
    private FunnelSummaryVO summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "漏斗步骤VO")
    public static class FunnelStepVO {
        @Schema(description = "步骤名称")
        private String stepName;

        @Schema(description = "用户数量")
        private Integer userCount;

        @Schema(description = "转化率")
        private BigDecimal conversionRate;

        @Schema(description = "流失率")
        private BigDecimal dropOffRate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "漏斗摘要VO")
    public static class FunnelSummaryVO {
        @Schema(description = "总进入用户数")
        private Integer totalUsers;

        @Schema(description = "最终转化用户数")
        private Integer convertedUsers;

        @Schema(description = "最大流失步骤")
        private String maxDropOffStep;

        @Schema(description = "优化建议")
        private List<String> optimizationSuggestions;
    }
}