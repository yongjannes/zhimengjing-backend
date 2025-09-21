package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Title: CategoryStatisticsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @description: 梦境分类统计视图对象（VO），用于前端展示分类的统计信息，
 *               包含梦境数量、审核状态、平均睡眠质量和清醒梦程度等。
 */
@Data
@Schema(description = "分类统计VO")
public class CategoryStatisticsVO {

    /**
     * 统计ID
     */
    @Schema(description = "统计ID", example = "1")
    private Long id;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "10")
    private Long categoryId;

    /**
     * 总梦境数
     */
    @Schema(description = "总梦境数", example = "120")
    private Integer totalDreams;

    /**
     * 公开梦境数
     */
    @Schema(description = "公开梦境数", example = "80")
    private Integer publicDreams;

    /**
     * 私有梦境数
     */
    @Schema(description = "私有梦境数", example = "40")
    private Integer privateDreams;

    /**
     * 已审核梦境数
     */
    @Schema(description = "已审核梦境数", example = "70")
    private Integer approvedDreams;

    /**
     * 待审核梦境数
     */
    @Schema(description = "待审核梦境数", example = "30")
    private Integer pendingDreams;

    /**
     * 已拒绝梦境数
     */
    @Schema(description = "已拒绝梦境数", example = "20")
    private Integer rejectedDreams;

    /**
     * 平均睡眠质量（评分或指数）
     */
    @Schema(description = "平均睡眠质量", example = "4.5")
    private BigDecimal avgSleepQuality;

    /**
     * 平均清醒梦程度（评分或指数）
     */
    @Schema(description = "平均清醒梦程度", example = "3.2")
    private BigDecimal avgLucidityLevel;

    /**
     * 最后统计计算时间
     */
    @Schema(description = "最后统计计算时间", example = "2025-09-21T15:00:00")
    private LocalDateTime lastCalculated;
}
