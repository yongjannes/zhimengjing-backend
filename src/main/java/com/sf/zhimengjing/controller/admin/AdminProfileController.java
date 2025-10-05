package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.AdminChangePasswordDTO;
import com.sf.zhimengjing.common.model.dto.AdminUpdateInfoDTO;
import com.sf.zhimengjing.common.model.dto.ChangeEmailDTO;
import com.sf.zhimengjing.common.model.dto.SendChangeEmailCodeDTO;
import com.sf.zhimengjing.common.model.vo.AdminLoginLogVO;
import com.sf.zhimengjing.common.model.vo.AdminProfileVO;
import com.sf.zhimengjing.common.model.vo.AvatarVO;
import com.sf.zhimengjing.common.result.Result;
import com.sf.zhimengjing.common.util.SecurityUtils;
import com.sf.zhimengjing.service.admin.AdminProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * @Title: AdminProfileController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @description: 管理员个人中心控制器
 */
@RestController
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
@Tag(name = "管理员个人中心", description = "管理员个人信息管理、密码修改、登录日志等")
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    /**
     * 获取个人信息
     */
    @GetMapping("/info")
    @Operation(summary = "1. 获取个人信息")
    @Log(module = "个人中心", operation = "获取个人信息")
    public Result<AdminProfileVO> getProfile() {
        String adminIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long adminId = Long.parseLong(adminIdStr);
        AdminProfileVO profile = adminProfileService.getProfile(adminId);
        return Result.success(profile);
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/info")
    @Operation(summary = "2. 更新个人信息")
    @Log(module = "个人中心", operation = "更新个人信息")
    public Result<String> updateProfile(@Valid @RequestBody AdminUpdateInfoDTO updateDTO) {
        String adminIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long adminId = Long.parseLong(adminIdStr);
        adminProfileService.updateProfile(adminId, updateDTO);
        return Result.success("更新成功");
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "3. 修改密码")
    @Log(module = "个人中心", operation = "修改密码")
    public Result<String> changePassword(@Valid @RequestBody AdminChangePasswordDTO passwordDTO) {
        String adminIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long adminId = Long.parseLong(adminIdStr);
        adminProfileService.changePassword(adminId, passwordDTO);
        return Result.success("密码修改成功，请重新登录");
    }

    /**
     * 获取登录日志
     */
    @GetMapping("/loginLogs")
    @Operation(summary = "4. 获取登录日志")
    @Log(module = "个人中心", operation = "获取登录日志")
    public Result<Page<AdminLoginLogVO>> getLoginLogs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String adminIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long adminId = Long.parseLong(adminIdStr);
        Page<AdminLoginLogVO> logs = adminProfileService.getLoginLogs(adminId, pageNum, pageSize);
        return Result.success(logs);
    }

    /**
     * 发送修改邮箱验证码
     */
    @PostMapping("/email/send-code")
    @Operation(summary = "5. 发送修改邮箱验证码")
    @Log(module = "个人中心", operation = "发送修改邮箱验证码")
    public Result<String> sendChangeEmailCode(@Valid @RequestBody SendChangeEmailCodeDTO dto) {
        String adminIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long adminId = Long.parseLong(adminIdStr);
        adminProfileService.sendChangeEmailCode(adminId, dto.getNewEmail());
        return Result.success("验证码已发送到新邮箱，请查收");
    }

    /**
     * 验证并修改邮箱
     */
    @PutMapping("/email")
    @Operation(summary = "6. 修改邮箱")
    @Log(module = "个人中心", operation = "修改邮箱")
    public Result<String> changeEmail(@Valid @RequestBody ChangeEmailDTO dto) {
        String adminIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long adminId = Long.parseLong(adminIdStr);
        adminProfileService.changeEmail(adminId, dto.getNewEmail(), dto.getCaptcha());
        return Result.success("邮箱修改成功");
    }

    @GetMapping("/refresh-avatar")
    @Operation(summary = "7. 刷新头像URL", description = "获取一个新的带有时效性的头像访问URL")
    public AvatarVO refreshAvatar() {
        Long adminId = SecurityUtils.getUserId();
        return adminProfileService.refreshAvatar(adminId);
    }

}