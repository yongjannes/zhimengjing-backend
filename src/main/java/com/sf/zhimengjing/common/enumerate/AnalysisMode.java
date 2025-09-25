package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: AnalysisMode
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.enumerate
 * @Description: 梦境解析模式枚举类，用于表示不同的梦境解析方式。
 *               枚举值包括心理学解析、象征意义解析、情感分析和综合分析，
 *               用于控制系统解析梦境时采用的分析方法。
 */
public enum AnalysisMode {

    /** 心理学解析 */
    PSYCHOLOGY("心理学解析"),

    /** 象征意义解析 */
    SYMBOLIC("象征意义解析"),

    /** 情感分析 */
    EMOTION("情感分析"),

    /** 综合分析 */
    COMPREHENSIVE("综合分析");

    @EnumValue
    private final String value;

    AnalysisMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}