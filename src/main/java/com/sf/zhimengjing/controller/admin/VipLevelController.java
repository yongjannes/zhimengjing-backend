package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.model.dto.VipLevelDTO;
import com.sf.zhimengjing.service.admin.VipLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title: VipLevelController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @description: VIP等级管理控制器，提供VIP等级的增删改查、状态管理等接口
 */
@RestController
@RequestMapping("/admin/vip/levels")
@RequiredArgsConstructor
@Tag(name = "VIP等级管理接口")
public class VipLevelController {

    private final VipLevelService vipLevelService;

    /** 获取所有VIP等级 */
    @GetMapping
    @Operation(summary = "1. 获取所有VIP等级")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<VipLevelDTO> getAllLevels() {
        return vipLevelService.getAllLevels();
    }

    /** 获取启用的VIP等级 */
    @GetMapping("/active")
    @Operation(summary = "2. 获取启用的VIP等级")
    public List<VipLevelDTO> getActiveLevels() {
        return vipLevelService.getActiveLevels();
    }

    /** 根据ID获取VIP等级 */
    @GetMapping("/{levelId}")
    @Operation(summary = "3. 获取VIP等级详情")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public VipLevelDTO getLevelById(
            @Parameter(description = "等级ID") @PathVariable Long levelId) {
        return vipLevelService.getLevelById(levelId);
    }

    /** 创建VIP等级 */
    @PostMapping
    @Operation(summary = "4. 创建VIP等级")
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean createLevel(
            @Parameter(description = "等级创建请求数据") @Validated @RequestBody VipLevelDTO.LevelRequestDTO requestDTO) {
        return vipLevelService.createLevel(requestDTO);
    }

    /** 更新VIP等级 */
    @PutMapping("/{levelId}")
    @Operation(summary = "5. 更新VIP等级")
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean updateLevel(
            @Parameter(description = "等级ID") @PathVariable Long levelId,
            @Parameter(description = "等级更新请求数据") @Validated @RequestBody VipLevelDTO.LevelRequestDTO requestDTO) {
        return vipLevelService.updateLevel(levelId, requestDTO);
    }

    /** 启用/禁用VIP等级 */
    @PatchMapping("/{levelId}/status")
    @Operation(summary = "6. 启用/禁用VIP等级")
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean toggleLevelStatus(
            @Parameter(description = "等级ID") @PathVariable Long levelId,
            @Parameter(description = "是否启用") @RequestParam Boolean isActive) {
        return vipLevelService.toggleLevelStatus(levelId, isActive);
    }

    /** 删除VIP等级 */
    @DeleteMapping("/{levelId}")
    @Operation(summary = "7. 删除VIP等级")
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteLevel(
            @Parameter(description = "等级ID") @PathVariable Long levelId) {
        return vipLevelService.deleteLevel(levelId);
    }

    /** 获取等级统计信息 */
    @GetMapping("/stats")
    @Operation(summary = "8. 获取等级统计信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public VipLevelDTO.LevelStatsVO getLevelStats() {
        return vipLevelService.getLevelStats();
    }

    /** 分页查询VIP等级 */
    @GetMapping("/page")
    @Operation(summary = "9. 分页查询VIP等级")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public IPage<VipLevelDTO> getLevelPage(
            @Parameter(description = "页码，默认为1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页数量，默认为10") @RequestParam(defaultValue = "10") long size,
            @Parameter(description = "等级名称，可选") @RequestParam(required = false) String levelName,
            @Parameter(description = "是否启用，可选") @RequestParam(required = false) Boolean isActive) {

        Page<VipLevelDTO> page = new Page<>(current, size);
        return vipLevelService.getLevelPage(page, levelName, isActive);
    }
}