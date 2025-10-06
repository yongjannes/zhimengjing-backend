package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.ai.AIPromptTemplateDTO;
import com.sf.zhimengjing.common.result.Result;
import com.sf.zhimengjing.common.util.SecurityUtils;
import com.sf.zhimengjing.service.admin.AIPromptTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Title: AIPromptTemplateController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @description: AI提示词模板管理控制器
 */
@RestController
@RequestMapping("/admin/ai/prompt-template")
@RequiredArgsConstructor
@Tag(name = "AI提示词模板管理接口")
@PreAuthorize("hasAuthority('system:ai:config')")
public class AIPromptTemplateController {

    private final AIPromptTemplateService templateService;

    /** 分页获取模板列表 */
    @GetMapping("/list")
    @Operation(summary = "1. 分页获取模板列表")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Log(module = "AI提示词模板", operation = "获取模板列表")
    public Result<IPage<AIPromptTemplateDTO>> getTemplates(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "模板类型") @RequestParam(required = false) String templateType) {

        Page<AIPromptTemplateDTO> page = new Page<>(current, size);
        return Result.success(templateService.getTemplates(page, templateType));
    }

    /** 获取模板详情 */
    @GetMapping("/{templateCode}")
    @Operation(summary = "2. 获取模板详情")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Log(module = "AI提示词模板", operation = "获取模板详情")
    public Result<AIPromptTemplateDTO> getTemplateDetail(
            @Parameter(description = "模板编码") @PathVariable String templateCode) {

        return Result.success(templateService.getTemplateDetail(templateCode));
    }

    /** 创建模板 */
    @PostMapping("/create")
    @Operation(summary = "3. 创建模板")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "AI提示词模板", operation = "创建模板")
    public Result<Boolean> createTemplate(
            @Validated @RequestBody AIPromptTemplateDTO.TemplateRequestDTO requestDTO) {

        Long operatorId = SecurityUtils.getUserId();
        return Result.success(templateService.createTemplate(requestDTO, operatorId));
    }

    /** 更新模板 */
    @PutMapping("/{templateCode}")
    @Operation(summary = "4. 更新模板")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "AI提示词模板", operation = "更新模板")
    public Result<Boolean> updateTemplate(
            @Parameter(description = "模板编码") @PathVariable String templateCode,
            @Validated @RequestBody AIPromptTemplateDTO.TemplateRequestDTO requestDTO) {

        Long operatorId = SecurityUtils.getUserId();
        return Result.success(templateService.updateTemplate(templateCode, requestDTO, operatorId));
    }

    /** 删除模板 */
    @DeleteMapping("/{templateCode}")
    @Operation(summary = "5. 删除模板")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "AI提示词模板", operation = "删除模板")
    public Result<Boolean> deleteTemplate(
            @Parameter(description = "模板编码") @PathVariable String templateCode) {

        Long operatorId = SecurityUtils.getUserId();
        return Result.success(templateService.deleteTemplate(templateCode, operatorId));
    }

    /** 启用/禁用模板 */
    @PutMapping("/{templateCode}/status")
    @Operation(summary = "6. 切换模板状态")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "AI提示词模板", operation = "切换模板状态")
    public Result<Boolean> toggleTemplateStatus(
            @Parameter(description = "模板编码") @PathVariable String templateCode,
            @Parameter(description = "是否激活") @RequestParam Boolean isActive) {

        Long operatorId = SecurityUtils.getUserId();
        return Result.success(templateService.toggleTemplateStatus(templateCode, isActive, operatorId));
    }

    /** 渲染模板 */
    @PostMapping("/{templateCode}/render")
    @Operation(summary = "7. 渲染模板")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "AI提示词模板", operation = "渲染模板")
    public Result<String> renderTemplate(
            @Parameter(description = "模板编码") @PathVariable String templateCode,
            @Parameter(description = "模板变量 JSON 字符串") @RequestParam String variablesJson) throws JsonProcessingException {

        // 把 JSON 字符串解析成 Map
        Map<String, Object> variables = new ObjectMapper().readValue(variablesJson, new TypeReference<Map<String, Object>>() {});
        return Result.success(templateService.renderTemplate(templateCode, variables));
    }


    /** 测试模板 */
    @PostMapping("/{templateCode}/test")
    @Operation(summary = "8. 测试模板")
    @PreAuthorize("hasRole('ADMIN')")
    @Log(module = "AI提示词模板", operation = "测试模板")
    public Result<String> testTemplate(
            @Parameter(description = "模板编码") @PathVariable String templateCode,
            @RequestBody AIPromptTemplateDTO.TemplateTestDTO testDTO) {

        return Result.success(templateService.testTemplate(testDTO));
    }

    /** 获取所有模板类型 */
    @GetMapping("/types")
    @Operation(summary = "9. 获取所有模板类型")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Log(module = "AI提示词模板", operation = "获取模板类型列表")
    public Result<List<String>> getAllTemplateTypes() {
        return Result.success(templateService.getAllTemplateTypes());
    }
}
