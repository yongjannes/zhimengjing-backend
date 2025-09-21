package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * @Title: CategoryDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @description: 梦境分类创建/更新 DTO，用于接收前端提交的分类信息。
 *               合并创建和更新场景，部分字段可选或有默认值。
 */
@Data
@Schema(description = "梦境分类创建/更新DTO")
public class CategoryDTO {

    /**
     * 分类名称（必填，最大长度100）
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100字符")
    @Schema(description = "分类名称", example = "梦境日记")
    private String name;

    /**
     * 分类描述（可选，最大长度1000）
     */
    @Size(max = 1000, message = "分类描述长度不能超过1000字符")
    @Schema(description = "分类描述", example = "用于记录用户梦境的分类")
    private String description;

    /**
     * 分类图标URL（可选，最大长度200）
     */
    @Size(max = 200, message = "图标URL长度不能超过200字符")
    @Schema(description = "分类图标URL", example = "https://example.com/icon.png")
    private String icon;

    /**
     * 分类颜色（可选，格式必须为#RRGGBB）
     */
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "颜色格式不正确")
    @Schema(description = "分类颜色", example = "#FF5733")
    private String color;

    /**
     * 父分类ID（必填，默认0表示顶级分类）
     */
    @NotNull(message = "父分类ID不能为空")
    @Schema(description = "父分类ID", example = "0")
    private Integer parentId = 0;

    /**
     * 排序序号（最小0，默认0）
     */
    @Min(value = 0, message = "排序不能小于0")
    @Schema(description = "排序序号", example = "0")
    private Integer sortOrder = 0;

    /**
     * 是否启用（必填，默认true）
     */
    @NotNull(message = "是否启用不能为空")
    @Schema(description = "是否启用", example = "true")
    private Boolean isActive = true;

    /**
     * 是否系统内置分类（可选，默认false）
     */
    @Schema(description = "是否系统内置分类", example = "false")
    private Boolean isSystem = false;
}
