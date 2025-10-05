package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @Title: AdminUpdateInfoDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @description: 管理员更新个人信息DTO
 */
@Data
@Schema(description = "管理员更新个人信息DTO")
public class AdminUpdateInfoDTO {

    @Schema(description = "真实姓名")
    @Length(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "头像URL")
    private String avatar;
}