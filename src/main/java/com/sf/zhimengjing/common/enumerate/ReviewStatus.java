package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: ReviewStatus
 * @Author: 殇枫
 * @Description: 报告审核状态枚举，用于表示审核流程的不同阶段
 */
public enum ReviewStatus {

    /** 待审核 */
    PENDING("待审核"),

    /** 审核中 */
    UNDER_REVIEW("审核中"),

    /** 已审核 */
    REVIEWED("已审核"),

    /** 已申诉 */
    APPEALED("已申诉"),

    /** 申诉已处理 */
    APPEAL_RESOLVED("申诉已处理");

    @EnumValue
    private final String value;

    ReviewStatus(String value) {
        this.value = this.name(); // 或使用中文文本
    }

    public String getValue() {
        return value;
    }
}
