package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: ServiceType
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.enumerate
 * @description: 第三方服务类型枚举
 */
public enum ServiceType {

    /** 邮件服务 */
    EMAIL("EMAIL", "邮件服务"),

    /** 短信服务 */
    SMS("SMS", "短信服务"),

    /** 存储服务 */
    STORAGE("STORAGE", "存储服务"),

    /** 支付服务 */
    PAYMENT("PAYMENT", "支付服务");

    @EnumValue
    private final String code;
    private final String description;

    ServiceType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}