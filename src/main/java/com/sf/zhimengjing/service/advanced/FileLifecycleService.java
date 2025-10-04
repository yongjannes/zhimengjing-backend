package com.sf.zhimengjing.service.advanced;

/**
 * @Title: FileLifecycleService
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.advanced
 * @description: 文件生命周期管理服务接口，用于定时清理过期文件。
 */
public interface FileLifecycleService {

    /**
     * 执行清理过期文件的定时任务。
     * 该方法由 Spring 的调度器根据 cron 表达式自动调用。
     */
    void cleanExpiredFiles();
}
