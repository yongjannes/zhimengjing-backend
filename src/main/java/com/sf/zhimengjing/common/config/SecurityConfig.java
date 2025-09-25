package com.sf.zhimengjing.common.config;

import cn.hutool.json.JSONUtil;
import com.sf.zhimengjing.common.enumerate.ResultEnum;
import com.sf.zhimengjing.common.filter.JwtAuthenticationTokenFilter;
import com.sf.zhimengjing.common.result.Result;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Title: SecurityConfig
 * @Author 殇枫
 * @Package com.sf.zhimengjing.common.config
 * @description: Spring Security 配置类，配置 JWT 认证、异常处理、权限控制等。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    /**
     * 提供一个空的 UserDetailsService，避免 Spring Security 自动生成默认用户
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> null; // 不返回任何用户
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 csrf
                .csrf(AbstractHttpConfigurer::disable)
                // 关闭 formLogin，防止跳转到默认登录页
                .formLogin(AbstractHttpConfigurer::disable)
                // 关闭 httpBasic 登录框
                .httpBasic(AbstractHttpConfigurer::disable)
                // 无状态 session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置请求权限
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 将 Knife4j/Swagger UI 的相关资源路径也设置为公开访问
                        .requestMatchers(
                                "/user/login",
                                "/captcha/graph-captcha/**",
                                "/",
                                "/doc.html",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/swagger-resources/**",
                                "/admin/auth/login",
                                "/api/dream-analysis/analyze/stream"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class) // 添加此行
                .exceptionHandling(exceptions -> {
                    // 配置认证失败处理器（未登录）
                    exceptions.authenticationEntryPoint((request, response, authException) -> {
                        response.setContentType("application/json;charset=utf-8");
                        response.setStatus(401); // 设置状态码为 401
                        response.getWriter().write(JSONUtil.toJsonStr(Result.error(ResultEnum.UNAUTHORIZED)));
                    });
                    // 配置授权失败处理器（无权限）
                    exceptions.accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setContentType("application/json;charset=utf-8");
                        response.setStatus(403); // 设置状态码为 403
                        response.getWriter().write(JSONUtil.toJsonStr(Result.error(ResultEnum.FORBIDDEN)));
                    });
                });
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}