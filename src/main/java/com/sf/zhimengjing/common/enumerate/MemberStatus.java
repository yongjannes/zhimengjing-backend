package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: MemberStatus
 * @Author: 殇枫
 * @Description: 会员状态枚举，用于表示VIP会员的不同状态
 */
public enum MemberStatus {

    /** 正常 */
    ACTIVE("正常"),

    /** 已过期 */
    EXPIRED("已过期"),

    /** 已暂停 */
    SUSPENDED("已暂停"),

    /** 已取消 */
    CANCELLED("已取消"),

    /** 待升级 */
    PENDING_UPGRADE("待升级"),

    /** 待续费 */
    PENDING_RENEWAL("待续费");

    @EnumValue
    private final String value;

    MemberStatus(String value) {
        this.value = this.name();
    }

    public String getValue() {
        return value;
    }
}