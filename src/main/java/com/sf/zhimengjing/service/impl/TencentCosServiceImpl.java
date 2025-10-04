package com.sf.zhimengjing.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.sf.zhimengjing.common.config.properties.OssProperties;
import com.sf.zhimengjing.common.enumerate.FileTypeEnum;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.service.OssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Title: TencentCosServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.impl
 * @description: 腾讯云 COS 服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "oss", name = "provider", havingValue = "tencent")
public class TencentCosServiceImpl implements OssService {

    private final COSClient cosClient;
    private final OssProperties ossProperties;
    private final Tika tika = new Tika();

    @Override
    public String uploadFile(MultipartFile file, String folder, FileTypeEnum fileType) {
        try {
            // 1. 文件基本校验
            validateFile(file, fileType);

            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName != null && originalFileName.contains(".")
                    ? originalFileName.substring(originalFileName.lastIndexOf("."))
                    : "";
            String fileName = IdUtil.simpleUUID() + fileExtension;
            String objectKey = buildObjectKey(folder, fileName);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossProperties.getTencent().getBucketName(),
                    objectKey,
                    file.getInputStream(),
                    metadata
            );

            cosClient.putObject(putObjectRequest);

            log.info("腾讯云COS文件上传成功 - Bucket: {}, ObjectKey: {}",
                    ossProperties.getTencent().getBucketName(), objectKey);

            return objectKey;

        } catch (Exception e) {
            log.error("腾讯云COS文件上传失败 - 文件名: {}", file.getOriginalFilename(), e);
            throw new GeneralBusinessException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName, String folder) {
        try {
            String fileExtension = fileName.contains(".")
                    ? fileName.substring(fileName.lastIndexOf("."))
                    : "";
            String newFileName = IdUtil.simpleUUID() + fileExtension;
            String objectKey = buildObjectKey(folder, newFileName);

            ObjectMetadata metadata = new ObjectMetadata();
            // 对于流式上传，如果无法预知大小，可以不设置 ContentLength

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossProperties.getTencent().getBucketName(),
                    objectKey,
                    inputStream,
                    metadata
            );
            cosClient.putObject(putObjectRequest);

            log.info("腾讯云COS文件流上传成功 - ObjectKey: {}", objectKey);
            return objectKey;

        } catch (Exception e) {
            log.error("腾讯云COS文件流上传失败 - 文件名: {}", fileName, e);
            throw new GeneralBusinessException("文件流上传失败：" + e.getMessage());
        }
    }

    @Override
    public String deleteFile(String fileUrl) {
        try {
            String objectKey = extractObjectKey(fileUrl);
            if (StrUtil.isBlank(objectKey)) {
                log.warn("无效的文件URL: {}", fileUrl);
                return String.format("文件删除失败：无效的文件URL '%s'", fileUrl);
            }
            cosClient.deleteObject(ossProperties.getTencent().getBucketName(), objectKey);
            log.info("腾讯云COS文件删除成功 - Bucket: {}, ObjectKey: {}",
                    ossProperties.getTencent().getBucketName(), objectKey);
            return String.format("文件删除成功：%s", objectKey);
        } catch (Exception e) {
            log.error("腾讯云COS文件删除失败 - fileUrl: {}", fileUrl, e);
            return String.format("文件删除失败：%s", e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String fileKey) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + ossProperties.getUrlExpiration() * 1000);
            URL url = cosClient.generatePresignedUrl(ossProperties.getTencent().getBucketName(), fileKey, expiration);
            return url.toString();
        } catch (Exception e) {
            log.error("生成腾讯云COS文件访问URL失败 - fileKey: {}", fileKey, e);
            throw new GeneralBusinessException("生成文件访问URL失败");
        }
    }

    @Override
    public String fileExists(String fileKey) {
        try {
            boolean exists = cosClient.doesObjectExist(ossProperties.getTencent().getBucketName(), fileKey);
            if (exists) {
                log.info("腾讯云COS文件存在检查 - fileKey: {}, 结果: 存在", fileKey);
                return String.format("文件存在：%s", fileKey);
            } else {
                log.info("腾讯云COS文件存在检查 - fileKey: {}, 结果: 不存在", fileKey);
                return String.format("文件不存在：%s", fileKey);
            }
        } catch (Exception e) {
            log.error("检查腾讯云COS文件是否存在失败 - fileKey: {}", fileKey, e);
            return String.format("检查文件失败：%s", e.getMessage());
        }
    }

    @Override
    public String downloadFile(String fileKey, String localPath) {
        // 腾讯云COS的下载实现，这里暂时为空，您可以根据SDK进行实现
        log.warn("腾讯云COS的下载功能尚未实现");
        return "下载功能尚未实现";
    }

    /**
     * 文件校验方法，与 AliyunOssServiceImpl 完全一致
     */
    private void validateFile(MultipartFile file, FileTypeEnum fileType) {
        if (file == null || file.isEmpty()) {
            throw new GeneralBusinessException("上传的文件为空");
        }

        long maxSize = ossProperties.getUpload().getMaxSize();
        if (file.getSize() > maxSize) {
            throw new GeneralBusinessException(
                    String.format("文件大小超过限制：最大支持 %dMB", maxSize / (1024 * 1024))
            );
        }

        List<String> allowedExtensions;
        if (fileType == null || fileType == FileTypeEnum.OTHER) {
            allowedExtensions = Arrays.asList(ossProperties.getAllAllowedFileTypes());
        } else {
            switch (fileType) {
                case IMAGE:
                    allowedExtensions = ossProperties.getUpload().getAllowedImageTypes();
                    break;
                case DOCUMENT:
                    allowedExtensions = ossProperties.getUpload().getAllowedDocumentTypes();
                    break;
                case VIDEO:
                    allowedExtensions = ossProperties.getUpload().getAllowedVideoTypes();
                    break;
                case AUDIO:
                    allowedExtensions = ossProperties.getUpload().getAllowedAudioTypes();
                    break;
                case ARCHIVE:
                    allowedExtensions = ossProperties.getUpload().getAllowedArchiveTypes();
                    break;
                default:
                    allowedExtensions = Arrays.asList(ossProperties.getAllAllowedFileTypes());
                    break;
            }
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase()
                : "";

        if (allowedExtensions.isEmpty() || !allowedExtensions.contains(fileExtension)) {
            throw new GeneralBusinessException(
                    String.format("不支持的文件类型：%s。当前接口仅支持：%s", fileExtension, String.join(", ", allowedExtensions))
            );
        }

        if (Boolean.TRUE.equals(ossProperties.getUpload().getCheckMimeType())) {
            try {
                String mimeType = tika.detect(file.getInputStream());
                log.debug("检测到文件MIME类型: {}", mimeType);
            } catch (Exception e) {
                log.warn("MIME类型检测失败", e);
            }
        }
    }

    private String buildObjectKey(String folder, String fileName) {
        String basePath = ossProperties.getTencent().getBasePath();
        String datePath = DateUtil.format(new Date(), "yyyyMMdd");

        if (StringUtils.hasText(basePath) && !basePath.endsWith("/")) {
            basePath += "/";
        }

        return String.format("%s%s/%s/%s",
                (basePath != null ? basePath : ""),
                datePath,
                folder,
                fileName
        );
    }


    private String extractObjectKey(String fileUrl) {
        if (StrUtil.isBlank(fileUrl)) {
            return null;
        }

        if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
            try {
                URL url = new URL(fileUrl);
                String path = url.getPath();
                return path.startsWith("/") ? path.substring(1) : path;
            } catch (Exception e) {
                log.error("解析腾讯云COS文件URL失败: {}", fileUrl, e);
                return null;
            }
        }

        return fileUrl;
    }
}