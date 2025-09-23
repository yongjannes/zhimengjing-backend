package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

/**
 * @Title: ReportHandleDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto.community
 * @description: 举报处理DTO，用于封装批量处理内容举报的请求数据
 */
@Data
@Schema(description = "举报处理DTO")
public class ReportHandleDTO {

    /**
     * 举报ID列表，必填，用于标识需要处理的举报
     */
    @NotEmpty(message = "举报ID列表不能为空")
    @Schema(description = "举报ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> reportIds;

    /**
     * 处理状态，必填
     * 1 - 已处理
     * 2 - 已驳回
     * 注意：Integer类型无法使用@NotEmpty生效，需要在业务逻辑中校验
     */
    @Schema(description = "处理状态：1-已处理，2-已驳回", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    /**
     * 处理结果描述，可选，用于记录处理情况或说明
     */
    @Schema(description = "处理结果描述")
    private String handleResult;
}