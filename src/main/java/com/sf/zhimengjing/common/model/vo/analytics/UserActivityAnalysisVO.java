package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Title: UserActivityAnalysisVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 用户活跃度分析 VO（视图对象）
 *               用于统计和展示用户的整体活跃度情况，包括访问频率、停留时长、行为分布等。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户活跃度分析VO")
public class UserActivityAnalysisVO {
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "活跃度等级")
    private String activityLevel;

    @Schema(description = "活跃度评分")
    private Double activityScore;

    @Schema(description = "总访问次数")
    private Integer totalVisits;

    @Schema(description = "总停留时长(分钟)")
    private Long totalStayMinutes;

    @Schema(description = "平均会话时长(分钟)")
    private Double avgSessionMinutes;

    @Schema(description = "活跃天数")
    private Integer activeDays;

    @Schema(description = "每日活跃趋势")
    private List<DailyActivityVO> dailyTrend;

    @Schema(description = "行为分布")
    private Map<String, Integer> behaviorDistribution;

    @Schema(description = "设备使用情况")
    private Map<String, Integer> deviceUsage;
}