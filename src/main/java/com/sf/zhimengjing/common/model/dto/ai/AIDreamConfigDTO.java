package com.sf.zhimengjing.common.model.dto.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * @Title: AIDreamConfigDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: AI梦境解析配置数据传输对象，用于前后端交互
 */
@Data
@Schema(description = "AI梦境解析配置DTO")
public class AIDreamConfigDTO implements Serializable {

    @Schema(description = "配置记录ID（主键ID）")
    private Long id;

    @Schema(description = "关联的AI模型编码")
    private String modelCode;

    @Schema(description = "解析模式")
    private String analysisMode;

    @Schema(description = "解析深度")
    private String analysisDepth;

    @Schema(description = "语言风格")
    private String languageStyle;

    @Schema(description = "解析长度")
    private String analysisLength;

    @Schema(description = "是否启用情感分析")
    private Boolean enableEmotionAnalysis;

    @Schema(description = "是否启用标签生成")
    private Boolean enableTagGeneration;

    @Schema(description = "是否启用建议生成")
    private Boolean enableSuggestion;

    @Schema(description = "自定义提示词")
    private String customPrompt;

    @Schema(description = "是否激活")
    private Boolean isActive;

    /**
     * 梦境解析配置更新请求 DTO
     */
    @Data
    @Schema(description = "梦境解析配置请求DTO")
    public static class DreamConfigRequestDTO implements Serializable {

        @Schema(description = "关联的AI模型编码")
        @NotBlank
        private String modelCode;

        @Schema(description = "解析模式")
        private String analysisMode;

        @Schema(description = "解析深度")
        private String analysisDepth;

        @Schema(description = "语言风格")
        private String languageStyle;

        @Schema(description = "解析长度")
        private String analysisLength;

        @Schema(description = "是否启用情感分析")
        private Boolean enableEmotionAnalysis;

        @Schema(description = "是否启用标签生成")
        private Boolean enableTagGeneration;

        @Schema(description = "是否启用建议生成")
        private Boolean enableSuggestion;

        @Schema(description = "自定义提示词")
        private String customPrompt;
    }
}