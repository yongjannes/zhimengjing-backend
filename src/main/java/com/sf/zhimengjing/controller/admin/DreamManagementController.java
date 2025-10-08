package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.DreamAuditDTO;
import com.sf.zhimengjing.common.model.dto.DreamQueryDTO;
import com.sf.zhimengjing.common.model.vo.DreamListVO;
import com.sf.zhimengjing.common.model.vo.DreamStatisticsVO;
import com.sf.zhimengjing.entity.admin.DreamRecord;
import com.sf.zhimengjing.service.admin.DreamManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title: DreamManagementController
 * @Author: 殤枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @Description: 后台梦境管理控制器，提供管理员对用户梦境的分页查询、统计、详情、审核及删除接口。
 */
@RestController
@RequestMapping("/admin/dreams")
@RequiredArgsConstructor
@Tag(name = "梦境管理接口", description = "后台梦境管理相关接口")
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('dream:manage')")
public class DreamManagementController {

    private final DreamManagementService dreamManagementService;

    /**
     * 分页查询梦境列表
     *
     * @param dreamQueryDTO 查询条件，包括用户名、标题、分类、标签、日期范围、状态及分页参数
     * @return 分页的梦境列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询梦境列表")
    @Log(module = "梦境管理", operation = "查询梦境列表")
    public IPage<DreamListVO> pageDreams(DreamQueryDTO dreamQueryDTO) {
        return dreamManagementService.pageDreams(dreamQueryDTO);
    }

    /**
     * 获取梦境统计信息
     *
     * @return 梦境总数、审核中、已审核、已拒绝、公开、今日/本周/本月新增数量等统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取梦境统计信息")
    @Log(module = "梦境管理", operation = "获取梦境统计")
    public DreamStatisticsVO getDreamStatistics() {
        return dreamManagementService.getDreamStatistics();
    }

    /**
     * 获取梦境详情
     *
     * @param id 梦境ID
     * @return 梦境详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取梦境详情")
    @Log(module = "梦境管理", operation = "获取梦境详情")
    public DreamRecord getDreamDetail(@PathVariable Long id) {
        return dreamManagementService.getDreamDetail(id);
    }

    /**
     * 审核梦境
     *
     * @param auditDTO 审核数据
     */
    @PutMapping("/audit")
    @Operation(summary = "审核梦境")
    @Log(module = "梦境管理", operation = "审核梦境")
    public void auditDreams(@Validated @RequestBody DreamAuditDTO auditDTO) {
        dreamManagementService.auditDreams(auditDTO);
    }

    /**
     * 删除梦境
     *
     * @param dreamIds 梦境ID列表
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除梦境")
    @Log(module = "梦境管理", operation = "删除梦境")
    public void deleteDreams(@RequestParam("ids") List<Long> dreamIds) {
        dreamManagementService.deleteDreams(dreamIds);
    }
}