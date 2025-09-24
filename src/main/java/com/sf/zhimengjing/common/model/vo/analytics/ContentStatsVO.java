package com.sf.zhimengjing.common.model.vo.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * @Title: ContentStatsVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo.analytics
 * @Description: 内容统计 VO，用于展示文本内容的基础统计信息，
 *               包含内容长度、词数、句子数、段落数及平均词长。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内容统计VO")
public class ContentStatsVO {
    @Schema(description = "内容长度")
    private Integer contentLength;

    @Schema(description = "词数")
    private Integer wordCount;

    @Schema(description = "句子数")
    private Integer sentenceCount;

    @Schema(description = "段落数")
    private Integer paragraphCount;

    @Schema(description = "平均词长")
    private BigDecimal avgWordLength;
}