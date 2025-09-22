package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum ReviewResult {

    /** 审核通过 */
    APPROVED("审核通过"),

    /** 审核驳回 */
    REJECTED("审核驳回"),

    /** 违规报告 */
    VIOLATION("违规报告");

    @EnumValue
    private final String value;

    ReviewResult(String value) {
        this.value = this.name(); // 或者直接使用自定义中文文本
    }

    public String getValue() {
        return value;
    }
}
