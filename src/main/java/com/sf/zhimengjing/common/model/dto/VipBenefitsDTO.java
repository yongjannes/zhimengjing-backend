package com.sf.zhimengjing.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "VIP会员权益信息DTO")
public class VipBenefitsDTO implements Serializable {

    @Schema(description = "会员ID")
    private Long memberId;

    @Schema(description = "会员编号")
    private String membershipNo;

    @Schema(description = "会员等级名称")
    private String levelName;

    @Schema(description = "会员状态（ACTIVE-正常，EXPIRED-已过期，SUSPENDED-停用）")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Schema(description = "开始日期")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Schema(description = "到期日期")
    private LocalDate expireDate;

    @Schema(description = "每日解析次数上限")
    private Integer dailyAnalysisLimit;

    @Schema(description = "剩余解析次数")
    private Integer remainingAnalysisCount;

    @Schema(description = "存储空间（MB）")
    private Integer storageSpaceMb;

    @Schema(description = "可访问的AI模型列表")
    private List<String> aiModelAccess;

    @Schema(description = "是否优先客服支持")
    private Boolean prioritySupport;

    @Schema(description = "是否无广告体验")
    private Boolean adFree;

    @Schema(description = "高级功能列表")
    private List<String> advancedFeatures;

    @Schema(description = "折扣率")
    private BigDecimal discountRate;

    @Data
    @Schema(description = "会员使用统计DTO")
    public static class UsageStatsDTO implements Serializable {
        @Schema(description = "今日已用解析次数")
        private Integer todayAnalysisUsed;

        @Schema(description = "今日解析上限")
        private Integer todayAnalysisLimit;

        @Schema(description = "本月已用解析次数")
        private Integer monthAnalysisUsed;

        @Schema(description = "本月解析上限")
        private Integer monthAnalysisLimit;

        @Schema(description = "已使用存储空间（MB）")
        private Long storageUsedMb;

        @Schema(description = "存储空间上限（MB）")
        private Long storageLimitMb;

        @Schema(description = "使用占比（0-100，百分比）")
        private BigDecimal usagePercentage;
    }
}
