package com.sf.zhimengjing.common.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Title: AdminUserDTO
 * @Author 殤枫
 * @Package com.sf.zhimengjing.common.model.dto
 * @description: 后台管理员创建/更新请求数据传输对象
 */
@Data
@Schema(description = "后台管理员创建/更新DTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminUserDTO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名", required = true)
    @NotEmpty(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "真实姓名", required = true)
    @NotEmpty(message = "真实姓名不能为空")
    private String realName;

    @Schema(description = "角色ID", required = true)
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "状态:0-禁用,1-正常", required = true)
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像URL")
    private String avatar;
}
