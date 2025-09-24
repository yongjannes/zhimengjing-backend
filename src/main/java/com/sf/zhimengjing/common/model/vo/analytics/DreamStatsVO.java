package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * @Title: DreamStatsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 梦境统计 VO，用于展示平台梦境相关的统计信息，
 *               包含总梦境数、新增梦境数、平均梦境长度、梦境完成率及分享率。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "梦境统计VO")
public class DreamStatsVO {
    @Schema(description = "总梦境数")
    private Long totalDreams;

    @Schema(description = "新增梦境数")
    private Long newDreams;

    @Schema(description = "平均梦境长度")
    private BigDecimal avgDreamLength;

    @Schema(description = "梦境完成率")
    private BigDecimal completionRate;

    @Schema(description = "梦境分享率")
    private BigDecimal shareRate;
}