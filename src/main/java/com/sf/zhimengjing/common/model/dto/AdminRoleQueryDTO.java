package com.sf.zhimengjing.common.model.dto;

import com.sf.zhimengjing.common.model.BasePageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: AdminRoleQueryDTO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.dto
 * @description: 角色查询参数DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色查询参数DTO")
public class AdminRoleQueryDTO extends BasePageDTO {

    @Schema(description = "角色名称，用于模糊查询")
    private String roleName;

    @Schema(description = "角色状态 (0:禁用, 1:启用)")
    private Integer status;
}