package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.SystemLogDTO;
import com.sf.zhimengjing.common.model.vo.SystemLogVO;
import com.sf.zhimengjing.service.admin.SystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * @Title: SystemLogController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @Description: 系统日志控制器
 */
@RestController
@RequestMapping("/admin/system/logs")
@RequiredArgsConstructor
@Tag(name = "系统日志管理", description = "系统日志相关操作接口")
public class SystemLogController {

    private final SystemLogService systemLogService;

    @GetMapping("/list")
    @Operation(summary = "1. 获取系统日志列表")
    @PreAuthorize("hasRole('ADMIN')")
    public IPage<SystemLogVO> getLogList(@Valid SystemLogDTO dto) {
        return systemLogService.getLogList(dto);
    }

    @GetMapping("/statistics")
    @Operation(summary = "2. 获取日志统计")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> getLogStatistics(
            @Parameter(description = "开始时间") @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate  endTime) {
        return systemLogService.getLogStatistics(startTime, endTime);
    }

    @PostMapping("/clean")
    @Operation(summary = "3. 清理过期日志")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "系统日志", operation = "清理过期日志")
    public Boolean cleanExpiredLogs(
            @Parameter(description = "保留天数") @RequestParam Integer daysToKeep) {
        return systemLogService.cleanExpiredLogs(daysToKeep);
    }

    @GetMapping("/export") // 通常导出用 GET 更合适，与 UserController 保持一致
    @Operation(summary = "4. 导出日志")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "系统日志", operation = "导出日志")
    public void exportLogs(@Valid SystemLogDTO dto, HttpServletResponse response) {
        // 直接将请求参数和 response 对象传递给服务层处理
        systemLogService.exportLogs(dto, response);
    }

}