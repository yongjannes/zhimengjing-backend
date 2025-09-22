package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum ComplaintStatus {

    /** 待处理 */
    PENDING("待处理"),

    /** 处理中 */
    PROCESSING("处理中"),

    /** 已处理/已解决 */
    RESOLVED("已处理"),

    /** 驳回 */
    REJECTED("驳回");

    @EnumValue
    private final String value;

    ComplaintStatus(String value) {
        this.value = this.name(); // 或 value 可以用自定义文本
    }

    public String getValue() {
        return value;
    }
}
