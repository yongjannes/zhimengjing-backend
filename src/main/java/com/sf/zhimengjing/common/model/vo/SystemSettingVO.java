package com.sf.zhimengjing.common.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @Title: SystemSettingVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 系统配置视图对象
 */
@Data
@Schema(description = "系统配置VO")
public class SystemSettingVO {

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "配置键名")
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

    @Schema(description = "是否系统配置")
    private Boolean isSystem;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}