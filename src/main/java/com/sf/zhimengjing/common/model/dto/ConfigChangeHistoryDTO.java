package com.sf.zhimengjing.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sf.zhimengjing.common.model.BasePageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * @Title: ConfigChangeHistoryDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 配置变更历史数据传输对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "配置变更历史DTO")
public class ConfigChangeHistoryDTO extends BasePageDTO {

    @Schema(description = "配置键名")
    private String configKey;

    @Schema(description = "变更类型")
    private String changeType;

    @Schema(description = "变更人")
    private Long changedBy;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "关键词搜索")
    private String keyword;
}