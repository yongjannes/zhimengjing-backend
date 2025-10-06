package com.sf.zhimengjing.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    @Schema(description = "系统内置角色（0:否，1:是）")
    private Boolean isSystem;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "角色状态（0:禁用，1:启用）")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;


}