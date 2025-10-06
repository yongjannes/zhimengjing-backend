package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: AdminPermission
 * @Author 殇枫
 * @Package com.sf.zhimengjing.entity.admin
 * @description: 后台权限表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("admin_permission")
@ApiModel(value = "AdminPermission对象", description = "后台权限表")
public class AdminPermission extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("权限编码")
    private String permissionCode;

    @ApiModelProperty("权限描述")
    private String description;
}