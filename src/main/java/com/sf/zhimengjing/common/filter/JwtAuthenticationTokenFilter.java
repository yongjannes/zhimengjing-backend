package com.sf.zhimengjing.common.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.zhimengjing.common.constant.SystemConstants;
import com.sf.zhimengjing.common.util.JwtUtils;
import com.sf.zhimengjing.entity.User;
import com.sf.zhimengjing.entity.admin.AdminRole;
import com.sf.zhimengjing.entity.admin.AdminUser;
import com.sf.zhimengjing.mapper.UserMapper;
import com.sf.zhimengjing.mapper.admin.AdminRoleMapper;
import com.sf.zhimengjing.mapper.admin.AdminUserMapper;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Title: JwtAuthenticationTokenFilter
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.filter
 * @Description: JWT 认证过滤器，负责解析请求头中的 Token，
 * 校验用户身份并将认证信息写入 SecurityContext。
 * 同时在 Token 接近过期时自动刷新。
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final AdminUserMapper adminUserMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private final AdminRoleMapper adminRoleMapper;
    private final ObjectMapper objectMapper;


    @Value("${jwt.refresh-threshold}")
    private long refreshThreshold;

    public JwtAuthenticationTokenFilter(JwtUtils jwtUtils, UserMapper userMapper, AdminUserMapper adminUserMapper, RedisTemplate<String, Object> redisTemplate, AdminRoleMapper adminRoleMapper, ObjectMapper objectMapper) {
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
        this.adminUserMapper = adminUserMapper;
        this.redisTemplate = redisTemplate;
        this.adminRoleMapper = adminRoleMapper;
        this.objectMapper = objectMapper;
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

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                String subject = claims.getSubject();
                if ("admin".equals(subject)) {
                    handleAdminAuthentication(token, claims, response);
                } else {
                    handleUserAuthentication(token,claims, response);
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
    private void handleAdminAuthentication(String token,Claims claims, HttpServletResponse response) {
        Long adminId = claims.get("adminId", Long.class);
        if (adminId != null) {
            if (Boolean.FALSE.equals(redisTemplate.hasKey(SystemConstants.REDIS_ADMIN_USER_KEY + adminId))) {
                log.warn("[JWT Filter] 管理员缓存不存在，可能已登出: id={}", adminId);
                return;
            }
            AdminUser adminUser = adminUserMapper.selectById(adminId);
            if (adminUser != null && adminUser.getStatus() == 1) {
                List<GrantedAuthority> authorities = loadAdminAuthorities(adminUser);


                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(adminId, null,authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.info("[JWT Filter] 管理员认证成功: id={}, username={}", adminUser.getId(), adminUser.getUsername());
                tryToRefreshToken(token, claims, response);
            } else {
                log.warn("[JWT Filter] 管理员不存在或被禁用: id={}", adminId);
            }
        }
    }

    /**
     * 处理C端用户认证并刷新Token
     */
    private void handleUserAuthentication(String token,Claims claims, HttpServletResponse response) {
        Long userId = claims.get("userId", Long.class);
        if (userId != null) {
            User user = userMapper.selectById(userId);
            // 【已修正】: 检查用户状态是否为1（正常）
            if (user != null && user.getStatus() == 1) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.info("[JWT Filter] 用户认证成功: id={}", user.getId());
                tryToRefreshToken(token, claims, response);
            } else {
                log.warn("[JWT Filter] 用户不存在或被禁用: id={}", userId);
            }
        }
    }


    private void tryToRefreshToken(String token, Claims claims, HttpServletResponse response) {
        long expirationTime = claims.getExpiration().getTime();
        long currentTime = System.currentTimeMillis();

        // 判断剩余有效期是否小于刷新阈值
        if ((expirationTime - currentTime) < refreshThreshold) {
            log.info("[JWT Filter] Token 即将过期，准备刷新...");
            // 调用工具类中的 refreshToken 方法生成新令牌
            String newToken = jwtUtils.refreshToken(token);
            // 将新令牌设置在响应头中
            response.setHeader("Authorization", "Bearer " + newToken);
            // 暴露自定义的响应头 "Authorization"，以便前端JS可以访问
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
            log.info("[JWT Filter] Token 刷新成功！");
        }
    }

    private List<GrantedAuthority> loadAdminAuthorities(AdminUser adminUser) {
        try {
            // 1. 根据角色ID查询角色
            AdminRole adminRole = adminRoleMapper.selectById(adminUser.getRoleId());
            if (adminRole == null) {
                log.warn("[JWT Filter] 管理员角色不存在: userId={}, roleId={}",
                        adminUser.getId(), adminUser.getRoleId());
                return Collections.emptyList();
            }

            List<GrantedAuthority> authorities;

            // 2. 解析权限JSON字符串
            if (StringUtils.isNotBlank(adminRole.getPermissions())) {
                List<String> permissions = objectMapper.readValue(
                        adminRole.getPermissions(),
                        new TypeReference<List<String>>() {}
                );
                authorities = permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                log.debug("[JWT Filter] 加载管理员权限成功: userId={}, roleId={}, permissions={}",
                        adminUser.getId(), adminUser.getRoleId(), permissions);
            } else {
                authorities = new java.util.ArrayList<>();
                log.warn("[JWT Filter] 管理员角色无任何权限配置: userId={}, roleId={}",
                        adminUser.getId(), adminUser.getRoleId());
            }

            // 3. 添加角色作为权限，前缀为 "ROLE_"
            if (StringUtils.isNotBlank(adminRole.getRoleCode())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + adminRole.getRoleCode()));
            }

            return authorities;
        } catch (Exception e) {
            log.error("[JWT Filter] 加载管理员权限失败: userId={}", adminUser.getId(), e);
            return Collections.emptyList();
        }
    }
}