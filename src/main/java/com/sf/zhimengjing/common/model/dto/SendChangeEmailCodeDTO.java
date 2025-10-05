package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @Title: SendChangeEmailCodeDTO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.dto
 * @description: 发送修改邮箱验证码 DTO
 */
@Data
@Schema(description = "发送修改邮箱验证码请求参数")
public class SendChangeEmailCodeDTO {

    @Schema(description = "新邮箱地址", example = "newemail@example.com")
    @NotBlank(message = "新邮箱不能为空")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "邮箱格式不正确")
    private String newEmail;
}