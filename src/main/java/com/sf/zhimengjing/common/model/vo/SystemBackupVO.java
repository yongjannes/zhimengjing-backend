package com.sf.zhimengjing.common.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @Title: SystemBackupVO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.vo
 * @Description: 系统备份视图对象
 */
@Data
@Schema(description = "系统备份VO")
public class SystemBackupVO {

    @Schema(description = "备份ID")
    private Long id;

    @Schema(description = "备份名称")
    private String backupName;

    @Schema(description = "备份类型")
    private String backupType;

    @Schema(description = "备份大小(字节)")
    private Long backupSize;

    @Schema(description = "备份大小(格式化)")
    private String backupSizeFormatted;

    @Schema(description = "备份状态")
    private String backupStatus;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "创建人")
    private Long createdBy;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}