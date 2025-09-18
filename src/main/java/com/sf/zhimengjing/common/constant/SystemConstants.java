package com.sf.zhimengjing.common.constant;

/**
 * @Title: SystemConstants
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.constant
 * @description: 系统常量类，存放用户状态、VIP等级、梦境状态、解析状态、支付状态、文件类型及缓存键前缀等
 */
public class SystemConstants {

    // 用户状态
    public static final Integer USER_STATUS_DISABLED = 0; // 禁用
    public static final Integer USER_STATUS_NORMAL = 1;   // 正常

    // VIP等级
    public static final Integer VIP_LEVEL_NORMAL = 0;     // 普通用户
    public static final Integer VIP_LEVEL_MONTHLY = 1;    // 月度会员
    public static final Integer VIP_LEVEL_YEARLY = 2;     // 年度会员
    public static final Integer VIP_LEVEL_LIFETIME = 3;   // 永久会员

    // 梦境状态
    public static final Integer DREAM_STATUS_PRIVATE = 0; // 私密
    public static final Integer DREAM_STATUS_PUBLIC = 1;  // 公开

    // 解析状态
    public static final Integer ANALYSIS_STATUS_PENDING = 0;   // 待解析
    public static final Integer ANALYSIS_STATUS_COMPLETED = 1; // 解析完成
    public static final Integer ANALYSIS_STATUS_FAILED = 2;    // 解析失败

    // 支付状态
    public static final Integer PAYMENT_STATUS_PENDING = 0;   // 待支付
    public static final Integer PAYMENT_STATUS_PAID = 1;      // 已支付
    public static final Integer PAYMENT_STATUS_FAILED = 2;    // 支付失败
    public static final Integer PAYMENT_STATUS_REFUNDED = 3;  // 已退款

    // 文件类型
    public static final String FILE_TYPE_IMAGE = "image";       // 图片
    public static final String FILE_TYPE_VOICE = "voice";       // 语音
    public static final String FILE_TYPE_VIDEO = "video";       // 视频
    public static final String FILE_TYPE_DOCUMENT = "document"; // 文档

    // 缓存键前缀
    public static final String CACHE_PREFIX_USER = "user:";       // 用户缓存
    public static final String CACHE_PREFIX_DREAM = "dream:";     // 梦境缓存
    public static final String CACHE_PREFIX_ANALYSIS = "analysis:"; // 解析缓存
    public static final String CACHE_PREFIX_VIP = "vip:";         // VIP缓存
    public static final String CACHE_PREFIX_REPORT = "report:";   // 报告缓存
}