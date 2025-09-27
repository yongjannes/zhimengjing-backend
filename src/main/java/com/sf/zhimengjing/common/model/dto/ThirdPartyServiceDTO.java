package com.sf.zhimengjing.common.model.dto;

import com.sf.zhimengjing.common.model.BasePageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @Title: ThirdPartyServiceDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 第三方服务配置数据传输对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "第三方服务配置DTO")
public class ThirdPartyServiceDTO extends BasePageDTO {

    @Schema(description = "服务名称")
    private String serviceName;

    @Schema(description = "服务类型")
    private String serviceType;

    @Schema(description = "配置数据")
    private Map<String, Object> configData;

    @Schema(description = "是否激活")
    private Boolean isActive;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "关键词搜索")
    private String keyword;
}