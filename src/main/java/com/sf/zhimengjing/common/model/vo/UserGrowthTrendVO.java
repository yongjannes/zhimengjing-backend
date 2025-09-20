package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @Title: UserGrowthTrendVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 用户增长趋势 VO，用于表示某一时间段内每日新增用户数的统计信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户增长趋势VO，用于统计每日新增用户数")
public class UserGrowthTrendVO {

    /** 日期，表示该条数据对应的具体日期 */
    @Schema(description = "日期")
    private LocalDate date;

    /** 新增用户数，该日期内注册成功的用户数量 */
    @Schema(description = "新增用户数")
    private Long count;
}
