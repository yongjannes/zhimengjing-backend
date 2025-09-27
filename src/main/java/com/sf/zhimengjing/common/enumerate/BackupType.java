package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: BackupType
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.enumerate
 * @description: 备份类型枚举
 */
public enum BackupType {

    /** 完整备份 */
    FULL("FULL", "完整备份"),

    /** 部分备份 */
    PARTIAL("PARTIAL", "部分备份"),

    /** 数据库备份 */
    DATABASE("DATABASE", "数据库备份");

    @EnumValue
    private final String code;
    private final String description;

    BackupType(String code, String description) {
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