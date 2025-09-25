package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: LanguageStyle
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.enumerate
 * @Description: 语言风格枚举类，用于表示 AI 或系统输出文本的风格类型。
 *               枚举值包括友好亲切、专业严谨、轻松随意和温暖关怀，
 *               可用于控制文本输出的语气和风格。
 */
public enum LanguageStyle {

    /** 友好亲切 */
    FRIENDLY("友好亲切"),

    /** 专业严谨 */
    PROFESSIONAL("专业严谨"),

    /** 轻松随意 */
    CASUAL("轻松随意"),

    /** 温暖关怀 */
    WARM("温暖关怀");

    @EnumValue
    private final String value;

    LanguageStyle(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}