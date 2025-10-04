
package com.sf.zhimengjing.common.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Title: FileUploadDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: 文件上传请求对象
 */
@Data
@Schema(description = "文件上传请求对象")
public class FileUploadDTO {

    @Schema(description = "文件URL或文件Key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件URL不能为空")
    private String fileUrl;

    @Schema(description = "文件类型（image/document）")
    private String fileType;
}
