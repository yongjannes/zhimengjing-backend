package com.sf.zhimengjing.common.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Title: CategoryAttributeDTO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.dto
 * @description: 梦境分类属性数据传输对象
 */
@Data
@Schema(description = "分类自定义属性DTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryAttributeDTO {

    @NotBlank(message = "属性名称不能为空")
    @Schema(description = "属性名称", example = "清醒度")
    private String attributeName;

    @NotBlank(message = "属性类型不能为空")
    @Schema(description = "属性类型", example = "NUMBER")
    private String attributeType; // "TEXT", "NUMBER", "BOOLEAN", "DATE"

    @NotNull(message = "是否必填不能为空")
    @Schema(description = "是否必填", example = "true")
    private Boolean isRequired;

    @Schema(description = "默认值", example = "5")
    private String defaultValue;
}
