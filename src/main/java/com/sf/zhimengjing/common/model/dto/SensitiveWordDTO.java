package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.io.Serializable;

/**
 * @Title: SensitiveWordDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 敏感词数据传输对象，用于前后端交互
 */
@Data
@Schema(description = "敏感词DTO")
public class SensitiveWordDTO implements Serializable {

    @Schema(description = "敏感词记录ID（主键ID）")
    private Long id;

    @Schema(description = "敏感词内容（不能为空）")
    @NotBlank
    private String word;

    @Schema(description = "敏感词类型（如：政治、暴力、色情、广告等，不能为空）")
    @NotBlank
    private String wordType;

    @Schema(description = "敏感等级（数值越大表示越严重，用于分级处理）")
    private Integer severityLevel;

    @Schema(description = "替换内容（检测到敏感词时可替换成的文本，如'***'）")
    private String replacement;

    @Schema(description = "是否启用（true：启用，false：禁用）")
    private Boolean isEnabled;
}
