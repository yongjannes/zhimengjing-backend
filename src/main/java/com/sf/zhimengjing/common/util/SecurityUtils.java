package com.sf.zhimengjing.common.util;

import com.sf.zhimengjing.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @Title: SecurityUtils
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.util
 * @description: 安全工具类，用于获取当前登录用户信息
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户的ID
     * @return 当前登录用户的ID，如果未认证或无法识别用户类型则返回 null
     */
    public static Long getUserId() {
        // 1. 从 Spring Security 的上下文中获取当前认证对象 Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 检查用户是否已认证
        // 如果 authentication 为空、未认证或 principal 为空，说明没有登录用户，直接返回 null
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
            return null;
        }

        // 3. 获取身份主体（Principal），它代表当前登录用户
        Object principal = authentication.getPrincipal();

        // 4. 根据 Principal 的类型返回对应用户ID
        // - 如果是 C 端用户，Principal 是 User 对象，直接返回 User 的 id
        if (principal instanceof User) {
            return ((User) principal).getId();
        }

        // - 如果是后台管理员，Principal 是 Long 类型的 adminId，直接返回
        if (principal instanceof Long) {
            return (Long) principal;
        }

        // 5. 如果是匿名用户（Spring Security 默认匿名用户标识为 "anonymousUser"），返回 null
        if ("anonymousUser".equals(principal.toString())) {
            return null;
        }

        // 6. 如果无法识别用户类型，为了安全，返回 null
        // 在正常项目中，这种情况不应该出现
        return null;
    }
}
