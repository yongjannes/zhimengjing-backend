package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum ComplaintType {

    /** 不当内容 */
    INAPPROPRIATE_CONTENT("不当内容"),

    /** 虚假信息 */
    FALSE_INFORMATION("虚假信息"),

    /** 抄袭 */
    PLAGIARISM("抄袭"),

    /** 垃圾内容/广告 */
    SPAM("垃圾内容/广告"),

    /** 其他类型 */
    OTHER("其他");

    @EnumValue
    private final String value;

    ComplaintType(String value) {
        this.value = this.name(); // 或 value 可以用自定义中文文本
    }

    public String getValue() {
        return value;
    }
}
