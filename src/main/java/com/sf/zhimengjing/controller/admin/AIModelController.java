package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.model.dto.ai.AIModelDTO;
import com.sf.zhimengjing.common.result.Result;
import com.sf.zhimengjing.service.admin.AIModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @Title: AIModelController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @description: AI模型管理控制器，提供AI模型配置、管理、测试等接口
 */
@RestController
@RequestMapping("/admin/ai/models")
@RequiredArgsConstructor
@Tag(name = "AI模型管理接口")
public class AIModelController {

    private final AIModelService aiModelService;

    /** 获取可用AI模型列表 */
    @GetMapping("/list")
    @Operation(summary = "1. 获取AI模型列表")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<IPage<AIModelDTO>> getAvailableModels(
            @Parameter(description = "页码，默认为1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页数量，默认为10") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "提供商过滤，可选") @RequestParam(required = false) String provider) {

        Page<AIModelDTO> page = new Page<>(current, size);
        return Result.success(aiModelService.getAvailableModels(page, provider));
    }

    /** 获取单个AI模型详情 */
    @GetMapping("/{modelCode}")
    @Operation(summary = "2. 获取AI模型详情")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<AIModelDTO> getModelDetail(
            @Parameter(description = "模型编码") @PathVariable String modelCode) {
        return Result.success(aiModelService.getModelDetail(modelCode));
    }

    /** 创建AI模型 */
    @PostMapping("/create")
    @Operation(summary = "3. 创建AI模型")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> createModel(
            @Parameter(description = "AI模型创建请求数据") @Validated @RequestBody AIModelDTO.ModelRequestDTO requestDTO) {

        Long operatorId = getCurrentUserId();
        return Result.success(aiModelService.createModel(requestDTO, operatorId));
    }

    /** 更新AI模型配置 */
    @PutMapping("/{modelCode}")
    @Operation(summary = "4. 更新AI模型配置")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> updateModel(
            @Parameter(description = "模型编码") @PathVariable String modelCode,
            @Parameter(description = "AI模型更新请求数据") @Validated @RequestBody AIModelDTO.ModelRequestDTO requestDTO) {

        Long operatorId = getCurrentUserId();
        return Result.success(aiModelService.updateModel(modelCode, requestDTO, operatorId));
    }

    /** 删除AI模型 */
    @DeleteMapping("/{modelCode}")
    @Operation(summary = "5. 删除AI模型")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> deleteModel(
            @Parameter(description = "模型编码") @PathVariable String modelCode) {

        Long operatorId = getCurrentUserId();
        return Result.success(aiModelService.deleteModel(modelCode, operatorId));
    }

    /** 切换默认模型 */
    @PostMapping("/{modelCode}/default")
    @Operation(summary = "6. 设置默认AI模型")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> switchDefaultModel(
            @Parameter(description = "模型编码") @PathVariable String modelCode) {

        Long operatorId = getCurrentUserId();
        return Result.success(aiModelService.switchDefaultModel(modelCode, operatorId));
    }

    /** 启用/禁用AI模型 */
    @PutMapping("/{modelCode}/status")
    @Operation(summary = "7. 切换AI模型状态")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> toggleModelStatus(
            @Parameter(description = "模型编码") @PathVariable String modelCode,
            @Parameter(description = "是否可用") @RequestParam Boolean isAvailable) {

        Long operatorId = getCurrentUserId();
        return Result.success(aiModelService.toggleModelStatus(modelCode, isAvailable, operatorId));
    }

    /** 获取AI模型统计信息 */
    @GetMapping("/stats")
    @Operation(summary = "8. 获取AI模型统计")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<List<AIModelDTO.ModelStatsVO>> getModelStats(
            @Parameter(description = "开始日期，可选") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "结束日期，可选") @RequestParam(required = false) LocalDate endDate) {

        return Result.success(aiModelService.getModelStats(startDate, endDate));
    }

    /** 测试AI模型连接 */
    @PostMapping("/{modelCode}/test")
    @Operation(summary = "9. 测试AI模型连接")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> testModelConnection(
            @Parameter(description = "模型编码") @PathVariable String modelCode) {

        return Result.success(aiModelService.testModelConnection(modelCode));
    }

    /** 获取所有提供商列表 */
    @GetMapping("/providers")
    @Operation(summary = "10. 获取AI提供商列表")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Result<List<String>> getAllProviders() {
        return Result.success(aiModelService.getAllProviders());
    }

    /** 获取当前登录用户ID (模拟) */
    private Long getCurrentUserId() {
        // 实际项目中可通过 SecurityContextHolder 获取登录用户ID
        return 501L;
    }
}