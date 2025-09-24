package com.sf.zhimengjing.common.model.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Title: DreamContentRequestDTO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.dto.analytics
 * @description: 梦境内容请求DTO
 */
@Data
@Schema(description = "梦境内容请求DTO")
public class DreamContentRequestDTO {


    @Schema(description = "梦境内容文本", required = true, example = "我梦见自己在海边散步")
    @NotBlank(message = "梦境内容不能为空")
    private String content;
}