package com.sf.zhimengjing.controller;

import com.sf.zhimengjing.common.model.vo.CaptchaVO;
import com.sf.zhimengjing.service.admin.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }
    @GetMapping("/graph-captcha")
    @Operation(summary = "获取图形验证码")
    public CaptchaVO getCaptcha(String captchaId) {
        return captchaService.getCaptcha(captchaId);

    }
}