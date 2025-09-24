package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @Title: CohortAnalysisVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 群组分析 VO，用于展示特定用户群组的留存情况，
 *               包含群组标识、开始日期、用户总数及对应的留存率数据。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "群组分析VO")
public class CohortAnalysisVO {
    @Schema(description = "群组标识")
    private String cohortId;

    @Schema(description = "群组开始日期")
    private LocalDate startDate;

    @Schema(description = "群组用户数")
    private Long userCount;

    @Schema(description = "留存率数据")
    private List<RetentionDataVO> retentionData;
}