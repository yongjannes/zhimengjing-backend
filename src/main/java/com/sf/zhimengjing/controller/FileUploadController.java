package com.sf.zhimengjing.controller;

import cn.hutool.core.io.FileUtil;
import com.sf.zhimengjing.common.config.properties.OssProperties;
import com.sf.zhimengjing.common.enumerate.FileTypeEnum;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.FileUploadDTO;
import com.sf.zhimengjing.common.model.vo.FileUploadVO;
import com.sf.zhimengjing.service.OssService;
import com.sf.zhimengjing.service.advanced.MultipartUploadService;
import com.sf.zhimengjing.service.advanced.QuickUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @Title: FileUploadController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller
 * @Description: 文件上传下载管理控制器，提供通用文件、图片、音视频、压缩包等上传与删除操作。
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Tag(name = "文件管理模块", description = "文件上传下载管理相关接口")
public class FileUploadController {

    private final OssService ossService;
    private final OssProperties ossProperties;
    private final QuickUploadService quickUploadService;

    @Autowired(required = false)
    private MultipartUploadService multipartUploadService;


    // ======================= 上传接口 =======================

    @PostMapping("/upload/image")
    @Operation(summary = "1. 上传图片", description = "将图片文件上传至 OSS 指定文件夹（默认：images）")
    public FileUploadVO uploadImage(
            @Parameter(description = "图片文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "存储文件夹，默认 images") @RequestParam(value = "folder", required = false, defaultValue = "images") String folder) {

        log.info("接收到图片上传请求 - 文件名: {}, 大小: {} bytes, 文件夹: {}", file.getOriginalFilename(), file.getSize(), folder);
        return uploadAndBuildVO(file, folder, FileTypeEnum.IMAGE);
    }

    @PostMapping("/upload/document")
    @Operation(summary = "2. 上传文档", description = "将文档文件上传至 OSS 指定文件夹（默认：documents）")
    public FileUploadVO uploadDocument(
            @Parameter(description = "文档文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "存储文件夹，默认 documents") @RequestParam(value = "folder", required = false, defaultValue = "documents") String folder) {

        log.info("接收到文档上传请求 - 文件名: {}, 大小: {} bytes, 文件夹: {}", file.getOriginalFilename(), file.getSize(), folder);
        return uploadAndBuildVO(file, folder, FileTypeEnum.DOCUMENT);
    }

    @PostMapping("/upload/video")
    @Operation(summary = "3. 上传视频", description = "将视频文件上传至 OSS 指定文件夹（默认：videos）")
    public FileUploadVO uploadVideo(
            @Parameter(description = "视频文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "存储文件夹，默认 videos") @RequestParam(value = "folder", required = false, defaultValue = "videos") String folder) {

        log.info("接收到视频上传请求 - 文件名: {}, 大小: {} bytes, 文件夹: {}", file.getOriginalFilename(), file.getSize(), folder);
        return uploadAndBuildVO(file, folder, FileTypeEnum.VIDEO);
    }

    @PostMapping("/upload/audio")
    @Operation(summary = "4. 上传音频", description = "将音频文件上传至 OSS 指定文件夹（默认：audios）")
    public FileUploadVO uploadAudio(
            @Parameter(description = "音频文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "存储文件夹，默认 audios") @RequestParam(value = "folder", required = false, defaultValue = "audios") String folder) {

        log.info("接收到音频上传请求 - 文件名: {}, 大小: {} bytes, 文件夹: {}", file.getOriginalFilename(), file.getSize(), folder);
        return uploadAndBuildVO(file, folder, FileTypeEnum.AUDIO);
    }

    @PostMapping("/upload/archive")
    @Operation(summary = "5. 上传压缩包", description = "将压缩文件上传至 OSS 指定文件夹（默认：archives）")
    public FileUploadVO uploadArchive(
            @Parameter(description = "压缩文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "存储文件夹，默认 archives") @RequestParam(value = "folder", required = false, defaultValue = "archives") String folder) {

        log.info("接收到压缩包上传请求 - 文件名: {}, 大小: {} bytes, 文件夹: {}", file.getOriginalFilename(), file.getSize(), folder);
        return uploadAndBuildVO(file, folder, FileTypeEnum.ARCHIVE);
    }

    @PostMapping("/upload")
    @Operation(summary = "6. 通用/高级文件上传", description = "自动识别文件类型并上传，支持通过mode参数选择高级上传模式。")
    public FileUploadVO unifiedUpload(
            @Parameter(description = "上传文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "上传模式：quick(秒传), multipart(分片上传), 不传则为普通上传") @RequestParam(value = "mode", required = false) String mode) {

        FileTypeEnum fileType = determineFileType(Objects.requireNonNull(file.getOriginalFilename()));
        String folder = getFolderForFileType(fileType);
        String fileKey;

        if ("quick".equalsIgnoreCase(mode)) {
            log.info("接收到秒传请求 - 文件名: {}", file.getOriginalFilename());
            fileKey = quickUploadService.quickUpload(file, folder, fileType);
        } else if ("multipart".equalsIgnoreCase(mode)) {
            log.info("接收到分片上传请求 - 文件名: {}", file.getOriginalFilename());
            if (multipartUploadService == null) {
                throw new GeneralBusinessException("当前环境不支持分片上传功能。");
            }
            try {
                fileKey = multipartUploadService.multipartUpload(file, folder, fileType);
            } catch (Exception e) {
                log.error("分片上传失败", e);
                throw new GeneralBusinessException("分片上传失败: " + e.getMessage());
            }
        } else {
            log.info("接收到通用文件上传请求 - 文件名: {}", file.getOriginalFilename());
            fileKey = ossService.uploadFile(file, folder, fileType);
        }

        String publicUrl = buildPublicUrl(fileKey);
        return FileUploadVO.builder()
                .fileKey(fileKey)
                .url(publicUrl)
                .fileType(fileType)
                .fileSize(FileUploadVO.FileSizeVO.fromBytes(file.getSize()))
                .uploadTime(LocalDateTime.now())
                .build();
    }
    @PostMapping("/upload/batch")
    @Operation(summary = "7. 批量上传文件", description = "同时上传多个文件至 OSS 指定文件夹（默认：files）")
    public List<FileUploadVO> uploadBatch(
            @Parameter(description = "上传文件列表", required = true) @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "存储文件夹，默认 files") @RequestParam(value = "folder", required = false, defaultValue = "files") String folder) {

        log.info("接收到批量文件上传请求 - 文件数量: {}, 文件夹: {}", files.length, folder);
        List<FileUploadVO> results = new ArrayList<>();
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    FileTypeEnum fileType = determineFileType(Objects.requireNonNull(file.getOriginalFilename()));
                    results.add(uploadAndBuildVO(file, folder, fileType));
                }
            }
        }
        return results;
    }

    // ======================= 删除接口 =======================

    @DeleteMapping("/delete")
    @Operation(summary = "8. 删除文件", description = "根据文件URL删除 OSS 上对应文件")
    public String deleteFile(
            @Parameter(description = "文件信息 DTO", required = true) @Validated @RequestBody FileUploadDTO dto) {

        log.info("接收到文件删除请求 - fileUrl: {}", dto.getFileUrl());
        return ossService.deleteFile(dto.getFileUrl());
    }

    @DeleteMapping("/delete/batch")
    @Operation(summary = "9. 批量删除文件", description = "根据文件URL列表批量删除 OSS 上的文件")
    public Map<String, String> deleteFiles(
            @Parameter(description = "文件URL列表", required = true) @RequestBody List<String> fileUrls) {

        log.info("接收到批量文件删除请求 - 文件数量: {}", fileUrls.size());
        Map<String, String> results = new HashMap<>();
        for (String fileUrl : fileUrls) {
            String result = ossService.deleteFile(fileUrl);
            results.put(fileUrl, result);
        }
        return results;
    }

    // ======================= 查询接口 =======================

    @GetMapping("/url")
    @Operation(summary = "10. 获取文件访问URL", description = "根据文件Key生成可访问的完整URL")
    public String getFileUrl(
            @Parameter(description = "文件在OSS中的唯一路径", required = true) @RequestParam("fileKey") String fileKey) {

        log.info("接收到获取文件URL请求 - fileKey: {}", fileKey);
        return ossService.getFileUrl(fileKey);
    }

    @GetMapping("/exists")
    @Operation(summary = "11. 检查文件是否存在", description = "验证 OSS 上是否存在指定文件")
    public String fileExists(
            @Parameter(description = "文件在OSS中的唯一路径", required = true) @RequestParam("fileKey") String fileKey) {

        log.info("接收到检查文件存在性请求 - fileKey: {}", fileKey);
        return ossService.fileExists(fileKey);
    }


    // ============================ 辅助方法 ============================

    /**
     * 内部方法：封装上传和构建VO的通用逻辑
     */
    private FileUploadVO uploadAndBuildVO(MultipartFile file, String folder, FileTypeEnum fileType) {
        // 将 fileType 传递给服务层进行校验
        String fileKey = ossService.uploadFile(file, folder, fileType);

        String publicUrl = null;
        if (Boolean.TRUE.equals(ossProperties.getCdn().getEnabled()) && ossProperties.getCdn().getDomain() != null) {
            publicUrl = ossProperties.getCdn().getDomain() + "/" + fileKey;
        }

        return FileUploadVO.builder()
                .fileKey(fileKey)
                .url(publicUrl)
                .fileType(fileType)
                .fileSize(FileUploadVO.FileSizeVO.fromBytes(file.getSize()))
                .uploadTime(LocalDateTime.now())
                .build();
    }

    private String buildPublicUrl(String fileKey) {
        if (Boolean.TRUE.equals(ossProperties.getCdn().getEnabled()) && ossProperties.getCdn().getDomain() != null) {
            return ossProperties.getCdn().getDomain() + "/" + fileKey;
        }
        return null;
    }

    /**
     * 内部方法：根据文件名后缀判断文件类型
     */
    private FileTypeEnum determineFileType(String fileName) {
        String extension = FileUtil.getSuffix(fileName).toLowerCase();
        if (ossProperties.getUpload().getAllowedImageTypes().contains(extension)) {
            return FileTypeEnum.IMAGE;
        } else if (ossProperties.getUpload().getAllowedDocumentTypes().contains(extension)) {
            return FileTypeEnum.DOCUMENT;
        } else if (ossProperties.getUpload().getAllowedVideoTypes().contains(extension)) {
            return FileTypeEnum.VIDEO;
        } else if (ossProperties.getUpload().getAllowedAudioTypes().contains(extension)) {
            return FileTypeEnum.AUDIO;
        } else if (ossProperties.getUpload().getAllowedArchiveTypes().contains(extension)) {
            return FileTypeEnum.ARCHIVE;
        }
        // 对于通用上传接口，如果一个类型都不匹配，就抛出异常
        throw new GeneralBusinessException("不支持的文件类型：" + extension);
    }

    private String getFolderForFileType(FileTypeEnum fileType) {
        switch (fileType) {
            case IMAGE: return "images";
            case DOCUMENT: return "documents";
            case VIDEO: return "videos";
            case AUDIO: return "audios";
            case ARCHIVE: return "archives";
            default: return "others"; // 所有未知类型统一放到 others 文件夹
        }
    }
}