package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: PaymentStatus
 * @Author: 殇枫
 * @Description: 支付状态枚举，用于表示支付的不同状态
 */
public enum PaymentStatus {

    /** 待支付 */
    PENDING("待支付"),

    /** 处理中 */
    PROCESSING("处理中"),

    /** 支付成功 */
    SUCCESS("支付成功"),

    /** 支付失败 */
    FAILED("支付失败"),

    /** 已取消 */
    CANCELLED("已取消"),

    /** 已退款 */
    REFUNDED("已退款");

    @EnumValue
    private final String value;

    PaymentStatus(String value) {
        this.value = this.name();
    }

    public String getValue() {
        return value;
    }
}