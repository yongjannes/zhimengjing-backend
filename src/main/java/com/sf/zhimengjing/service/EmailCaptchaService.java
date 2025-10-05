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
     * 验证邮箱验证码
     *
     * @param email   邮箱地址
     * @param captcha 验证码
     * @return
     */
    String verifyMailCaptcha(String email, String captcha);
}