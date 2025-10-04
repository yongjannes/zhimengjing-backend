package com.sf.zhimengjing.service.advanced.impl;

import cn.hutool.crypto.SecureUtil;
import com.sf.zhimengjing.common.enumerate.FileTypeEnum;
import com.sf.zhimengjing.service.OssService;
import com.sf.zhimengjing.service.advanced.QuickUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

/**
 * @Title: QuickUploadServiceImpl
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.advanced.impl
 * @description: 文件秒传服务实现类，通过文件MD5实现秒传功能。
 *               若文件已存在于OSS，则直接返回对应FileKey，否则执行正常上传流程并缓存MD5。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuickUploadServiceImpl implements QuickUploadService {

    private final OssService ossService;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String FILE_MD5_KEY_PREFIX = "file:md5:";
    private static final long CACHE_EXPIRE_DAYS = 30;

    @Override
    public String quickUpload(MultipartFile file, String folder, FileTypeEnum fileType) {
        try {
            String md5 = SecureUtil.md5(file.getInputStream());
            log.info("计算文件MD5: {}", md5);

            String cacheKey = FILE_MD5_KEY_PREFIX + md5;
            String fileKey = stringRedisTemplate.opsForValue().get(cacheKey);

            if (fileKey != null) {
                String existResult = ossService.fileExists(fileKey);
                if (existResult != null && existResult.startsWith("文件存在")) {
                    log.info("文件秒传成功，MD5: {}, FileKey: {}", md5, fileKey);
                    stringRedisTemplate.expire(cacheKey, CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
                    return fileKey;
                } else {
                    log.warn("缓存中的FileKey {} 已不存在于OSS, 清除缓存", fileKey);
                    stringRedisTemplate.delete(cacheKey);
                }
            }

            log.info("Redis未命中或OSS文件不存在，执行正常上传流程");
            String newFileKey = ossService.uploadFile(file, folder, fileType);

            stringRedisTemplate.opsForValue().set(cacheKey, newFileKey, CACHE_EXPIRE_DAYS, TimeUnit.DAYS);
            log.info("新文件上传成功，并已写入缓存，MD5: {}, FileKey: {}", md5, newFileKey);

            return newFileKey;
        } catch (Exception e) {
            log.error("秒传或上传过程中发生错误", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
}