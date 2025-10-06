package com.sf.zhimengjing.controller.admin;

import com.sf.zhimengjing.common.model.vo.OptionVO;
import com.sf.zhimengjing.entity.admin.AdminPermission;
import com.sf.zhimengjing.service.admin.AdminPermissionService;
import com.sf.zhimengjing.service.admin.AdminRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: AdminOptionController
 * @Author 殇枫
 * @Package com.sf.zhimengjing.controller.admin
 * @description: 后台选项控制器
 */
@Tag(name = "后台选项接口")
@RestController
@RequestMapping("/admin/option")
public class AdminOptionController {

    @Autowired
    private AdminPermissionService adminPermissionService;

    @Autowired
    private AdminRoleService adminRoleService;

    @Operation(summary = "获取所有权限选项")
    @GetMapping("/permissions")
    public List<OptionVO> getPermissionOptions() {
        List<AdminPermission> permissions = adminPermissionService.list();
        // 直接返回 List<OptionVO>
        return permissions.stream()
                .map(p -> new OptionVO(p.getPermissionCode(), p.getDescription()))
                .collect(Collectors.toList());
    }

    @Operation(summary = "获取所有角色编码选项")
    @GetMapping("/role-codes")
    public List<OptionVO> getRoleCodeOptions() {
        // 直接返回 List<OptionVO>
        return adminRoleService.getRoleCodeOptions();
    }
}
