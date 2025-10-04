package com.sf.zhimengjing.service.advanced;

import com.sf.zhimengjing.common.enumerate.FileTypeEnum;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Title: MultipartUploadService
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.advanced
 * @description: 大文件分片上传服务接口，提供分片上传与任务取消等功能。
 */
public interface MultipartUploadService {

    /**
     * 执行分片上传
     *
     * @param file 文件输入流
     * @param folder OSS对象key
     * @param fileType 文件类型
     * @return 文件访问key
     * @throws Exception 上传过程中可能抛出的异常
     */
    String multipartUpload(MultipartFile file, String folder, FileTypeEnum fileType) throws Exception;

    /**
     * 取消分片上传任务
     *
     * @param objectKey OSS对象key
     * @param uploadId 分片上传任务的ID
     */
    void abortMultipartUpload(String objectKey, String uploadId);
}
