package com.sf.zhimengjing.common.model.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "梦境内容分析DTO")
public class DreamContentAnalyticsDTO {
    @Schema(description = "梦境ID")
    private Long dreamId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "情感分数")
    private BigDecimal emotionScore;

    @Schema(description = "情感标签")
    private String emotionLabel;

    @Schema(description = "关键词列表")
    private List<String> keywords;

    @Schema(description = "分类列表")
    private List<String> categories;

    @Schema(description = "创建日期")
    private LocalDate createdDate;
}