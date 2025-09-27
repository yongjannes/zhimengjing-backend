package com.sf.zhimengjing.common.model.dto;

import com.sf.zhimengjing.common.model.BasePageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Title: SystemBackupDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 系统备份数据传输对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统备份DTO")
public class SystemBackupDTO extends BasePageDTO {

    @Schema(description = "备份名称")
    @NotBlank(message = "备份名称不能为空")
    private String backupName;

    @Schema(description = "备份类型")
    @NotBlank(message = "备份类型不能为空")
    private String backupType;

    @Schema(description = "备份状态")
    private String backupStatus;

    @Schema(description = "创建人")
    private Long createdBy;

    @Schema(description = "关键词搜索")
    private String keyword;
}