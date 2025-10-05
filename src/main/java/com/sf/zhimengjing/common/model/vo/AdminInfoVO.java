package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * @Title: AdminInfoVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 管理员信息视图对象（VO）
 */
@Data
@Builder
@Schema(description = "管理员信息VO")
public class AdminInfoVO {
    @Schema(description = "管理员ID")
    private Long id;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "真实姓名")
    private String realName;
    @Schema(description = "头像VO")
    private AvatarVO avatar;
    @Schema(description = "角色编码列表")
    private List<String> roles;
    @Schema(description = "权限标识列表")
    private List<String> permissions;
}