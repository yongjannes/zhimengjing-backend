package com.sf.zhimengjing.common.model.dto.analytics;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.Map;

/**
 * @Title: PredictionRequestDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto.analytics
 * @Description: 模型预测请求 DTO
 */
@Data
@Schema(description = "模型预测请求参数")
public class PredictionRequestDTO {

    @NotEmpty(message = "输入数据不能为空")
    @Schema(description = "模型预测所需的输入数据，键值对形式",
            example = "{\"content\": \"这个梦很奇怪，我梦到了会飞的鱼。\"}")
    private Map<String, Object> inputData;
}