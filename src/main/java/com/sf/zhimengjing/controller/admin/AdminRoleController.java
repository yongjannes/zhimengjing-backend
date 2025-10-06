package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.AdminRoleDTO;
import com.sf.zhimengjing.common.model.dto.AdminRoleQueryDTO;
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
     * 分页获取角色列表
     * @return 分页后的角色列表数据
     */
    @GetMapping("/list")
    @Operation(summary = "分页获取角色列表")
    @PreAuthorize("hasAuthority('system:role:manage')")
    public Page<AdminRole> getRoleListByPage(AdminRoleQueryDTO queryDTO) {
        return adminRoleService.getRoleListByPage(queryDTO);
    }

    @GetMapping("/list-all")
    @Operation(summary = "获取所有角色列表（不分页）")
    // 权限可以设置为拥有用户管理权限的用户即可调用
//    @PreAuthorize("hasAuthority('system:user:add') or hasAuthority('system:user:edit')")
    public List<AdminRole> getAllRoles() {
        // 使用 service.list() 方法获取所有未被逻辑删除的角色，并进行排序
        return adminRoleService.list(
                new LambdaQueryWrapper<AdminRole>()
                        .orderByDesc(AdminRole::getIsSystem) // 系统角色置顶
                        .orderByAsc(AdminRole::getSortOrder)   // 按排序值升序
                        .orderByDesc(AdminRole::getCreateTime) // 按创建时间降序
        );
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
     * 删除角色 (逻辑删除)
     * 返回 void，ResultAdvice 会自动封装为 Result.success()
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除角色")
    @Log(module = "角色管理", operation = "删除角色")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public void deleteRole(@PathVariable("ids") String ids) {
        adminRoleService.deleteRoles(ids);
    }

    /**
     * 根据ID获取角色详情
     * 直接返回 AdminRole 对象，ResultAdvice 会自动封装为 Result.success(role)
     *
     * @param id 角色ID
     * @return 角色详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情")
    @PreAuthorize("hasAuthority('system:role:view')")
    public AdminRole getRoleById(@PathVariable("id") Long id) {
        return adminRoleService.getRoleById(id);
    }
}