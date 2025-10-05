package com.sf.zhimengjing.common.enumerate;

import lombok.Getter;
import java.util.Locale;

/**
 * 邮件模板枚举
 *
 * @author zhimengjing
 */
@Getter
public enum EmailTemplateEnum {

    // 验证码邮件
    VERIFICATION_CODE_EMAIL_HTML("<html><body><div style='padding: 20px; background-color: #f5f5f5;'>" +
            "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
            "<h2 style='color: #333; text-align: center;'>织梦境验证码</h2>" +
            "<p style='color: #666; font-size: 14px;'>尊敬的用户，您好：</p>" +
            "<p style='color: #666; font-size: 14px;'>您的验证码是：</p>" +
            "<h1 style='color: #4a90e2; text-align: center; letter-spacing: 5px; font-size: 32px;'>%s</h1>" +
            "<p style='color: #999; font-size: 12px; text-align: center;'>验证码5分钟内有效，请勿泄露给他人</p>" +
            "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
            "<p style='color: #999; font-size: 12px; text-align: center;'>此邮件由系统自动发送，请勿回复</p>" +
            "</div></div></body></html>", "【织梦境】登录验证码"),

    // 用户被封禁邮件通知
    USER_BANNED_EMAIL("<html><body><div style='padding: 20px; background-color: #f5f5f5;'>" +
            "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
            "<h2 style='color: #e74c3c; text-align: center;'>账号封禁通知</h2>" +
            "<p style='color: #666; font-size: 14px;'>尊敬的用户，您好：</p>" +
            "<p style='color: #666; font-size: 14px;'>您的账号已被管理员封禁。</p>" +
            "<p style='color: #666; font-size: 14px;'><strong>封禁原因：</strong>%s</p>" +
            "<p style='color: #999; font-size: 12px;'>如有疑问，请联系客服。</p>" +
            "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
            "<p style='color: #999; font-size: 12px; text-align: center;'>此邮件由系统自动发送，请勿回复</p>" +
            "</div></div></body></html>", "【织梦境】账号封禁通知"),

    // 欢迎注册邮件
    WELCOME_EMAIL("<html><body><div style='padding: 20px; background-color: #f5f5f5;'>" +
            "<div style='max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
            "<h2 style='color: #4a90e2; text-align: center;'>欢迎加入织梦境</h2>" +
            "<p style='color: #666; font-size: 14px;'>尊敬的用户，您好：</p>" +
            "<p style='color: #666; font-size: 14px;'>感谢您注册织梦境，开启您的梦境解析之旅！</p>" +
            "<p style='color: #666; font-size: 14px;'>在这里，您可以记录梦境、获取AI解析、与社区交流。</p>" +
            "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
            "<p style='color: #999; font-size: 12px; text-align: center;'>此邮件由系统自动发送，请勿回复</p>" +
            "</div></div></body></html>", "【织梦境】欢迎注册"),

    ;

    private final String template;
    private final String subject;

    EmailTemplateEnum(String template, String subject) {
        this.template = template;
        this.subject = subject;
    }

    public String value() {
        return this.template;
    }

    /**
     * 模板参数填充
     * @param args 参数集
     * @return 填充后的字符串
     */
    public String set(Object... args) {
        return String.format(Locale.ROOT, this.template, args);
    }
}