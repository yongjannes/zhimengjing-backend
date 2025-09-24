package com.sf.zhimengjing.common.model.vo.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Title: DailyUserGrowthVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 每日用户增长数据视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyUserGrowthVO {

    /**
     * 统计日期
     */
    private LocalDate date;

    /**
     * 总用户数
     */
    private Integer totalUsers;

    /**
     * 新增用户数
     */
    private Integer newUsers;

    /**
     * 活跃用户数
     */
    private Integer activeUsers;

    /**
     * 用户留存率
     */
    private BigDecimal retentionRate;
}
