package com.sf.zhimengjing.common.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @Title: AdminUserVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 后台管理员列表视图对象，用于列表展示管理员基本信息
 */
@Data
@Schema(description = "后台管理员列表VO")
public class AdminUserVO {

    @Schema(description = "管理员ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "状态:0-禁用,1-正常")
    private Integer status;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
