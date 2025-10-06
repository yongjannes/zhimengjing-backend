package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.annotation.Log;
import com.sf.zhimengjing.common.model.dto.UserQueryDTO;
import com.sf.zhimengjing.common.model.vo.UserDetailVO;
import com.sf.zhimengjing.common.model.vo.UserGrowthTrendVO;
import com.sf.zhimengjing.common.model.vo.UserListVO;
import com.sf.zhimengjing.common.model.vo.UserStatisticsVO;
import com.sf.zhimengjing.common.result.Result;
import com.sf.zhimengjing.entity.User;
import com.sf.zhimengjing.service.admin.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: UserController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @Description: 后台用户管理控制器
 */
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Tag(name = "用户管理接口", description = "后台用户管理接口")
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户列表
     *
     * @param userQueryDTO 查询条件
     * @return IPage<UserListVO> 用户列表分页结果
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询用户列表")
    @PreAuthorize("hasAuthority('user:normal:view')")
    @Log(module = "用户管理", operation = "查询用户列表")
    public Result<IPage<UserListVO>> pageUsers(@Valid UserQueryDTO userQueryDTO) {
        IPage<UserListVO> result = userService.pageUsers(userQueryDTO);
        return Result.success(result);
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return UserDetailVO 用户详细信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    @PreAuthorize("hasAuthority('user:normal:view')")
    @Log(module = "用户管理", operation = "获取用户详情")
    public Result<UserDetailVO> getUserDetail(@PathVariable Long id) {
        UserDetailVO result = userService.getUserDetail(id);
        return Result.success(result);
    }

    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 状态值
     * @return 操作结果提示
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态")
    @PreAuthorize("hasAuthority('user:normal:edit')")
    @Log(module = "用户管理", operation = "更新用户状态")
    public Result<String> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success("状态更新成功");
    }

    /**
     * 批量更新用户状态
     *
     * @param userIds 用户ID列表
     * @param status 状态值
     * @return 操作结果提示
     */
    @PutMapping("/batch/status")
    @Operation(summary = "批量更新用户状态")
    @PreAuthorize("hasAuthority('user:normal:edit')")
    @Log(module = "用户管理", operation = "批量更新用户状态")
    public Result<String> batchUpdateUserStatus(@RequestBody List<Long> userIds, @RequestParam Integer status) {
        userService.batchUpdateUserStatus(userIds, status);
        return Result.success("批量状态更新成功");
    }

    /**
     * 删除用户 (支持批量)
     *
     * @param ids 用户ID，多个用逗号分隔
     * @return 操作结果提示
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除用户")
    @Log(module = "用户管理", operation = "删除用户")
    @PreAuthorize("hasAuthority('user:normal:delete')")
    public Result<String> deleteUsers(@PathVariable String ids) {
        userService.deleteUsers(ids);
        return Result.success("删除成功");
    }

    /**
     * 获取用户统计信息
     *
     * @return UserStatisticsVO 封装好的用户统计信息对象
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('user:normal:view')")
    @Operation(summary = "获取用户统计信息")
    @Log(module = "用户管理", operation = "获取用户统计")
    public Result<UserStatisticsVO> getUserStatistics() {
        UserStatisticsVO result = userService.getUserStatistics();
        return Result.success(result);
    }

    /**
     * 导出用户数据接口
     * 注意：此接口返回类型为 void，因为文件直接写入 HttpServletResponse
     *
     * @param userQueryDTO 查询条件
     * @param response HttpServletResponse对象
     */
    @GetMapping("/export")
    @Operation(summary = "导出用户数据")
    @Log(module = "用户管理", operation = "导出用户数据")
    @PreAuthorize("hasAuthority('user:normal:view')")
    public void exportUsers(UserQueryDTO userQueryDTO, HttpServletResponse response) {
        userService.exportUsers(userQueryDTO, response);
    }

    /**
     * 获取用户增长趋势接口
     *
     * @param startTime 查询开始时间
     * @param endTime 查询结束时间
     * @return 用户增长趋势列表
     */
    @GetMapping("/growth-trend")
    @Operation(summary = "获取用户增长趋势")
    @Log(module = "用户管理", operation = "获取用户增长趋势")
    @PreAuthorize("hasAuthority('user:normal:view')")
    public Result<List<UserGrowthTrendVO>> getUserGrowthTrend(
            @RequestParam LocalDateTime startTime,
            @RequestParam LocalDateTime endTime) {
        List<UserGrowthTrendVO> result = userService.getUserGrowthTrend(startTime, endTime);
        return Result.success(result);
    }

    /**
     * 更新普通用户信息
     * 返回 void，ResultAdvice 会自动封装为 Result.success()
     *
     * @param id 用户ID
     * @param user 用户信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新普通用户信息")
    @Log(module = "用户管理", operation = "更新用户信息")
    @PreAuthorize("hasAuthority('user:normal:edit')")
    public void updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateUserInfo(user);
    }

    /**
     * 根据ID获取用户基本信息（用于编辑表单）
     * 直接返回 User 对象，ResultAdvice 会自动封装为 Result.success(user)
     *
     * @param id 用户ID
     * @return User实体（不含敏感信息）
     */
    @GetMapping("/{id}/basic")
    @Operation(summary = "获取用户基本信息")
    @PreAuthorize("hasAuthority('user:normal:view')")
    @Log(module = "用户管理", operation = "获取用户基本信息")
    public User getUserBasic(@PathVariable Long id) {
        return userService.getUserBasicInfo(id);
    }
}
