package com.sf.zhimengjing.common.model.dto;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @Title: AdminChangePasswordDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 管理员修改密码数据传输对象（DTO），用于前端提交修改密码请求
 */
@Data
public class AdminChangePasswordDTO {

    /**
     * 旧密码，前端输入的当前密码，用于验证管理员身份
     */
    @ApiModelProperty(value = "旧密码", required = true)
    @NotEmpty(message = "旧密码不能为空")
    private String oldPassword;

    /**
     * 新密码，管理员希望修改的新密码，长度必须在6-20位之间
     */
    @ApiModelProperty(value = "新密码", required = true)
    @NotEmpty(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度必须在6-20位之间")
    private String newPassword;

    /**
     * 确认新密码，前端再次输入的新密码，用于确认与 newPassword 一致
     */
    @ApiModelProperty(value = "确认新密码", required = true)
    @NotEmpty(message = "确认新密码不能为空")
    private String confirmNewPassword;
}
