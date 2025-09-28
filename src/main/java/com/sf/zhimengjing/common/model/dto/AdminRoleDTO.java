package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "后台角色数据传输对象")
public class AdminRoleDTO {

    @Schema(description = "角色ID（更新时需要）")
    private Long id;

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;

    @NotBlank(message = "角色编码不能为空")
    @Schema(description = "角色编码（唯一）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleCode;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "权限标识集合")
    private List<String> permissions;

    @Schema(description = "角色状态（0:禁用，1:启用）")
    private Integer status;
}