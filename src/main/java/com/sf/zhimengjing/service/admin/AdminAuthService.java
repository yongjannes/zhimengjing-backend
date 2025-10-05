package com.sf.zhimengjing.service.admin;

import com.sf.zhimengjing.common.model.dto.AdminChangePasswordDTO;
import com.sf.zhimengjing.common.model.dto.AdminLoginDTO;
import com.sf.zhimengjing.common.model.vo.AdminInfoVO;
import com.sf.zhimengjing.common.model.vo.AdminLoginVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @Title: AdminAuthService
 * @Author 殇枫 & Gemini
 * @Package com.sf.zhimengjing.service
 * @description: 管理员认证相关服务接口
 */
public interface AdminAuthService {

    /**
     * 管理员登录
     */
    AdminLoginVO login(AdminLoginDTO loginDTO, HttpServletRequest request);

    /**
     * 管理员登出
     */
    void logout();

    /**
     * 获取管理员信息
     */
    AdminInfoVO getAdminInfo(Long adminId);

    /**
     * 修改密码
     */
    void changePassword(Long adminId, AdminChangePasswordDTO passwordDTO);

    /**
     * 重置密码
     */
    String resetPassword(Long targetAdminId, Long currentAdminId);

    /**
     * 发送忘记密码验证码
     *
     * @param identifier 用户名或邮箱
     */
    void sendForgotPasswordCode(String identifier);

    /**
     * 通过验证码重置密码
     *
     * @param email       邮箱
     * @param captcha     验证码
     * @param newPassword 新密码
     */
    void resetPasswordByCaptcha(String email, String captcha, String newPassword);
}