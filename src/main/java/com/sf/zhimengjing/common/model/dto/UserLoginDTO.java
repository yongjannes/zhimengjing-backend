package com.sf.zhimengjing.common.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @Title: UserLoginDTO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.dto
 * @description: 用户登录请求数据传输对象
 */
@Data
public class UserLoginDTO {
    @NotBlank(message = "账号不能为空")
    private String userAccount;// 用户账号
    @Size(min = 6, max = 18, message = "用户密码长度需在6-18位")
    private String password;// 密码
    @NotBlank(message = "验证码id不能为空")
    private String captchaId;// 验证码id
    @NotBlank(message = "验证码内容不能为空")
    private String captcha;// 验证码内容
}
