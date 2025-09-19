package com.sf.zhimengjing.common.model.dto;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @Title: AdminLoginDTO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.dto
 * @description: 管理员登录数据传输对象（DTO）
 */
@Data
public class AdminLoginDTO {
    @ApiModelProperty(value = "用户名", required = true)
    @NotEmpty(message = "用户名不能为空")
    private String username;
    @ApiModelProperty(value = "密码", required = true)
    @NotEmpty(message = "密码不能为空")
    private String password;
    @ApiModelProperty(value = "验证码", required = true)
    @NotEmpty(message = "验证码不能为空")
    private String captcha;
    @ApiModelProperty(value = "验证码Key", required = true)
    @NotEmpty(message = "验证码Key不能为空")
    private String captchaKey;
}