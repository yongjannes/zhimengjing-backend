package com.sf.zhimengjing.service;

import com.sf.zhimengjing.common.enumerate.FileTypeEnum;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @Title: OssService
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service
 * @description: 对象存储服务接口，定义了文件上传、下载、删除等标准操作。
 */
public interface OssService {

    /**
     * 上传文件
     *
     * @param file     MultipartFile 文件对象
     * @param folder   存储的文件夹
     * @param fileType 期望的文件类型，用于校验
     * @return 文件的唯一访问Key (ObjectKey)
     */
    String uploadFile(MultipartFile file, String folder, FileTypeEnum fileType);

    /**
     * 上传文件流
     *
     * @param inputStream 文件输入流
     * @param fileName    文件名
     * @param folder      存储的文件夹
     * @return 文件的唯一访问Key (ObjectKey)
     */
    String uploadFile(InputStream inputStream, String fileName, String folder);

    /**
     * 删除文件
     *
     * @param fileUrl 文件的URL或ObjectKey
     * @return 删除结果
     */
    String deleteFile(String fileUrl);

    /**
     * 获取文件访问URL
     *
     * @param fileKey 文件的唯一访问Key
     * @return 文件的完整可访问URL
     */
    String getFileUrl(String fileKey);

    /**
     * 检查文件是否存在
     *
     * @param fileKey 文件的唯一访问Key
     * @return 检查结果
     */
    String fileExists(String fileKey);

    /**
     * 下载文件到本地
     *
     * @param fileKey   文件的唯一访问Key
     * @param localPath 本地存储路径
     * @return 下载结果
     */
    String downloadFile(String fileKey, String localPath);
}