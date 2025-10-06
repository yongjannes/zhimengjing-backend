package com.sf.zhimengjing.controller.admin;

import com.sf.zhimengjing.common.model.dto.ai.AIDreamConfigDTO;
import com.sf.zhimengjing.common.result.Result;
import com.sf.zhimengjing.common.util.SecurityUtils;
import com.sf.zhimengjing.service.admin.AIDreamConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.sf.zhimengjing.common.annotation.Log;

import java.util.List;

/**
 * @Title: AIDreamConfigController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @description: AI梦境解析配置管理控制器
 */
@RestController
@RequestMapping("/admin/ai/dream-config")
@RequiredArgsConstructor
@Tag(name = "AI梦境解析配置管理接口")
@PreAuthorize("hasAuthority('system:ai:config')")
public class AIDreamConfigController {

    private final AIDreamConfigService dreamConfigService;

    /** 获取AI梦境解析配置列表 */
    @GetMapping("/list")
    @Operation(summary = "1. 获取AI梦境解析配置列表")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Log(module = "AI梦境解析配置", operation = "获取配置列表")
    public Result<List<AIDreamConfigDTO>> getDreamConfigs() {
        return Result.success(dreamConfigService.getAllDreamConfigs());
    }

    /** 获取指定模型的梦境解析配置 */
    @GetMapping("/{modelCode}")
    @Operation(summary = "2. 获取AI模型梦境解析配置")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Log(module = "AI梦境解析配置", operation = "获取指定模型配置")
    public Result<AIDreamConfigDTO> getDreamConfig(
            @Parameter(description = "模型编码") @PathVariable String modelCode) {
        return Result.success(dreamConfigService.getDreamConfigByModel(modelCode));
    }

    /** 创建/更新AI梦境解析配置 */
    @PostMapping("/save")
    @Operation(summary = "3. 保存AI梦境解析配置")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "AI梦境解析配置", operation = "保存配置")
    public Result<Boolean> saveDreamConfig(
            @Parameter(description = "梦境解析配置请求数据") @Validated @RequestBody AIDreamConfigDTO.DreamConfigRequestDTO requestDTO) {

        Long operatorId = SecurityUtils.getUserId();
        return Result.success(dreamConfigService.saveDreamConfig(requestDTO, operatorId));
    }

    /** 删除AI梦境解析配置 */
    @DeleteMapping("/{modelCode}")
    @Operation(summary = "4. 删除AI梦境解析配置")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "AI梦境解析配置", operation = "删除配置")
    public Result<Boolean> deleteDreamConfig(
            @Parameter(description = "模型编码") @PathVariable String modelCode) {

        Long operatorId = SecurityUtils.getUserId();
        return Result.success(dreamConfigService.deleteDreamConfig(modelCode, operatorId));
    }

    /** 启用/禁用梦境解析配置 */
    @PutMapping("/{modelCode}/status")
    @Operation(summary = "5. 切换配置状态")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "AI梦境解析配置", operation = "切换配置状态")
    public Result<Boolean> toggleConfigStatus(
            @Parameter(description = "模型编码") @PathVariable String modelCode,
            @Parameter(description = "是否激活") @RequestParam Boolean isActive) {

        Long operatorId = SecurityUtils.getUserId();
        return Result.success(dreamConfigService.toggleConfigStatus(modelCode, isActive, operatorId));
    }

    /** 测试梦境解析配置 */
    @PostMapping("/{modelCode}/test")
    @Operation(summary = "6. 测试梦境解析配置")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "AI梦境解析配置", operation = "测试配置")
    public Result<String> testDreamConfig(
            @Parameter(description = "模型编码") @PathVariable String modelCode,
            @Parameter(description = "测试梦境内容") @RequestParam String dreamContent) {

        return Result.success(dreamConfigService.testDreamAnalysis(modelCode, dreamContent));
    }

    /** 获取默认梦境解析配置 */
    @GetMapping("/default")
    @Operation(summary = "7. 获取默认梦境解析配置")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Log(module = "AI梦境解析配置", operation = "获取默认配置")
    public Result<AIDreamConfigDTO> getDefaultConfig() {
        return Result.success(dreamConfigService.getDefaultConfig());
    }
}
