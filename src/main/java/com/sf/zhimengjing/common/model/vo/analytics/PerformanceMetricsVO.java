package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Title: PerformanceMetricsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 性能指标 VO，用于展示平台系统性能相关的统计信息，
 *               包含平均会话时长、系统负载、响应时间、错误率及可用性。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "性能指标VO")
public class PerformanceMetricsVO {
    @Schema(description = "平均会话时长")
    private BigDecimal avgSessionDuration;

    @Schema(description = "系统负载")
    private BigDecimal systemLoad;

    @Schema(description = "响应时间")
    private BigDecimal responseTime;

    @Schema(description = "错误率")
    private BigDecimal errorRate;

    @Schema(description = "可用性")
    private BigDecimal availability;
}