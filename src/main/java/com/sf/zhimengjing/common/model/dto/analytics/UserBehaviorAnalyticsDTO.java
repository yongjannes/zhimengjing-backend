package com.sf.zhimengjing.common.model.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description="用户行为分析DTO")
public class UserBehaviorAnalyticsDTO {
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "行为类型")
    private String behaviorType;

    @Schema(description = "统计开始时间")
    private LocalDateTime startTime;

    @Schema(description = "统计结束时间")
    private LocalDateTime endTime;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "操作系统类型")
    private String osType;
}