package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Title: AdminProfileVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @description: 管理员个人中心信息VO
 */
@Data
@Builder
@Schema(description = "管理员个人中心信息VO")
public class AdminProfileVO {

    @Schema(description = "管理员ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "状态:0-禁用,1-正常,2-锁定")
    private Integer status;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}