package com.sf.zhimengjing.common.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Title: BehaviorType
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.enumerate
 * @description: 用户行为类型枚举，用于表示用户在平台上的各种操作行为
 */
public enum BehaviorType {
    /** 页面浏览 */
    PAGE_VIEW("页面浏览"),

    /** 登录 */
    LOGIN("登录"),

    /** 梦境提交 */
    DREAM_SUBMIT("梦境提交"),

    /** 社区分享 */
    COMMUNITY_SHARE("社区分享"),

    /** VIP购买 */
    VIP_PURCHASE("VIP购买"),

    /** 搜索 */
    SEARCH("搜索"),

    /** 评论 */
    COMMENT("评论"),

    /** 点赞 */
    LIKE("点赞");

    @EnumValue
    private final String value;

    BehaviorType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
