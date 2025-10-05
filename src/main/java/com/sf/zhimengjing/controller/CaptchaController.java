package com.sf.zhimengjing.controller;

import com.sf.zhimengjing.common.model.vo.CaptchaVO;
import com.sf.zhimengjing.service.EmailCaptchaService;
import com.sf.zhimengjing.service.admin.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

/**
 * @Title: CaptchaController
 * @Author 殇枫
 * @Package com.sf.zhimengjing.controller
 * @description: 验证码
 */
@RestController
@RequestMapping("/captcha")
@Tag(name = "验证码接口", description = "验证码接口相关操作")
public class CaptchaController {

    private final CaptchaService captchaService;
    private final EmailCaptchaService emailCaptchaService;

    public CaptchaController(CaptchaService captchaService, EmailCaptchaService emailCaptchaService) {
        this.captchaService = captchaService;
        this.emailCaptchaService = emailCaptchaService;
    }
    @GetMapping("/graph-captcha")
    @Operation(summary = "获取图形验证码")
    public CaptchaVO getCaptcha(String captchaId) {
        return captchaService.getCaptcha(captchaId);

    }

    /**
     * 获取邮箱验证码
     */
    @GetMapping("/mail/{email}")
    @Operation(summary = "获取邮箱验证码", description = "发送验证码到指定邮箱，60秒内只能发送一次，5分钟有效，24小时内最多发送5次")
    public void getEmailCaptcha(
            @Parameter(description = "邮箱地址", required = true, example = "example@qq.com")
            @PathVariable
            @Email(message = "邮箱格式不正确")
            @NotBlank(message = "邮箱不能为空")
            String email) {
        emailCaptchaService.sendMailCaptcha(email);
    }

    /**
     * 验证邮箱验证码
     */
    @PostMapping("/mail/verify")
    @Operation(summary = "验证邮箱验证码", description = "验证邮箱验证码是否正确，验证成功后验证码自动失效")
    public String verifyEmailCaptcha(
            @Parameter(description = "邮箱地址", required = true, example = "example@qq.com")
            @RequestParam
            @Email(message = "邮箱格式不正确")
            @NotBlank(message = "邮箱不能为空")
            String email,
            @Parameter(description = "验证码", required = true, example = "123456")
            @RequestParam
            @NotBlank(message = "验证码不能为空")
            String captcha) {
        return emailCaptchaService.verifyMailCaptcha(email, captcha);
    }
}