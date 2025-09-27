package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: ChangeType
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.enumerate
 * @description: 配置变更类型枚举
 */
public enum ChangeType {

    /** 创建 */
    CREATE("CREATE", "创建"),

    /** 更新 */
    UPDATE("UPDATE", "更新"),

    /** 删除 */
    DELETE("DELETE", "删除");

    @EnumValue
    private final String code;
    private final String description;

    ChangeType(String code, String description) {
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