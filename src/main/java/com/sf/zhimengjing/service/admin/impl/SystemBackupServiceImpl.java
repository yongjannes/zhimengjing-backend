package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.SystemBackupDTO;
import com.sf.zhimengjing.common.model.vo.SystemBackupVO;
import com.sf.zhimengjing.entity.admin.SystemBackup;
import com.sf.zhimengjing.mapper.admin.SystemBackupMapper;
import com.sf.zhimengjing.service.admin.SystemBackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @Title: SystemBackupServiceImpl
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.admin.impl
 * @description: 负责系统备份相关业务逻辑，包括备份记录的增删改查、备份执行、恢复、完整性检查和统计信息等功能。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemBackupServiceImpl extends ServiceImpl<SystemBackupMapper, SystemBackup> implements SystemBackupService {

    /**
     * 获取备份列表（分页）
     *
     * @param dto 查询条件 DTO，包括关键字、类型、状态、创建者等信息
     * @return 分页后的备份记录 VO 列表
     */
    @Override
    public IPage<SystemBackupVO> getBackupList(SystemBackupDTO dto) {
        LambdaQueryWrapper<SystemBackup> wrapper = new LambdaQueryWrapper<SystemBackup>()
                .like(StringUtils.hasText(dto.getKeyword()), SystemBackup::getBackupName, dto.getKeyword())
                .eq(StringUtils.hasText(dto.getBackupType()), SystemBackup::getBackupType, dto.getBackupType())
                .eq(StringUtils.hasText(dto.getBackupStatus()), SystemBackup::getBackupStatus, dto.getBackupStatus())
                .eq(dto.getCreatedBy() != null, SystemBackup::getCreatedBy, dto.getCreatedBy())
                .orderByDesc(SystemBackup::getCreateTime);

        Page<SystemBackup> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<SystemBackup> entityPage = this.page(page, wrapper);

        return entityPage.convert(this::convertToVO);
    }

    /**
     * 创建备份记录并异步执行备份
     *
     * @param dto    备份信息 DTO
     * @param userId 执行备份的用户 ID
     * @return 创建的备份记录 VO
     * @throws GeneralBusinessException 如果创建备份记录失败，则抛出业务异常
     */
    @Override
    @Transactional
    public SystemBackupVO createBackup(SystemBackupDTO dto, Long userId) {
        SystemBackup backup = new SystemBackup();
        backup.setBackupName(dto.getBackupName());
        backup.setBackupType(dto.getBackupType());
        backup.setBackupStatus("PROCESSING");
        backup.setStartTime(LocalDateTime.now());
        backup.setCreatedBy(userId);

        boolean saved = this.save(backup);
        if (!saved) {
            throw new GeneralBusinessException("创建备份记录失败");
        }

        // 异步执行备份任务
        executeBackupAsync(backup);
        return convertToVO(backup);
    }

    /**
     * 删除备份记录
     *
     * @param id 备份记录 ID
     * @return 删除是否成功
     */
    @Override
    public Boolean deleteBackup(Long id) {
        SystemBackup backup = this.getById(id);
        if (backup == null) {
            throw new GeneralBusinessException("备份记录不存在");
        }

        // 删除备份文件
        if (StringUtils.hasText(backup.getFilePath())) {
            try {
                Files.deleteIfExists(Paths.get(backup.getFilePath()));
            } catch (IOException e) {
                log.error("删除备份文件失败: {}", e.getMessage());
                throw new GeneralBusinessException("删除备份文件失败");
            }
        }

        return this.removeById(id);
    }

    /**
     * 获取备份文件下载 URL
     *
     * @param id 备份记录 ID
     * @return 下载 URL 字符串
     */
    @Override
    public String getDownloadUrl(Long id) {
        SystemBackup backup = this.getById(id);
        if (backup == null || !"SUCCESS".equals(backup.getBackupStatus())) {
            throw new GeneralBusinessException("备份文件不存在或状态异常");
        }
        return backup.getFilePath();
    }

    /**
     * 恢复备份
     *
     * @param id     备份记录 ID
     * @param userId 操作用户 ID
     * @return 恢复是否成功
     */
    @Override
    public Boolean restoreBackup(Long id, Long userId) {
        SystemBackup backup = this.getById(id);
        if (backup == null || !"SUCCESS".equals(backup.getBackupStatus())) {
            throw new GeneralBusinessException("备份不存在或状态不正确，无法恢复");
        }

        backup.setBackupStatus("RESTORING");
        this.updateById(backup);

        log.info("用户 {} 开始恢复备份 {}", userId, id);
        executeRestoreAsync(backup, userId);
        return true;
    }

    /**
     * 测试备份完整性
     *
     * @param id 备份记录 ID
     * @return 测试是否成功
     */
    @Override
    public Boolean testBackupIntegrity(Long id) {
        SystemBackup backup = this.getById(id);
        if (backup == null) {
            throw new GeneralBusinessException("备份记录不存在");
        }

        if (!"SUCCESS".equals(backup.getBackupStatus())) {
            log.warn("备份 {} 状态不为 SUCCESS，可能无法保证完整性", id);
        }

        File backupFile = new File(backup.getFilePath());
        if (!backupFile.exists() || !backupFile.isFile()) {
            log.error("备份文件 {} 不存在或不是一个有效文件", backup.getFilePath());
            return false;
        }

        // 模拟检查文件是否损坏，例如检查文件大小
        if (backupFile.length() != backup.getBackupSize()) {
            log.error("备份文件大小与记录不符，可能已损坏");
            return false;
        }

        log.info("备份 {} 完整性检查通过", id);
        return true;
    }

    /**
     * 获取备份统计信息
     *
     * @return 统计信息 Map，例如总备份数、成功数、失败数等
     */
    @Override
    public Map<String, Long> getBackupStatistics() {
        return baseMapper.getBackupStatistics();
    }

    /**
     * 异步执行备份任务
     *
     * @param backup 备份记录实体
     * @return CompletableFuture<Void> 异步任务对象
     */
    @Async
    public CompletableFuture<Void> executeBackupAsync(SystemBackup backup) {
        try {
            Thread.sleep(3000); // 模拟备份耗时
            backup.setBackupStatus("SUCCESS");
            backup.setBackupSize(104857600L); // 100MB
            backup.setEndTime(LocalDateTime.now());
            this.updateById(backup);
        } catch (Exception e) {
            backup.setBackupStatus("FAILED");
            backup.setErrorMessage(e.getMessage());
            this.updateById(backup);
        }
        return CompletableFuture.completedFuture(null);
    }


    /**
     * 异步执行恢复任务
     *
     * @param backup 备份记录
     * @param userId 操作用户
     */
    @Async
    public void executeRestoreAsync(SystemBackup backup, Long userId) {
        try {
            log.info("开始执行恢复任务: {}", backup.getId());
            // 模拟恢复过程
            Thread.sleep(10000);

            // 恢复成功后，更新备份状态
            backup.setBackupStatus("SUCCESS");
            this.updateById(backup);
            log.info("备份 {} 已成功恢复，操作员: {}", backup.getId(), userId);
        } catch (InterruptedException e) {
            log.error("恢复任务被中断: {}", e.getMessage());
            backup.setBackupStatus("FAILED");
            backup.setErrorMessage("恢复任务被中断: " + e.getMessage());
            this.updateById(backup);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("恢复备份 {} 失败: {}", backup.getId(), e.getMessage());
            backup.setBackupStatus("FAILED");
            backup.setErrorMessage(e.getMessage());
            this.updateById(backup);
        }
    }

    /**
     * 将实体对象转换为 VO 对象
     *
     * @param entity 系统备份实体
     * @return 系统备份 VO 对象
     */
    private SystemBackupVO convertToVO(SystemBackup entity) {
        SystemBackupVO vo = new SystemBackupVO();
        vo.setId(entity.getId());
        vo.setBackupName(entity.getBackupName());
        vo.setBackupType(entity.getBackupType());
        vo.setBackupSize(entity.getBackupSize());
        vo.setBackupSizeFormatted(formatFileSize(entity.getBackupSize()));
        vo.setBackupStatus(entity.getBackupStatus());
        vo.setErrorMessage(entity.getErrorMessage());
        vo.setStartTime(entity.getStartTime());
        vo.setEndTime(entity.getEndTime());
        vo.setCreatedBy(entity.getCreatedBy());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    /**
     * 格式化文件大小显示
     *
     * @param size 文件大小（字节）
     * @return 格式化后的字符串，例如 1.23 MB
     */
    private String formatFileSize(Long size) {
        if (size == null || size == 0) return "-";
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double fileSize = size.doubleValue();
        while (fileSize >= 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }
        return String.format("%.2f %s", fileSize, units[unitIndex]);
    }
}