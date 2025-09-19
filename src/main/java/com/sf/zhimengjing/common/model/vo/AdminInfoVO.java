package com.sf.zhimengjing.common.model.vo;

import io.swagger.annotations.ApiModelProperty;
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
public class AdminInfoVO {
    @ApiModelProperty("管理员ID")
    private Long id;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("真实姓名")
    private String realName;
    @ApiModelProperty("头像URL")
    private String avatar;
    @ApiModelProperty("角色编码列表")
    private List<String> roles;
    @ApiModelProperty("权限标识列表")
    private List<String> permissions;
}