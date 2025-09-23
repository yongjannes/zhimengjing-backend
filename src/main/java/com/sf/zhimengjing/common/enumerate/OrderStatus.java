package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: OrderStatus
 * @Author: 殇枫
 * @Description: 订单状态枚举，用于表示VIP订单的不同状态
 */
public enum OrderStatus {

    /** 待支付 */
    PENDING("待支付"),

    /** 已支付 */
    PAID("已支付"),

    /** 已取消 */
    CANCELLED("已取消"),

    /** 已过期 */
    EXPIRED("已过期"),

    /** 退款中 */
    REFUNDING("退款中"),

    /** 已退款 */
    REFUNDED("已退款"),

    /** 已完成 */
    COMPLETED("已完成");

    @EnumValue
    private final String value;

    OrderStatus(String value) {
        this.value = this.name();
    }

    public String getValue() {
        return value;
    }
}