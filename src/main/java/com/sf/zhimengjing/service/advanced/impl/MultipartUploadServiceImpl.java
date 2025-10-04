package com.sf.zhimengjing.service.advanced.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import com.sf.zhimengjing.common.config.properties.OssProperties;
import com.sf.zhimengjing.common.enumerate.FileTypeEnum;
import com.sf.zhimengjing.service.advanced.MultipartUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Title: MultipartUploadServiceImpl
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.advanced.impl
 * @description: 阿里云 OSS 大文件分片上传服务实现类，支持分片上传、异常中断恢复及自动清理临时文件功能。
 *               通过分片方式上传大文件，提高上传效率与稳定性。
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "oss.provider", havingValue = "aliyun")
public class MultipartUploadServiceImpl implements MultipartUploadService {

    private final OSS ossClient;
    private final OssProperties ossProperties;

    @Override
    public String multipartUpload(MultipartFile file, String folder, FileTypeEnum fileType) throws Exception {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = (originalFileName != null && originalFileName.contains("."))
                ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : "";
        String fileName = IdUtil.simpleUUID() + fileExtension;
        String objectKey = buildObjectKey(folder, fileName);


        String bucketName = ossProperties.getAliyun().getBucketName();
        long fileSize = file.getSize();


        // 1. 初始化分片上传
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectKey);
        InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
        String uploadId = upresult.getUploadId();
        log.info("分片上传初始化完成，Bucket: {}, ObjectKey: {}, UploadId: {}", bucketName, objectKey, uploadId);

        // 2. 计算文件分片数量
        final long partSize = 5 * 1024 * 1024L; // 每片5MB
        int partCount = (int) (fileSize / partSize);
        if (fileSize % partSize != 0) {
            partCount++;
        }
        log.info("文件将被分为 {} 片上传", partCount);

        List<PartETag> partETags = new ArrayList<>();
        File tempFile = null;
        try {
            // 3. 【核心修复】创建一个临时文件，将上传的文件内容写入其中
            tempFile = Files.createTempFile("upload-part-", ".tmp").toFile();
            file.transferTo(tempFile);

            // 4. 遍历所有分片
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileSize - startPos) : partSize;

                // 【核心修复】每次循环都从临时文件创建一个新的输入流
                try (InputStream fis = new FileInputStream(tempFile)) {
                    fis.skip(startPos); // 跳到当前分片的起始位置

                    UploadPartRequest uploadPartRequest = new UploadPartRequest();
                    uploadPartRequest.setBucketName(bucketName);
                    uploadPartRequest.setKey(objectKey);
                    uploadPartRequest.setUploadId(uploadId);
                    uploadPartRequest.setInputStream(fis);
                    uploadPartRequest.setPartSize(curPartSize);
                    uploadPartRequest.setPartNumber(i + 1);

                    UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
                    partETags.add(uploadPartResult.getPartETag());
                }
                log.info("上传分片 {}/{} 完成", i + 1, partCount);
            }

            // 5. 完成分片上传
            CompleteMultipartUploadRequest completeReq = new CompleteMultipartUploadRequest(bucketName, objectKey, uploadId, partETags);
            ossClient.completeMultipartUpload(completeReq);
            log.info("分片上传完成: {}", objectKey);
        } catch (Exception e) {
            log.error("分片上传过程中发生错误，即将取消上传。UploadId: {}", uploadId, e);
            abortMultipartUpload(objectKey, uploadId);
            throw e;
        } finally {
            // 6. 【核心修复】无论成功或失败，最后都删除临时文件
            if (tempFile != null && tempFile.exists()) {
                if (tempFile.delete()) {
                    log.info("临时文件 {} 已成功删除", tempFile.getName());
                } else {
                    log.warn("临时文件 {} 删除失败", tempFile.getName());
                }
            }
        }

        return objectKey;
    }

    @Override
    public void abortMultipartUpload(String objectKey, String uploadId) {
        String bucketName = ossProperties.getAliyun().getBucketName();
        AbortMultipartUploadRequest abortRequest = new AbortMultipartUploadRequest(bucketName, objectKey, uploadId);
        ossClient.abortMultipartUpload(abortRequest);
        log.warn("已取消分片上传: {}, uploadId: {}", objectKey, uploadId);
    }

    private String buildObjectKey(String folder, String fileName) {
        String basePath = ossProperties.getAliyun().getBasePath();
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
}