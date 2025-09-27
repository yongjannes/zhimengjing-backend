package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: SettingType
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.enumerate
 * @Description: 系统配置类型枚举
 */
public enum SettingType {

    /** 字符串类型 */
    STRING("STRING", "字符串"),

    /** 整数类型 */
    INTEGER("INTEGER", "整数"),

    /** 布尔类型 */
    BOOLEAN("BOOLEAN", "布尔值"),

    /** JSON对象类型 */
    JSON("JSON", "JSON对象");

    @EnumValue
    private final String code;
    private final String description;

    SettingType(String code, String description) {
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