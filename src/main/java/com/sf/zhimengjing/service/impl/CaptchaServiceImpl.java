package com.sf.zhimengjing.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.GifCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.core.math.Calculator;
import com.sf.zhimengjing.common.model.vo.CaptchaVO;
import com.sf.zhimengjing.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Title: CaptchaServiceImpl
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.impl
 * @description: 验证码服务实现（动图算数验证码）
 */
@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {
    private final StringRedisTemplate stringRedisTemplate;

    public CaptchaServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public CaptchaVO getCaptcha(String captchaId) {
        GifCaptcha gifCaptcha = CaptchaUtil.createGifCaptcha(130, 48);

        // 使用个位数算数验证码生成器
        MathGenerator mathGenerator = new MathGenerator(1);
        gifCaptcha.setGenerator(mathGenerator);

        gifCaptcha.createCode();
        String captchaExpression = gifCaptcha.getCode();

        // 计算表达式结果
        String expressionToCalc = captchaExpression.replace("=", "");
        double result = Calculator.conversion(expressionToCalc);
        String captchaResult = String.valueOf((int) result);

        log.info("验证码表达式: {}, 计算结果: {}", captchaExpression, captchaResult);

        String captchaImageBase64Data = gifCaptcha.getImageBase64Data();
        captchaId = Optional.ofNullable(captchaId).orElseGet(() -> UUID.randomUUID().toString());

        // 将验证码结果存入Redis，5分钟有效
        stringRedisTemplate.opsForValue().set("login:captcha:" + captchaId, captchaResult, 300, TimeUnit.SECONDS);

        return CaptchaVO.builder()
                .captchaId(captchaId)
                .captchaImage(captchaImageBase64Data)
                .build();
    }
}
