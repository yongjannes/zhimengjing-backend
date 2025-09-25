package com.sf.zhimengjing.common.model.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @Title: AIPromptTemplateDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: AI提示词模板数据传输对象，用于前后端交互
 */
@Data
@Schema(description = "AI提示词模板DTO")
public class AIPromptTemplateDTO implements Serializable {

    @Schema(description = "模板记录ID（主键ID）")
    private Long id;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "模板内容")
    private String templateContent;

    @Schema(description = "模板类型")
    private String templateType;

    @Schema(description = "变量定义")
    private String variables;

    @Schema(description = "关联模型编码")
    private String modelCode;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "是否激活")
    private Boolean isActive;

    @Schema(description = "创建人ID")
    private Long createdBy;

    /**
     * 提示词模板创建/更新请求 DTO
     */
    @Data
    @Schema(description = "提示词模板请求DTO")
    public static class TemplateRequestDTO implements Serializable {

        @Schema(description = "模板编码")
        @NotBlank
        private String templateCode;

        @Schema(description = "模板名称")
        @NotBlank
        private String templateName;

        @Schema(description = "模板内容")
        @NotBlank
        private String templateContent;

        @Schema(description = "模板类型")
        private String templateType;

        @Schema(description = "变量定义")
        private String variables;

        @Schema(description = "关联模型编码")
        private String modelCode;
    }

    /**
     * 模板测试 DTO
     */
    @Data
    @Schema(description = "模板测试DTO")
    public static class TemplateTestDTO implements Serializable {

        @Schema(description = "模板编码")
        @NotBlank
        private String templateCode;

        @Schema(description = "测试变量")
        private Map<String, Object> testVariables;

        @Schema(description = "测试输入内容")
        private String testContent;
    }
}