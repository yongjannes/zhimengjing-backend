package com.sf.zhimengjing.common.filter;

import com.sf.zhimengjing.common.util.JwtUtils;
import com.sf.zhimengjing.entity.User;
import com.sf.zhimengjing.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.refresh-threshold}")
    private long refreshThreshold;

    public JwtAuthenticationTokenFilter(JwtUtils jwtUtils, UserMapper userMapper) {
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
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

            Long userId = claims.get("userId", Long.class);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
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
        } catch (Exception e) {
            log.error("[JWT Filter] Token 处理异常", e);
        }

        filterChain.doFilter(request, response);
    }

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