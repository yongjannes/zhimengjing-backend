package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.SystemBackupDTO;
import com.sf.zhimengjing.common.model.vo.SystemBackupVO;
import com.sf.zhimengjing.common.util.SecurityUtils;
import com.sf.zhimengjing.service.admin.SystemBackupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Title: SystemBackupController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @Description: 系统备份控制器
 */
@RestController
@RequestMapping("/admin/system/backups")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('system:sys:config')")
@Tag(name = "系统备份管理", description = "系统备份相关操作接口")
public class SystemBackupController {

    private final SystemBackupService systemBackupService;

    @GetMapping("/list")
    @Operation(summary = "1. 获取备份列表")
    @PreAuthorize("hasRole('ADMIN')")
    public IPage<SystemBackupVO> getBackupList(@Valid SystemBackupDTO dto) {
        return systemBackupService.getBackupList(dto);
    }

    @PostMapping
    @Operation(summary = "2. 创建系统备份")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "系统备份", operation = "创建备份")
    public SystemBackupVO createBackup(@Valid @RequestBody SystemBackupDTO dto) {
        Long userId = SecurityUtils.getUserId();
        return systemBackupService.createBackup(dto, userId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "3. 删除备份")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "系统备份", operation = "删除备份")
    public Boolean deleteBackup(
            @Parameter(description = "备份ID") @PathVariable Long id) {
        return systemBackupService.deleteBackup(id);
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "4. 下载备份文件")
    @PreAuthorize("hasRole('ADMIN')")
    public String getDownloadUrl(
            @Parameter(description = "备份ID") @PathVariable Long id) {
        return systemBackupService.getDownloadUrl(id);
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "5. 恢复系统备份")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "系统备份", operation = "恢复备份")
    public Boolean restoreBackup(
            @Parameter(description = "备份ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getUserId();
        return systemBackupService.restoreBackup(id, userId);
    }

    @PostMapping("/{id}/test")
    @Operation(summary = "6. 测试备份完整性")
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean testBackupIntegrity(
            @Parameter(description = "备份ID") @PathVariable Long id) {
        return systemBackupService.testBackupIntegrity(id);
    }

    @GetMapping("/statistics")
    @Operation(summary = "7. 获取备份统计")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> getBackupStatistics() {
        return systemBackupService.getBackupStatistics();
    }

}