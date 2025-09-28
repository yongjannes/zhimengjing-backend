package com.sf.zhimengjing.controller.admin;

import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.AdminRoleDTO;
import com.sf.zhimengjing.entity.admin.AdminRole;
import com.sf.zhimengjing.service.admin.AdminRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title: AdminRoleController
 * @Package: com.sf.zhimengjing.controller.admin
 * @description: 后台角色管理控制器
 */
@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
@Tag(name = "后台角色管理接口", description = "提供角色的增删改查功能")
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    /**
     * 获取角色列表
     * @return 角色列表数据，会被 ResultAdvice 自动封装
     */
    @GetMapping("/list")
    @Operation(summary = "获取角色列表")
    @PreAuthorize("hasAuthority('system:role:manage')")
    public List<AdminRole> getRoleList() {
        return adminRoleService.getRoleList();
    }

    /**
     * 新增角色
     * 返回 void，ResultAdvice 会自动封装为 Result.success()
     */
    @PostMapping
    @Operation(summary = "新增角色")
    @Log(module = "角色管理", operation = "新增角色")
    @PreAuthorize("hasAuthority('system:role:add')")
    public void addRole(@Valid @RequestBody AdminRoleDTO adminRoleDTO) {
        adminRoleService.addRole(adminRoleDTO);
    }

    /**
     * 更新角色
     * 返回 void，ResultAdvice 会自动封装为 Result.success()
     */
    @PutMapping
    @Operation(summary = "更新角色")
    @Log(module = "角色管理", operation = "更新角色")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public void updateRole(@Valid @RequestBody AdminRoleDTO adminRoleDTO) {
        adminRoleService.updateRole(adminRoleDTO);
    }

    /**
     * 删除角色
     * 返回 void，ResultAdvice 会自动封装为 Result.success()
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    @Log(module = "角色管理", operation = "删除角色")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public void deleteRole(@PathVariable("id") Long roleId) {
        adminRoleService.deleteRole(roleId);
    }
}