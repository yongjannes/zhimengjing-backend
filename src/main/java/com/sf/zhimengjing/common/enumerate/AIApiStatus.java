package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: AIApiStatus
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.enumerate
 * @Description: 人工智能 API 调用状态枚举类，定义了接口调用的常见结果状态，
 *               包括成功、失败、超时、限流以及 API 密钥错误，用于统一状态标识，
 *               便于前后端交互和数据库存储。
 */
public enum AIApiStatus {

    /** 成功 */
    SUCCESS("成功"),

    /** 失败 */
    FAILED("失败"),

    /** 超时 */
    TIMEOUT("超时"),

    /** 限流 */
    RATE_LIMITED("限流"),

    /** API密钥错误 */
    API_KEY_ERROR("API密钥错误");

    @EnumValue
    private final String value;

    AIApiStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}