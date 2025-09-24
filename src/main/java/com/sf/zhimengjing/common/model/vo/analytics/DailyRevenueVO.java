package com.sf.zhimengjing.common.model.vo.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Title: DailyRevenueVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 每日收入数据视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyRevenueVO {

    /**
     * 统计日期
     */
    private LocalDate date;

    /**
     * 当日收入
     */
    private BigDecimal revenue;

    /**
     * VIP 用户数
     */
    private Integer vipUsers;

    /**
     * 转化率
     */
    private BigDecimal conversionRate;
}
