package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: SettingCategory
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.enumerate
 * @description: 系统配置分类枚举
 */
public enum SettingCategory {

    /** 基础配置 */
    BASIC("BASIC", "基础配置"),

    /** 安全配置 */
    SECURITY("SECURITY", "安全配置"),

    /** 第三方服务配置 */
    THIRD_PARTY("THIRD_PARTY", "第三方服务"),

    /** 系统配置 */
    SYSTEM("SYSTEM", "系统配置");

    @EnumValue
    private final String code;
    private final String description;

    SettingCategory(String code, String description) {
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