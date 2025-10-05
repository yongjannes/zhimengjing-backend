package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @Title: ForgotPasswordResetDTO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.dto
 * @description: 忘记密码-重置密码 DTO
 */
@Data
@Schema(description = "忘记密码-重置密码请求参数")
public class ForgotPasswordResetDTO {

    @Schema(description = "邮箱地址", example = "admin@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "邮箱格式不正确")
    private String email;

    @Schema(description = "验证码", example = "123456")
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须是6位")
    private String captcha;

    @Schema(description = "新密码", example = "newPassword123")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String newPassword;
}