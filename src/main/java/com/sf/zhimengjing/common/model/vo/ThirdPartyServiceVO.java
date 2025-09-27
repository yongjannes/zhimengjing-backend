package com.sf.zhimengjing.common.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Title: ThirdPartyServiceVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 第三方服务配置视图对象
 */
@Data
@Schema(description = "第三方服务配置VO")
public class ThirdPartyServiceVO {

    @Schema(description = "服务ID")
    private Long id;

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

    @Schema(description = "最后测试时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastTestTime;

    @Schema(description = "测试结果")
    private String testResult;

    @Schema(description = "测试错误信息")
    private String testError;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}