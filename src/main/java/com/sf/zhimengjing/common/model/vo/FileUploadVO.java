package com.sf.zhimengjing.common.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sf.zhimengjing.common.enumerate.FileTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * @Title: FileUploadVO
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.model.vo
 * @description: 文件上传结果对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件上传结果视图对象")
public class FileUploadVO {

    @Schema(description = "文件在OSS中的唯一标识路径 (用于后续操作)", example = "images/20251004/1d1bd9bc61764e4d87cdc944691c008d.jpg")
    private String fileKey;

    @Schema(description = "文件的公网访问URL (仅当配置了CDN时返回)", example = "https://cdn.yourdomain.com/images/20251004/1d1bd9bc61764e4d87cdc944691c008d.jpg")
    private String url;

    @Schema(description = "文件类型")
    private FileTypeEnum fileType;

    @Schema(description = "文件大小的详细信息")
    private FileSizeVO fileSize;

    @Schema(description = "上传时间", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadTime;

    /**
     * 内部类：用于展示文件大小
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "文件大小信息")
    public static class FileSizeVO {

        @Schema(description = "文件大小（字节）", example = "3023830")
        private long bytes;

        @Schema(description = "格式化后的可读大小", example = "2.88 MB")
        private String formatted;

        public static FileSizeVO fromBytes(long bytes) {
            if (bytes < 0) {
                bytes = 0;
            }
            return new FileSizeVO(bytes, formatSize(bytes));
        }

        private static String formatSize(long bytes) {
            if (bytes < 1024) {
                return bytes + " B";
            }
            int exp = (int) (Math.log(bytes) / Math.log(1024));
            String unit = "KMGTPE".charAt(exp - 1) + "B";
            double size = bytes / Math.pow(1024, exp);
            BigDecimal bd = new BigDecimal(size);
            return bd.setScale(2, RoundingMode.HALF_UP).toString() + " " + unit;
        }
    }
}