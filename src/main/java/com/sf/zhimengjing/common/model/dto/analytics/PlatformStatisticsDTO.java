package com.sf.zhimengjing.common.model.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "平台统计DTO")
public class PlatformStatisticsDTO {
    @Schema(description = "统计日期")
    private LocalDate statDate;

    @Schema(description = "总用户数")
    private Integer totalUsers;

    @Schema(description = "活跃用户数")
    private Integer activeUsers;

    @Schema(description = "新增用户数")
    private Integer newUsers;

    @Schema(description = "总梦境数")
    private Integer totalDreams;

    @Schema(description = "新增梦境数")
    private Integer newDreams;

    @Schema(description = "收入")
    private BigDecimal revenue;

    @Schema(description = "转化率")
    private BigDecimal conversionRate;

    @Schema(description = "留存率")
    private BigDecimal retentionRate;
}