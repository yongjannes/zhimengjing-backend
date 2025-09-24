package com.sf.zhimengjing.common.model.vo.analytics;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Title: RealtimeStatsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 实时统计 VO，用于展示平台的实时运营数据，
 *               包括在线用户、活跃用户、梦境新增、收入、转化率及系统负载等指标。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "实时统计VO")
public class RealtimeStatsVO {
    @Schema(description = "当前在线用户数")
    private Integer onlineUsers;

    @Schema(description = "今日活跃用户数")
    private Integer todayActiveUsers;

    @Schema(description = "今日新增用户数")
    private Integer todayNewUsers;

    @Schema(description = "今日新增梦境数")
    private Integer todayNewDreams;

    @Schema(description = "今日收入")
    private BigDecimal todayRevenue;

    @Schema(description = "当前转化率")
    private BigDecimal currentConversionRate;

    @Schema(description = "系统负载")
    private String systemLoad;

    @Schema(description = "最后更新时间")
    private LocalDateTime lastUpdateTime;
}