package com.sf.zhimengjing.common.filter;

import com.sf.zhimengjing.common.constant.SystemConstants;
import com.sf.zhimengjing.common.util.JwtUtils;
import com.sf.zhimengjing.entity.admin.AdminUser;
import com.sf.zhimengjing.entity.User;
import com.sf.zhimengjing.mapper.admin.AdminUserMapper;
import com.sf.zhimengjing.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @Title: JwtAuthenticationTokenFilter
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.filter
 * @Description: JWT 认证过滤器，负责解析请求头中的 Token，
 *               校验用户身份并将认证信息写入 SecurityContext。
 *               同时在 Token 接近过期时自动刷新。
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final AdminUserMapper adminUserMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.refresh-threshold}")
    private long refreshThreshold;

    // 👇 修改构造函数以接收 AdminUserMapper
    public JwtAuthenticationTokenFilter(JwtUtils jwtUtils, UserMapper userMapper, AdminUserMapper adminUserMapper, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
        this.adminUserMapper = adminUserMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (StringUtils.isBlank(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = StringUtils.removeStart(authorizationHeader, "Bearer ");

        try {
            Claims claims = jwtUtils.parseToken(token);
            if (Objects.isNull(claims)) {
                log.warn("[JWT Filter] Token 解析失败。");
                filterChain.doFilter(request, response);
                return;
            }

            // 👇 检查当前认证上下文是否为空
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // 👇 通过 Token 的 subject 判断用户类型
                String subject = claims.getSubject();
                if ("admin".equals(subject)) {
                    handleAdminAuthentication(claims);
                } else {
                    handleUserAuthentication(claims, response);
                }
            }
        } catch (Exception e) {
            log.error("[JWT Filter] Token 处理异常", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 处理后台管理员认证
     */
    private void handleAdminAuthentication(Claims claims) {
        Long adminId = claims.get("adminId", Long.class);
        if (adminId != null) {
            if (Boolean.FALSE.equals(redisTemplate.hasKey(SystemConstants.REDIS_ADMIN_USER_KEY + adminId))) {
                log.warn("[JWT Filter] 管理员缓存不存在，可能已登出: id={}", adminId);
                return;
            }
            AdminUser adminUser = adminUserMapper.selectById(adminId);
            if (adminUser != null && adminUser.getStatus() == 1) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(adminId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.info("[JWT Filter] 管理员认证成功: id={}, username={}", adminUser.getId(), adminUser.getUsername());
            } else {
                log.warn("[JWT Filter] 管理员不存在或被禁用: id={}", adminId);
            }
    }

}

    /**
     * 处理C端用户认证并刷新Token
     */
    private void handleUserAuthentication(Claims claims, HttpServletResponse response) {
        Long userId = claims.get("userId", Long.class);
        if (userId != null) {
            User user = userMapper.selectById(userId);
            if (user != null && user.getStatus()) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.info("[JWT Filter] 用户认证成功: id={}, role={}", user.getId(), user.getUserRole());
                checkAndRefreshToken(response, claims, user);
            } else {
                log.warn("[JWT Filter] 用户不存在或被禁用: id={}", userId);
            }
        }
    }


    /**
     * 检查并刷新C端用户的Token (管理员Token无需刷新)
     */
    private void checkAndRefreshToken(HttpServletResponse response, Claims claims, User user) {
        Date expiration = claims.getExpiration();
        long remainingTimeMillis = expiration.getTime() - System.currentTimeMillis();

        if (remainingTimeMillis < refreshThreshold) {
            String newToken = jwtUtils.generateToken(
                    Map.of("userId", user.getId(), "userRole", user.getUserRole()),
                    "user"
            );

            response.setHeader("Authorization", "Bearer " + newToken);
            log.info("[JWT Filter] Token 已刷新，用户 id={}", user.getId());
        }
    }
}