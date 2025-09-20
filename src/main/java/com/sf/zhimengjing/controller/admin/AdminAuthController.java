package com.sf.zhimengjing.controller.admin;

import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.annotation.RepeatSubmit;
import com.sf.zhimengjing.common.model.dto.AdminChangePasswordDTO;
import com.sf.zhimengjing.common.model.dto.AdminLoginDTO;
import com.sf.zhimengjing.common.model.vo.AdminInfoVO;
import com.sf.zhimengjing.common.model.vo.AdminLoginVO;
import com.sf.zhimengjing.common.result.Result;
import com.sf.zhimengjing.service.admin.AdminAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * @Title: AdminAuthController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @description: 后台管理员认证控制器
 *               提供登录、登出、获取管理员信息、修改密码和重置密码接口。
 */
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
@Tag(name = "后台认证接口", description = "后台管理员登录、登出、信息获取等操作")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    /**
     * 管理员登录接口
     *
     * @param loginDTO 登录信息，包括用户名、密码和验证码
     * @param request  HttpServletRequest对象，用于获取客户端IP和User-Agent
     * @return 登录结果，包括JWT令牌
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    @RepeatSubmit(message = "登录请求过于频繁，请稍后再试！")
    @Log(module = "后台认证", operation = "管理员登录")
    public Result<AdminLoginVO> login(@Valid @RequestBody AdminLoginDTO loginDTO, HttpServletRequest request) {
        AdminLoginVO loginVO = adminAuthService.login(loginDTO, request);
        return Result.success(loginVO);
    }

    /**
     * 获取当前登录管理员信息接口
     *
     * @return 管理员信息，包括用户名、真实姓名、头像、角色和权限
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前登录管理员信息")
    @Log(module = "后台认证", operation = "获取管理员信息")
    public Result<AdminInfoVO> getAdminInfo() {
        String adminIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long adminId = Long.parseLong(adminIdStr);
        AdminInfoVO adminInfoVO = adminAuthService.getAdminInfo(adminId);
        return Result.success(adminInfoVO);
    }

    /**
     * 管理员登出接口
     *
     * @return 登出成功信息
     */
    @PostMapping("/logout")
    @Operation(summary = "管理员登出")
    @Log(module = "后台认证", operation = "管理员登出")
    public Result<String> logout() {
        adminAuthService.logout();
        return Result.success("登出成功");
    }

    /**
     * 修改管理员密码接口
     *
     * @param passwordDTO 包含旧密码、新密码和确认新密码
     * @return 修改成功提示
     */
    @PutMapping("/changePassword")
    @Operation(summary = "修改密码")
    @Log(module = "后台认证", operation = "管理员修改密码")
    public Result<String> changePassword(@Valid @RequestBody AdminChangePasswordDTO passwordDTO) {
        String adminIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long adminId = Long.parseLong(adminIdStr);
        adminAuthService.changePassword(adminId, passwordDTO);
        return Result.success("密码修改成功");
    }

    /**
     * 重置管理员密码接口
     *
     * @param targetAdminId 目标管理员ID
     * @return 新生成的密码
     */
    @PutMapping("/resetPassword/{id}")
    @Operation(summary = "重置密码")
    @Log(module = "后台认证", operation = "重置管理员密码")
    public Result<String> resetPassword(@PathVariable("id") Long targetAdminId) {
        String adminIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentAdminId = Long.parseLong(adminIdStr);
        String newPassword = adminAuthService.resetPassword(targetAdminId, currentAdminId);
        return Result.success("重置密码成功，新密码为：" + newPassword);
    }
}
