package com.sf.zhimengjing.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Title: AliyunOssServiceImpl
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.impl
 * @description: 阿里云 OSS 服务实现类，提供文件上传、下载、删除、校验等功能。
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "oss", name = "provider", havingValue = "aliyun")
public class AliyunOssServiceImpl implements OssService {

    private final OSS ossClient;
    private final OssProperties ossProperties;
    private final Tika tika = new Tika();

    @Override
    public String uploadFile(MultipartFile file, String folder, FileTypeEnum fileType) {
        try {
            // 1. 文件基本校验 (现在会传入明确的文件类型)
            validateFile(file, fileType);

            // 2. 生成文件名（UUID + 原文件扩展名）
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName != null && originalFileName.contains(".")
                    ? originalFileName.substring(originalFileName.lastIndexOf("."))
                    : "";
            String fileName = IdUtil.simpleUUID() + fileExtension;

            // 3. 生成完整对象Key（路径）
            String objectKey = buildObjectKey(folder, fileName);

            // 4. 构建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossProperties.getAliyun().getBucketName(),
                    objectKey,
                    file.getInputStream()
            );

            // 5. 执行上传
            ossClient.putObject(putObjectRequest);

            log.info("文件上传成功 - Bucket: {}, ObjectKey: {}",
                    ossProperties.getAliyun().getBucketName(), objectKey);

            // 6. 返回 objectKey
            return objectKey;

        } catch (Exception e) {
            log.error("阿里云OSS文件上传失败 - 文件名: {}", file.getOriginalFilename(), e);
            throw new GeneralBusinessException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName, String folder) {
        try {
            // 1. 生成文件名
            String fileExtension = fileName.contains(".")
                    ? fileName.substring(fileName.lastIndexOf("."))
                    : "";
            String newFileName = IdUtil.simpleUUID() + fileExtension;

            // 2. 生成完整对象Key
            String objectKey = buildObjectKey(folder, newFileName);

            // 3. 执行上传
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossProperties.getAliyun().getBucketName(),
                    objectKey,
                    inputStream
            );
            ossClient.putObject(putObjectRequest);

            log.info("文件流上传成功 - ObjectKey: {}", objectKey);

            // 4. 返回objectKey
            return objectKey;

        } catch (Exception e) {
            log.error("阿里云OSS文件流上传失败 - 文件名: {}", fileName, e);
            throw new GeneralBusinessException("文件流上传失败：" + e.getMessage());
        }
    }


    @Override
    public String deleteFile(String fileUrl) {
        try {
            // 从URL中提取objectKey
            String objectKey = extractObjectKey(fileUrl);

            if (StrUtil.isBlank(objectKey)) {
                log.warn("无效的文件URL: {}", fileUrl);
                return String.format("文件删除失败：无效的文件URL '%s'", fileUrl);
            }

            ossClient.deleteObject(
                    ossProperties.getAliyun().getBucketName(),
                    objectKey
            );

            log.info("文件删除成功 - Bucket: {}, ObjectKey: {}",
                    ossProperties.getAliyun().getBucketName(), objectKey);

            return String.format("文件删除成功：%s", objectKey);

        } catch (Exception e) {
            log.error("阿里云OSS文件删除失败 - fileUrl: {}", fileUrl, e);
            return String.format("文件删除失败：%s", e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String fileKey) {
        try {
            // 生成带签名的URL，有效期由配置文件指定
            Date expiration = new Date(System.currentTimeMillis()
                    + ossProperties.getUrlExpiration() * 1000);

            URL url = ossClient.generatePresignedUrl(
                    ossProperties.getAliyun().getBucketName(),
                    fileKey,
                    expiration
            );

            return url.toString();

        } catch (Exception e) {
            log.error("生成文件访问URL失败 - fileKey: {}", fileKey, e);
            throw new GeneralBusinessException("生成文件访问URL失败");
        }
    }

    @Override
    public String fileExists(String fileKey) {
        try {
            boolean exists = ossClient.doesObjectExist(
                    ossProperties.getAliyun().getBucketName(),
                    fileKey
            );

            if (exists) {
                log.info("文件存在检查 - fileKey: {}, 结果: 存在", fileKey);
                return String.format("文件存在：%s", fileKey);
            } else {
                log.info("文件存在检查 - fileKey: {}, 结果: 不存在", fileKey);
                return String.format("文件不存在：%s", fileKey);
            }
        } catch (Exception e) {
            log.error("检查文件是否存在失败 - fileKey: {}", fileKey, e);
            return String.format("检查文件失败：%s", e.getMessage());
        }
    }

    @Override
    public String downloadFile(String fileKey, String localPath) {
        try {
            OSSObject ossObject = ossClient.getObject(
                    ossProperties.getAliyun().getBucketName(),
                    fileKey
            );

            // 创建本地文件
            File localFile = new File(localPath);
            FileUtil.writeFromStream(ossObject.getObjectContent(), localFile);

            log.info("文件下载成功 - fileKey: {}, localPath: {}", fileKey, localPath);
            return String.format("文件下载成功：%s -> %s", fileKey, localPath);

        } catch (Exception e) {
            log.error("文件下载失败 - fileKey: {}, localPath: {}", fileKey, localPath, e);
            return String.format("文件下载失败：%s", e.getMessage());
        }
    }

    /**
     * 文件校验（通用）
     */
    private void validateFile(MultipartFile file, FileTypeEnum fileType) {

        // 1. 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new GeneralBusinessException("上传的文件为空");
        }

        // 2. 检查文件大小
        long maxSize = ossProperties.getUpload().getMaxSize();
        if (file.getSize() > maxSize) {
            throw new GeneralBusinessException(
                    String.format("文件大小超过限制：最大支持 %dMB", maxSize / (1024 * 1024))
            );
        }

        // 3. 根据传入的 fileType 获取对应的允许后缀列表
        List<String> allowedExtensions;

        // 如果是通用上传接口 (fileType 为 OTHER) 或因某些原因类型为 null，则使用总的允许列表
        if (fileType == null || fileType == FileTypeEnum.OTHER) {
            allowedExtensions = Arrays.asList(ossProperties.getAllAllowedFileTypes());
        } else {
            // 对于指定的接口类型，使用其专属的允许列表
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
                    // 理论上不会走到这里，但作为保障，默认使用总列表
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

        // 4. MIME类型校验（可选，根据业务需要启用）
        if (Boolean.TRUE.equals(ossProperties.getUpload().getCheckMimeType())) {
            try {
                String mimeType = tika.detect(file.getInputStream());
                log.debug("检测到文件MIME类型: {}", mimeType);
                // 可以根据MIME类型做进一步校验
            } catch (Exception e) {
                log.warn("MIME类型检测失败", e);
            }
        }
    }

    /**
     * 构建对象Key（完整路径）
     *
     * @param folder 文件夹
     * @param fileName 文件名
     * @return 完整对象Key
     */
    private String buildObjectKey(String folder, String fileName) {
        // 从配置文件获取基础路径
        String basePath = ossProperties.getAliyun().getBasePath();
        // 使用更紧凑的日期格式
        String datePath = DateUtil.format(new Date(), "yyyyMMdd");

        // 确保 basePath 以斜杠结尾（如果存在）
        if (StringUtils.hasText(basePath) && !basePath.endsWith("/")) {
            basePath += "/";
        }

        return String.format("%s%s/%s/%s",
                (basePath != null ? basePath : ""), // basePath (来自配置，可为空)
                datePath,    // 日期 (如: 20251005)
                folder,      // 文件夹 (如: image)
                fileName     // 文件名 (如: uuid.jpg)
        );
    }

    /**
     * 构建完整文件访问URL
     *
     * @param objectKey 对象Key
     * @return 完整URL
     */
    private String buildFileUrl(String objectKey) {
        String endpoint = ossProperties.getAliyun().getEndpoint();
        String bucketName = ossProperties.getAliyun().getBucketName();

        // 格式：https://bucket-name.endpoint/objectKey
        return String.format("https://%s.%s/%s", bucketName, endpoint, objectKey);
    }

    /**
     * 从文件URL中提取ObjectKey
     *
     * @param fileUrl 文件URL
     * @return ObjectKey
     */
    private String extractObjectKey(String fileUrl) {
        if (StrUtil.isBlank(fileUrl)) {
            return null;
        }

        // 如果是完整URL，提取ObjectKey
        if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
            try {
                URL url = new URL(fileUrl);
                String path = url.getPath();
                // 移除开头的"/"
                return path.startsWith("/") ? path.substring(1) : path;
            } catch (Exception e) {
                log.error("解析文件URL失败: {}", fileUrl, e);
                return null;
            }
        }

        // 如果已经是ObjectKey，直接返回
        return fileUrl;
    }
}