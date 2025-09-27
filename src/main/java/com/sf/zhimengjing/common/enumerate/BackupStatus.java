package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: BackupStatus
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.enumerate
 * @description: 系统备份状态枚举
 */
public enum BackupStatus {

    /** 等待中 */
    PENDING("PENDING", "等待中"),

    /** 处理中 */
    PROCESSING("PROCESSING", "处理中"),

    /** 成功 */
    SUCCESS("SUCCESS", "成功"),

    /** 失败 */
    FAILED("FAILED", "失败");

    @EnumValue
    private final String code;
    private final String description;

    BackupStatus(String code, String description) {
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