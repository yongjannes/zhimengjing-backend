package com.sf.zhimengjing.service;

/**
 * 邮件验证码服务接口
 *
 * @author zhimengjing
 */
public interface EmailCaptchaService {

    /**
     * 发送邮箱验证码
     * @param email 邮箱地址
     */
    void sendMailCaptcha(String email);

    /**
     * 【新增】异步发送忘记密码邮件
     *
     * @param email 收件人邮箱
     * @param captcha 验证码
     */
    void sendForgotPasswordCodeAsync(String email, String captcha);

    /**
     * 验证邮箱验证码
     *
     * @param email   邮箱地址
     * @param captcha 验证码
     * @return
     */
    String verifyMailCaptcha(String email, String captcha);
}