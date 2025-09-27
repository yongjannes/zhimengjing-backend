package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.ConfigChangeHistoryDTO;
import com.sf.zhimengjing.common.model.dto.RecordConfigChangeDTO;
import com.sf.zhimengjing.common.model.vo.ConfigChangeHistoryVO;
import com.sf.zhimengjing.service.admin.ConfigChangeHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: ConfigChangeHistoryController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @Description: 配置变更历史控制器
 * 提供对系统配置变更历史的查询与管理接口
 */
@RestController
@RequestMapping("/admin/system/config-history")
@RequiredArgsConstructor
@Tag(name = "配置变更历史", description = "配置变更历史相关操作接口")
public class ConfigChangeHistoryController {

    private final ConfigChangeHistoryService configChangeHistoryService;

    @GetMapping("/list")
    @Operation(summary = "1. 获取配置变更历史列表")
    @PreAuthorize("hasRole('ADMIN')")
    public IPage<ConfigChangeHistoryVO> getChangeHistoryList(@Valid ConfigChangeHistoryDTO dto) {
        return configChangeHistoryService.getChangeHistoryList(dto);
    }

    @GetMapping("/config/{configKey}")
    @Operation(summary = "2. 根据配置键获取变更历史")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ConfigChangeHistoryVO> getHistoryByConfigKey(
            @Parameter(description = "配置键") @PathVariable String configKey,
            @Parameter(description = "限制记录数") @RequestParam(defaultValue = "10") Integer limit) {
        return configChangeHistoryService.getHistoryByConfigKey(configKey, limit);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "3. 根据用户ID获取变更历史")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ConfigChangeHistoryVO> getHistoryByUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "开始时间") @RequestParam(required = false) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) LocalDateTime endTime,
            @Parameter(description = "限制记录数") @RequestParam(defaultValue = "20") Integer limit) {
        return configChangeHistoryService.getHistoryByUser(userId, startTime, endTime, limit);
    }

    @PostMapping("/clean")
    @Operation(summary = "4. 清理过期变更历史")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "配置变更历史", operation = "清理过期历史")
    public String  cleanExpiredHistory(
            @Parameter(description = "保留天数") @RequestParam Integer daysToKeep) {
        return configChangeHistoryService.cleanExpiredHistory(daysToKeep);
    }

    @PostMapping("/record")
    @Operation(summary = "5. 手动记录配置变更历史")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "配置变更历史", operation = "手动记录配置变更")
    public Boolean recordConfigChange(@Valid @RequestBody RecordConfigChangeDTO dto, HttpServletRequest request) {
        // 通常情况下，IP地址和UserAgent可以从请求头中自动获取，但在手动记录的场景下，也允许由调用方直接传入
        String ipAddress = (dto.getIpAddress() != null) ? dto.getIpAddress() : request.getRemoteAddr();
        String userAgent = (dto.getUserAgent() != null) ? dto.getUserAgent() : request.getHeader("User-Agent");

        return configChangeHistoryService.recordConfigChange(
                dto.getConfigKey(),
                dto.getOldValue(),
                dto.getNewValue(),
                dto.getChangeType(),
                dto.getChangeReason(),
                dto.getChangedBy(),
                ipAddress,
                userAgent
        );
    }
}