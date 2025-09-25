package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;


/**
 * @Title: AnalysisDepth
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.enumerate
 * @Description: 分析深度枚举类，用于表示系统中 AI 或其他分析功能的解析级别。
 *               枚举值包括基础解析、详细解析和专业解析，用于控制分析结果的详细程度。
 */
public enum AnalysisDepth {

    /** 基础解析 */
    BASIC("基础解析"),

    /** 详细解析 */
    DETAILED("详细解析"),

    /** 专业解析 */
    PROFESSIONAL("专业解析");

    @EnumValue
    private final String value;

    AnalysisDepth(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}