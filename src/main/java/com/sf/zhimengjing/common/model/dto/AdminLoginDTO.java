package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @Title: AdminLoginDTO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.dto
 * @description: 管理员登录数据传输对象（DTO）
 */
@Data
@Schema(description = "管理员登录DTO")
public class AdminLoginDTO {
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "验证码不能为空")
    private String captcha;

    @Schema(description = "验证码Key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "验证码Key不能为空")
    private String captchaKey;
}