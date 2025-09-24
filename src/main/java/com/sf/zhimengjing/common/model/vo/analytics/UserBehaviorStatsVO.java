package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Title: UserBehaviorStatsVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 用户行为统计 VO（视图对象），用于对外展示用户在系统中的行为数据统计结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户行为统计VO")
public class UserBehaviorStatsVO {
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "行为类型")
    private String behaviorType;

    @Schema(description = "行为次数")
    private Integer behaviorCount;

    @Schema(description = "总停留时长(秒)")
    private Long totalStayDuration;

    @Schema(description = "平均停留时长(秒)")
    private Double avgStayDuration;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "最后行为时间")
    private LocalDateTime lastBehaviorTime;

    @Schema(description = "会话数量")
    private Integer sessionCount;
}