package com.sf.zhimengjing.common.model.dto;

import com.sf.zhimengjing.common.model.BasePageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: SystemSettingDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 系统配置数据传输对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统配置DTO")
public class SystemSettingDTO extends BasePageDTO {

    @Schema(description = "配置键名")
    @NotBlank(message = "配置键名不能为空")
    private String settingKey;

    @Schema(description = "配置值")
    private String settingValue;

    @Schema(description = "配置类型")
    private String settingType;

    @Schema(description = "配置分类")
    private String category;

    @Schema(description = "配置描述")
    private String description;

    @Schema(description = "是否加密")
    private Boolean isEncrypted;

    // 查询条件
    @Schema(description = "关键词搜索")
    private String keyword;
}