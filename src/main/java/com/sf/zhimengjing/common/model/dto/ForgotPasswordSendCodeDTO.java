package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Title: ForgotPasswordSendCodeDTO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.dto
 * @description: 忘记密码-发送验证码 DTO
 */
@Data
@Schema(description = "忘记密码-发送验证码请求参数")
public class ForgotPasswordSendCodeDTO {

    @Schema(description = "用户名或邮箱", example = "admin@example.com")
    @NotBlank(message = "用户名或邮箱不能为空")
    private String identifier;
}