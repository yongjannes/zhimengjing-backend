package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: EmotionLabel
 * @Description: 情感标签枚举类，常用于情感分析任务（如评论情感分类、文本情感识别）。
 *               该枚举包含积极、消极、中性三种情感状态。
 */
public enum EmotionLabel {

    /** 积极情感，例如“很开心”“非常好”等 */
    POSITIVE("积极"),

    /** 消极情感，例如“糟糕”“不满意”等 */
    NEGATIVE("消极"),

    /** 中性情感，例如“还行”“一般般”等 */
    NEUTRAL("中性");

    /**
     * 数据库存储的值。
     *
     * 使用 @EnumValue 注解后，MyBatis-Plus 会在持久化时
     * 使用该字段的值（而不是枚举常量名）。
     */
    @EnumValue
    private final String value;

    /**
     * 构造函数，给枚举常量绑定具体的存储值
     * @param value 中文描述
     */
    EmotionLabel(String value) {
        this.value = value;
    }

    /**
     * 获取枚举值对应的中文描述
     * @return 中文字符串
     */
    public String getValue() {
        return value;
    }
}
