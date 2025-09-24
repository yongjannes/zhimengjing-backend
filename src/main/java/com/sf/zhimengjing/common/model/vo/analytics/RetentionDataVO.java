package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Title: RetentionDataVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 留存数据 VO，用于展示特定日期的用户留存情况，
 *               包含留存率、用户总数及留存用户数等指标。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "留存数据VO")
public class RetentionDataVO {
    @Schema(description = "日期")
    private LocalDate date;

    @Schema(description = "留存率")
    private BigDecimal retentionRate;

    @Schema(description = "用户数量")
    private Long userCount;

    @Schema(description = "留存用户数")
    private Long retainedUserCount;
}