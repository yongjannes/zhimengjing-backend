package com.sf.zhimengjing.common.enumerate;

import lombok.Getter;

/**
 * @Title: ResultEnum
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.enumerate
 * @description: 错误码枚举类
 */
@Getter
public enum ResultEnum {
    // 通用错误码
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "此操作需要登陆系统！"),
    FORBIDDEN(403, "权限不足，无权操作！"),
    NOT_FOUND(404, "资源不存在"),

    // 用户相关错误码
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    USERNAME_EXISTS(1003, "用户名已存在"),
    EMAIL_EXISTS(1004, "邮箱已存在"),
    PHONE_EXISTS(1005, "手机号已存在"),
    PASSWORD_ERROR(1006, "密码错误"),
    LOGIN_EXPIRED(1007, "登录已过期"),
    CAPTCHA_ERROR(1008, "验证码错误"),

    // 梦境相关错误码
    DREAM_NOT_FOUND(2001, "梦境不存在"),
    DREAM_PERMISSION_DENIED(2002, "无权限访问此梦境"),
    DREAM_ANALYSIS_FAILED(2003, "梦境解析失败"),
    DREAM_ALREADY_ANALYZED(2004, "梦境已解析"),

    // VIP相关错误码
    VIP_NOT_ACTIVE(3001, "VIP未激活"),
    VIP_EXPIRED(3002, "VIP已过期"),
    VIP_LEVEL_INSUFFICIENT(3003, "VIP等级不足"),
    PACKAGE_NOT_FOUND(3004, "套餐不存在"),
    PACKAGE_DISABLED(3005, "套餐已下架"),

    // 支付相关错误码
    ORDER_NOT_FOUND(4001, "订单不存在"),
    ORDER_EXPIRED(4002, "订单已过期"),
    PAYMENT_FAILED(4003, "支付失败"),
    REFUND_FAILED(4004, "退款失败"),

    // 文件相关错误码
    FILE_UPLOAD_FAILED(5001, "文件上传失败"),
    FILE_TYPE_NOT_SUPPORTED(5002, "文件类型不支持"),
    FILE_SIZE_EXCEEDED(5003, "文件大小超限"),
    FILE_NOT_FOUND(5004, "文件不存在");

    // HTTP 状态码
    private final int code;
    // 提示信息
    private final String message;

    ResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}