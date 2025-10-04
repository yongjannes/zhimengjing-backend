package com.sf.zhimengjing.service.advanced;

import com.sf.zhimengjing.common.enumerate.FileTypeEnum;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Title: QuickUploadService
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.advanced
 * @description: 文件秒传服务接口，用于检测文件是否已存在以支持秒传功能。
 *               若文件存在则直接返回访问Key，否则执行常规上传流程。
 */
public interface QuickUploadService {

    /**
     * 尝试秒传，如果文件已存在则直接返回信息，否则执行普通上传流程。
     *
     * @param file     待上传的文件
     * @param folder   存储的目标文件夹
     * @param fileType 文件的业务类型
     * @return 最终的文件访问Key (无论是秒传还是新上传)
     */
    String quickUpload(MultipartFile file, String folder, FileTypeEnum fileType);
}
