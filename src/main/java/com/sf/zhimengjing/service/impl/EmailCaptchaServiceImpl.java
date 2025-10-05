package com.sf.zhimengjing.service.impl;

import com.sf.zhimengjing.common.enumerate.EmailTemplateEnum;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.util.EmailApi;
import com.sf.zhimengjing.service.EmailCaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 邮件验证码服务实现类
 *
 * @author zhimengjing
 */
@Service
@Slf4j
public class EmailCaptchaServiceImpl implements EmailCaptchaService {

    private final StringRedisTemplate stringRedisTemplate;
    private final EmailApi emailApi;

    public EmailCaptchaServiceImpl(StringRedisTemplate stringRedisTemplate, EmailApi emailApi) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.emailApi = emailApi;
    }

    @Override
    @Async
    public void sendMailCaptcha(String email) {
        String hashKey = "login:email:captcha:" + email;
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(hashKey);

        // 初始检查
        String lastSendTimestamp = hashOps.get("lastSendTimestamp");
        String sendCount = hashOps.get("sendCount");
        String captcha = hashOps.get("captcha");

        // 判断发送次数是否超过限制
        if (StringUtils.isNotBlank(sendCount) && Integer.parseInt(sendCount) >= 5) {
            hashOps.expire(24, TimeUnit.HOURS); // 重新设置过期时间为24H
            throw new GeneralBusinessException("发送次数过多，请24小时后再试");
        }

        // 判断发送频率是否过高
        if (StringUtils.isNotBlank(lastSendTimestamp)) {
            long lastSendTime = Long.parseLong(lastSendTimestamp);
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastSendTime;
            long interval = 60 * 1000; // 60秒
            if (elapsedTime < interval) {
                long remainingSeconds = (interval - elapsedTime) / 1000;
                throw new GeneralBusinessException("发送频繁，请" + remainingSeconds + "秒后再试");
            }
        }

        // 更新发送次数
        int newSendCount = StringUtils.isNotBlank(sendCount) ? Integer.parseInt(sendCount) + 1 : 1;

        // 生成新验证码（6位数字）
        captcha = RandomStringUtils.randomNumeric(6);

        // 发送邮件
        boolean success = emailApi.sendHtmlEmail(
                EmailTemplateEnum.VERIFICATION_CODE_EMAIL_HTML.getSubject(),
                EmailTemplateEnum.VERIFICATION_CODE_EMAIL_HTML.set(captcha),
                email
        );

        if (!success) {
            throw new GeneralBusinessException("发送邮件失败，请稍后重试");
        }

        // 更新 Redis 中的信息
        hashOps.put("captcha", captcha);
        hashOps.put("lastSendTimestamp", String.valueOf(System.currentTimeMillis()));
        hashOps.put("sendCount", String.valueOf(newSendCount));
        hashOps.expire(5, TimeUnit.MINUTES); // 设置过期时间为5分钟

        log.info("邮件验证码发送成功，邮箱：{}，发送次数：{}", email, newSendCount);
    }

    @Override
    @Async
    public void sendForgotPasswordCodeAsync(String email, String captcha) {
        // 1. 定义与忘记密码相关的 Redis Key
        String hashKey = "forgot:password:captcha:" + email;
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(hashKey);

        // 2. 【逻辑补充】检查发送频率和次数 (逻辑从 sendMailCaptcha 复制并适配)
        String lastSendTimestamp = hashOps.get("lastSendTimestamp");
        String sendCount = hashOps.get("sendCount");

        // 判断发送次数是否超过限制
        if (StringUtils.isNotBlank(sendCount) && Integer.parseInt(sendCount) >= 5) {
            hashOps.expire(24, TimeUnit.HOURS); // 达到上限后，将key的过期时间延长至24小时
            log.warn("邮箱 {} 忘记密码验证码发送次数过多，今日已被锁定", email);
            return; // 中断执行
        }

        // 判断发送频率是否过高
        if (StringUtils.isNotBlank(lastSendTimestamp)) {
            long lastSendTime = Long.parseLong(lastSendTimestamp);
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastSendTime;
            long interval = 60 * 1000; // 60秒
            if (elapsedTime < interval) {
                long remainingSeconds = (interval - elapsedTime) / 1000;
                log.warn("邮箱 {} 忘记密码验证码发送过于频繁，请 {} 秒后再试", email, remainingSeconds);
                return; // 中断执行
            }
        }

        // 3. 【逻辑补充】更新发送次数
        int newSendCount = StringUtils.isNotBlank(sendCount) ? Integer.parseInt(sendCount) + 1 : 1;

        // 4. 发送邮件（这是原有的逻辑）
        log.info("启动异步任务：发送忘记密码邮件至 {}", email);
        try {
            boolean success = emailApi.sendHtmlEmail(
                    EmailTemplateEnum.FORGOT_PASSWORD_EMAIL_HTML.getSubject(),
                    EmailTemplateEnum.FORGOT_PASSWORD_EMAIL_HTML.set(captcha),
                    email
            );

            if (success) {
                // 5. 【逻辑补充】邮件发送成功后，才将验证码和发送记录写入Redis
                hashOps.put("captcha", captcha);
                hashOps.put("lastSendTimestamp", String.valueOf(System.currentTimeMillis()));
                hashOps.put("sendCount", String.valueOf(newSendCount));
                hashOps.expire(5, TimeUnit.MINUTES); // 验证码5分钟有效
                log.info("异步邮件发送成功，收件人: {}", email);
            } else {
                log.error("异步邮件发送失败，收件人: {}", email);
            }
        } catch (Exception e) {
            log.error("异步邮件发送时发生未知异常，收件人: {}", email, e);
        }
    }

    @Override
    public String verifyMailCaptcha(String email, String captcha) {
        String hashKey = "login:email:captcha:" + email;
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(hashKey);

        String storedCaptcha = hashOps.get("captcha");

        if (StringUtils.isBlank(storedCaptcha)) {
            log.warn("验证码不存在或已过期，邮箱：{}", email);
            throw new GeneralBusinessException("验证码不存在或已过期");
        }


        // 验证验证码是否正确
        if (!storedCaptcha.equals(captcha)) {
            log.warn("验证码验证失败，邮箱：{}", email);
            throw new GeneralBusinessException("验证码错误");
        }
        // 验证成功后删除验证码
        stringRedisTemplate.delete(hashKey);
        log.info("验证码验证成功，邮箱：{}", email);
        return hashKey;
    }
}