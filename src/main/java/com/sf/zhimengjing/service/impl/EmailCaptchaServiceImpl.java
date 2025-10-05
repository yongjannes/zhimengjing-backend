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
    public boolean verifyMailCaptcha(String email, String captcha) {
        String hashKey = "login:email:captcha:" + email;
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(hashKey);

        String storedCaptcha = hashOps.get("captcha");

        if (StringUtils.isBlank(storedCaptcha)) {
            log.warn("验证码不存在或已过期，邮箱：{}", email);
            throw new GeneralBusinessException("验证码不存在或已过期");
        }

        boolean isValid = storedCaptcha.equals(captcha);

        if (isValid) {
            // 验证成功后删除验证码
            stringRedisTemplate.delete(hashKey);
            log.info("验证码验证成功，邮箱：{}", email);
        } else {
            log.warn("验证码验证失败，邮箱：{}", email);
            throw new GeneralBusinessException("验证码错误");
        }

        return isValid;
    }
}