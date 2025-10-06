package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.SystemSettingDTO;
import com.sf.zhimengjing.common.model.vo.SystemSettingVO;
import com.sf.zhimengjing.service.admin.SystemSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Title: SystemSettingController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @Description: 系统设置控制器
 */
@RestController
@RequestMapping("/admin/system/settings")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('system:sys:config')")
@Tag(name = "系统设置管理", description = "系统配置相关操作接口")
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    @GetMapping("/list")
    @Operation(summary = "1. 获取系统配置列表")
    @PreAuthorize("hasRole('ADMIN')")
    public IPage<SystemSettingVO> getSettingList(@Valid SystemSettingDTO dto) {
        return systemSettingService.getSettingList(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "2. 根据ID获取配置详情")
    @PreAuthorize("hasRole('ADMIN')")
    public SystemSettingVO getSettingById(
            @Parameter(description = "配置ID") @PathVariable Long id) {
        return systemSettingService.getSettingById(id);
    }

    @GetMapping("/key/{settingKey}")
    @Operation(summary = "3. 根据键名获取配置")
    @PreAuthorize("hasRole('ADMIN')")
    public SystemSettingVO getSettingByKey(
            @Parameter(description = "配置键名") @PathVariable String settingKey) {
        return systemSettingService.getSettingByKey(settingKey);
    }

    @PostMapping
    @Operation(summary = "4. 创建系统配置")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "系统设置", operation = "创建配置")
    public SystemSettingVO createSetting(@Valid @RequestBody SystemSettingDTO dto) {
        return systemSettingService.createSetting(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "5. 更新系统配置")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "系统设置", operation = "更新配置")
    public SystemSettingVO updateSetting(
            @Parameter(description = "配置ID") @PathVariable Long id,
            @Valid @RequestBody SystemSettingDTO dto) {
        return systemSettingService.updateSetting(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "6. 删除系统配置")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "系统设置", operation = "删除配置")
    public Boolean deleteSetting(
            @Parameter(description = "配置ID") @PathVariable Long id) {
        return systemSettingService.deleteSetting(id);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "7. 根据分类获取配置")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getSettingsByCategory(
            @Parameter(description = "配置分类") @PathVariable String category) {
        return systemSettingService.getSettingsByCategory(category);
    }

    @PostMapping("/batch-update")
    @Operation(summary = "8. 批量更新配置")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "系统设置", operation = "批量更新配置")
    public Boolean batchUpdateSettings(@RequestBody Map<String, String> settings) {
        return systemSettingService.batchUpdateSettings(settings);
    }

    @PostMapping("/refresh-cache")
    @Operation(summary = "9. 刷新配置缓存")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "系统设置", operation = "刷新缓存")
    public Boolean refreshCache() {
        return systemSettingService.refreshCache();
    }

    @GetMapping("/statistics")
    @Operation(summary = "10. 获取配置统计")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> getSettingStatistics() {
        return systemSettingService.getSettingStatistics();
    }
}
