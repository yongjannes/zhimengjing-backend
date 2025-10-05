package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @Title: ChangeEmailDTO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.dto
 * @description: 修改邮箱 DTO
 */
@Data
@Schema(description = "修改邮箱请求参数")
public class ChangeEmailDTO {

    @Schema(description = "新邮箱地址", example = "newemail@example.com")
    @NotBlank(message = "新邮箱不能为空")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "邮箱格式不正确")
    private String newEmail;

    @Schema(description = "验证码", example = "123456")
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须是6位")
    private String captcha;
}