package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Title: TagMergeDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @description: 标签合并 DTO
 */
@Data
@Schema(description = "标签合并DTO")
public class TagMergeDTO {

    /**
     * 源标签ID列表（要被合并的标签）
     */
    @NotEmpty(message = "源标签ID列表不能为空")
    @Schema(description = "源标签ID列表", example = "[1, 2, 3]")
    private List<Long> sourceTagIds;

    /**
     * 目标标签ID（合并到的标签）
     */
    @NotNull(message = "目标标签ID不能为空")
    @Schema(description = "目标标签ID", example = "4")
    private Long targetTagId;
}