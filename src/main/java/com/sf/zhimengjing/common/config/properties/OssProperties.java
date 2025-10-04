package com.sf.zhimengjing.common.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Title: OssProperties
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.config.properties
 * @description: OSS 对象存储配置属性类
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    // 云存储服务商，可选 aliyun 或 tencent，默认 aliyun
    private String provider = "aliyun";

    // 文件访问URL过期时间（秒），默认7200秒
    private Long urlExpiration = 7200L;

    // 阿里云OSS配置
    private AliyunConfig aliyun;

    // 腾讯云COS配置
    private TencentConfig tencent;

    // 文件上传配置
    private UploadConfig upload;

    // CDN加速配置
    private CdnConfig cdn;

    // 自动删除配置
    private AutoDeleteConfig autoDelete;

    @Data
    public static class AliyunConfig {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
        private String basePath = "files/";
        private Integer connectTimeout = 5000;
        private Integer socketTimeout = 50000;
        private Integer maxConnections = 1024;
        private String imageStyle;
    }

    @Data
    public static class TencentConfig {
        private String secretId;
        private String secretKey;
        private String region;
        private String bucketName;
        private String basePath = "files/";
        private Integer connectionTimeout = 5000;
        private Integer socketTimeout = 50000;
        private Integer maxConnections = 1024;
        private String cdnDomain;
    }

    /**
     * 【已修正 - 核心修改区域】
     */
    @Data
    public static class UploadConfig {

        // 全局最大文件大小（字节），默认100MB
        private Long maxSize = 104857600L;

        // 允许的图片格式 (从yml读取的原始字符串)
        private String allowedImageTypes = "jpg,jpeg,png,gif,webp,bmp,svg,ico";
        // 允许的文档格式
        private String allowedDocumentTypes = "pdf,doc,docx,xls,xlsx,ppt,pptx,txt,md,csv";
        // 允许的视频格式
        private String allowedVideoTypes = "mp4,avi,mov,wmv,flv,mkv,webm,m4v";
        // 允许的音频格式
        private String allowedAudioTypes = "mp3,wav,flac,aac,ogg,wma,m4a";
        // 允许的压缩包格式
        private String allowedArchiveTypes = "zip,rar,7z,tar,gz,bz2";

        // 图片最大大小（字节），默认10MB
        private Long imageMaxSize = 10485760L;
        // 文档最大大小（字节），默认50MB
        private Long documentMaxSize = 52428800L;
        // 视频最大大小（字节），默认500MB
        private Long videoMaxSize = 524288000L;
        // 音频最大大小（字节），默认100MB
        private Long audioMaxSize = 104857600L;
        // 压缩包最大大小（字节），默认200MB
        private Long archiveMaxSize = 209715200L;

        // 是否检查文件MIME类型，默认false
        private Boolean checkMimeType = false;

        // =========== 【Getter 方法，将 String 转换为 List<String>】 ===========

        private List<String> splitTypes(String types) {
            if (!StringUtils.hasText(types)) {
                return Collections.emptyList();
            }
            return Arrays.asList(types.split(","));
        }

        public List<String> getAllowedImageTypes() {
            return splitTypes(this.allowedImageTypes);
        }

        public List<String> getAllowedDocumentTypes() {
            return splitTypes(this.allowedDocumentTypes);
        }

        public List<String> getAllowedVideoTypes() {
            return splitTypes(this.allowedVideoTypes);
        }

        public List<String> getAllowedAudioTypes() {
            return splitTypes(this.allowedAudioTypes);
        }

        public List<String> getAllowedArchiveTypes() {
            return splitTypes(this.allowedArchiveTypes);
        }
    }

    @Data
    public static class CdnConfig {
        private Boolean enabled = false;
        private String domain;
    }

    @Data
    public static class AutoDeleteConfig {
        private Boolean enabled = false;
        private Integer days = 30;
    }

    /**
     * 【已修正】便捷方法：获取所有允许的文件类型
     * @return String 数组
     */
    public String[] getAllAllowedFileTypes() {
        if (this.upload == null) {
            return new String[0];
        }
        List<String> allTypes = new ArrayList<>();
        allTypes.addAll(this.upload.getAllowedImageTypes());
        allTypes.addAll(this.upload.getAllowedDocumentTypes());
        allTypes.addAll(this.upload.getAllowedVideoTypes());
        allTypes.addAll(this.upload.getAllowedAudioTypes());
        allTypes.addAll(this.upload.getAllowedArchiveTypes());
        return allTypes.toArray(new String[0]);
    }
}