package com.sf.zhimengjing.service.advanced.impl;

import com.sf.zhimengjing.common.config.properties.OssProperties;
import com.sf.zhimengjing.service.OssService;
import com.sf.zhimengjing.service.advanced.FileLifecycleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @Title: FileLifecycleServiceImpl
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.advanced.impl
 * @description: 文件生命周期管理服务实现类，负责定期清理 OSS 中的过期文件。
 *               每天凌晨 2 点执行清理任务，根据配置的保留天数自动删除过期文件。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileLifecycleServiceImpl implements FileLifecycleService {

    private final OssService ossService;
    private final OssProperties ossProperties;

    // TODO: 注入您用于记录文件信息的Mapper

    /**
     * 每天凌晨2点执行清理任务
     */
    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredFiles() {
        OssProperties.AutoDeleteConfig autoDeleteConfig = ossProperties.getAutoDelete();
        if (autoDeleteConfig == null || !Boolean.TRUE.equals(autoDeleteConfig.getEnabled())) {
            log.info("[定时任务] 文件自动清理功能未启用，跳过任务。");
            return;
        }

        Integer days = autoDeleteConfig.getDays();
        if (days == null || days <= 0) {
            log.warn("[定时任务] 文件保留天数未配置或无效，跳过清理任务。");
            return;
        }

        log.info("[定时任务] 开始执行：清理 {} 天前的过期文件...", days);

        // ======================= 核心业务逻辑 (待实现) =======================
        //  此部分需要您根据数据库表结构自行实现
        //  1. 查询数据库中所有上传时间早于 'now() - days' 的文件记录
        //  2. 遍历过期文件列表
        //  3. 调用 ossService.deleteFile(record.getFileKey()) 删除文件
        //  4. 如果删除成功，则从数据库中删除该条记录
        // =================================================================

        log.info("[定时任务] 文件定时清理任务执行完毕。");
    }
}