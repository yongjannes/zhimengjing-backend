package com.sf.zhimengjing.common.model.dto.ai;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Title: AIModelDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: AI模型相关的数据传输对象集合
 */
@Data
@Schema(description="AI模型配置DTO")
public class AIModelDTO implements Serializable {

    @Schema(description = "模型记录ID（主键ID）")
    private Long id;

    @Schema(description = "模型编码")
    private String modelCode;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "提供商")
    private String provider;

    @Schema(description = "API端点地址")
    private String apiEndpoint;

    @Schema(description = "模型类型")
    private String modelType;

    @Schema(description = "最大令牌数")
    private Integer maxTokens;

    @Schema(description = "每千令牌成本")
    private BigDecimal costPer1kTokens;

    @Schema(description = "温度参数")
    private BigDecimal temperature;

    @Schema(description = "是否可用")
    private Boolean isAvailable;

    @Schema(description = "是否为默认模型")
    private Boolean isDefault;

    @Schema(description = "模型描述")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * AI模型创建/更新请求 DTO
     */
    @Data
    @Schema(description = "AI模型请求DTO")
    public static class ModelRequestDTO implements Serializable {

        @Schema(description = "模型编码")
        @NotBlank
        private String modelCode;

        @Schema(description = "模型名称")
        @NotBlank
        private String modelName;

        @Schema(description = "提供商")
        @NotBlank
        private String provider;

        @Schema(description = "API端点地址")
        private String apiEndpoint;

        @Schema(description = "最大令牌数")
        private Integer maxTokens;

        @Schema(description = "每千令牌成本")
        private BigDecimal costPer1kTokens;

        @Schema(description = "温度参数")
        private BigDecimal temperature;

        @Schema(description = "模型描述")
        private String description;

        @Schema(description = "API密钥", required = true)
        @NotBlank(message = "API密钥不能为空")
        private String apiKey;
    }

    /**
     * AI模型统计 VO
     */
    @Data
    @Schema(description = "AI模型统计VO")
    public static class ModelStatsVO implements Serializable {

        @Schema(description = "模型编码")
        private String modelCode;

        @Schema(description = "模型名称")
        private String modelName;

        @Schema(description = "总调用次数")
        private Long totalCalls;

        @Schema(description = "成功调用次数")
        private Long successCalls;

        @Schema(description = "平均响应时间")
        private Integer avgResponseTime;

        @Schema(description = "总成本")
        private BigDecimal totalCost;

        @Schema(description = "成功率")
        private Double successRate;
    }
}