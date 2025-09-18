package com.sf.zhimengjing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Title: User
 * @Author 殇枫
 * @Package com.sf.zhimengjing.entity
 * @description: 用户表
 */
@Data
@TableName("user")
@ApiModel(value = "User对象", description = "用户表")
public class User extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户昵称")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("密码")
    @TableField("password")
    private String password;

    @ApiModelProperty("账号")
    @TableField("user_account")
    private String userAccount;

    @ApiModelProperty("用户角色：user / admin")
    @TableField("user_role")
    private String userRole;

    @ApiModelProperty("头像")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty("逻辑删除：1删除/0存在")
    @TableField("is_delete")
    private Boolean isDelete;

    @ApiModelProperty("性别")
    @TableField("gender")
    private Boolean gender;

    @ApiModelProperty("状态：1正常0禁用")
    @TableField("status")
    private Boolean status;

    @ApiModelProperty("手机号")
    @TableField("phone")
    private String phone;
}
