package com.sf.zhimengjing.common.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @Title: TagDTO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.dto
 * @description: 梦境标签创建/更新 DTO
 */
@Data
@Schema(description = "梦境标签创建/更新DTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagDTO {

    /**
     * 标签名称（必填，最大长度50）
     */
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称长度不能超过50字符")
    @Schema(description = "标签名称", example = "噩梦")
    private String name;

    /**
     * 标签颜色（可选，格式必须为#RRGGBB）
     */
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "颜色格式不正确")
    @Schema(description = "标签颜色", example = "#FF5733")
    private String color;

    /**
     * 标签描述（可选，最大长度500）
     */
    @Size(max = 500, message = "标签描述长度不能超过500字符")
    @Schema(description = "标签描述", example = "表示令人恐惧的梦境")
    private String description;

    /**
     * 是否启用（必填，默认true）
     */
    @NotNull(message = "是否启用不能为空")
    @Schema(description = "是否启用", example = "true")
    private Boolean isActive = true;
}