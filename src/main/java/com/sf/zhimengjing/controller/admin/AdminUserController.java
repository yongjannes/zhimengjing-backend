package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.AdminUserDTO;
import com.sf.zhimengjing.common.model.vo.AdminUserVO;
import com.sf.zhimengjing.common.result.Result;
import com.sf.zhimengjing.service.admin.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * @Title: AdminUserController
 * @Author 殇枫
 * @Package com.sf.zhimengjing.controller.admin
 * @description: 后台管理员控制器，提供管理员分页查询、创建、更新和逻辑删除接口
 */
@RestController
@RequestMapping("/admin/manage/users")
@RequiredArgsConstructor
@Tag(name = "后台管理员管理", description = "用于管理后台系统的管理员账号")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 分页查询后台管理员列表
     *
     * @param pageNum  当前页码，默认为1
     * @param pageSize 每页记录数，默认为10
     * @return Result<IPage<AdminUserVO>> 分页结果封装
     */
    @GetMapping
    @Operation(summary = "分页查询后台管理员列表")
    @Log(module = "管理员管理", operation = "查询列表")
    public Result<IPage<AdminUserVO>> pageAdminUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer status) {
        return Result.success(adminUserService.pageAdminUsers(pageNum, pageSize, username, realName, status));
    }

    /**
     * 创建后台管理员
     *
     * @param adminUserDTO 创建管理员所需数据
     * @return Result<String> 创建结果信息
     */
    @PostMapping
    @Operation(summary = "创建后台管理员")
    @Log(module = "管理员管理", operation = "创建管理员")
    public Result<String> createAdminUser(@Valid @RequestBody AdminUserDTO adminUserDTO) {
        String creatorIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long creatorId = Long.parseLong(creatorIdStr);
        adminUserService.createAdminUser(adminUserDTO, creatorId);
        return Result.success("创建成功");
    }

    /**
     * 更新后台管理员信息
     *
     * @param id           被更新管理员ID
     * @param adminUserDTO 更新数据
     * @return Result<String> 更新结果信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新后台管理员信息")
    @Log(module = "管理员管理", operation = "更新管理员")
    public Result<String> updateAdminUser(@PathVariable Long id, @Valid @RequestBody AdminUserDTO adminUserDTO) {
        String updaterIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long updaterId = Long.parseLong(updaterIdStr);
        adminUserService.updateAdminUser(id, adminUserDTO, updaterId);
        return Result.success("更新成功");
    }

    /**
     * 删除后台管理员 (逻辑删除)
     * <p>
     * 将管理员状态置为禁用，并记录操作人ID
     *
     * @param id 被删除管理员ID
     * @return Result<String> 删除结果信息
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除后台管理员")
    @Log(module = "管理员管理", operation = "删除管理员")
    public Result<String> deleteAdminUser(@PathVariable Long id) {
        String operatorIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long operatorId = Long.parseLong(operatorIdStr);

        adminUserService.deleteAdminUser(id, operatorId);
        return Result.success("删除成功");
    }

    /**
     *获取指定用户详情
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public Result<AdminUserVO> getAdminUserById(@PathVariable Long id) {
        AdminUserVO adminUserVO = adminUserService.getAdminUserById(id);
        return Result.success(adminUserVO);
    }
}
