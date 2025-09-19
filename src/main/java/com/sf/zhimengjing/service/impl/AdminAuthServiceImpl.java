package com.sf.zhimengjing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.zhimengjing.common.constant.SystemConstants;
import com.sf.zhimengjing.common.enumerate.ResultEnum;
import com.sf.zhimengjing.common.exception.*;
import com.sf.zhimengjing.common.model.dto.AdminChangePasswordDTO;
import com.sf.zhimengjing.common.model.dto.AdminLoginDTO;
import com.sf.zhimengjing.common.model.vo.AdminInfoVO;
import com.sf.zhimengjing.common.model.vo.AdminLoginVO;
import com.sf.zhimengjing.common.util.IpUtils;
import com.sf.zhimengjing.common.util.JwtUtils;
import com.sf.zhimengjing.entity.AdminLoginLog;
import com.sf.zhimengjing.entity.AdminRole;
import com.sf.zhimengjing.entity.AdminUser;
import com.sf.zhimengjing.mapper.AdminLoginLogMapper;
import com.sf.zhimengjing.mapper.AdminRoleMapper;
import com.sf.zhimengjing.mapper.AdminUserMapper;
import com.sf.zhimengjing.service.AdminAuthService;
import com.sf.zhimengjing.service.CaptchaService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Title: AdminAuthServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.impl
 * @description: 管理员认证服务实现类
 *               提供登录、获取管理员信息、注销、修改密码和重置密码等功能。
 *               同时记录登录日志到数据库，并将用户信息缓存到 Redis。
 */
@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final AdminRoleMapper adminRoleMapper;
    private final AdminLoginLogMapper adminLoginLogMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;      // 缓存管理员用户信息
    private final StringRedisTemplate stringRedisTemplate;          // 验证码缓存
    private final ObjectMapper objectMapper;
    private final JwtUtils jwtUtils;
    private final CaptchaService captchaService;

    /**
     * 管理员登录
     *
     * @param loginDTO 登录信息，包括用户名、密码、验证码及验证码Key
     * @param request  HttpServletRequest对象，用于获取IP和User-Agent
     * @return AdminLoginVO 返回JWT令牌
     */
    @Override
    public AdminLoginVO login(AdminLoginDTO loginDTO, HttpServletRequest request) {
        AdminUser adminUser = null;
        String ip = IpUtils.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        try {
            // 验证码校验
            String captchaId = loginDTO.getCaptchaKey();
            String userCaptcha = loginDTO.getCaptcha();
            String redisKey = "login:captcha:" + captchaId;
            String cacheCaptcha = stringRedisTemplate.opsForValue().get(redisKey);

            if (cacheCaptcha == null || !cacheCaptcha.equalsIgnoreCase(userCaptcha)) {
                throw new CaptchaErrorException(ResultEnum.CAPTCHA_ERROR);
            }
            stringRedisTemplate.delete(redisKey);

            // 用户信息校验
            adminUser = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                    .eq(AdminUser::getUsername, loginDTO.getUsername()));
            if (Objects.isNull(adminUser)) throw new AccountNotFoundException(ResultEnum.USER_NOT_FOUND);
            if (!passwordEncoder.matches(loginDTO.getPassword(), adminUser.getPassword())) {
                throw new PasswordErrorException(ResultEnum.PASSWORD_ERROR);
            }
            if (adminUser.getStatus() != 1) throw new AccountForbiddenException(ResultEnum.USER_DISABLED);

            // 生成JWT并缓存用户信息
            String token = jwtUtils.generateToken(Map.of("adminId", adminUser.getId()), "admin");
            redisTemplate.opsForValue().set(SystemConstants.REDIS_ADMIN_USER_KEY + adminUser.getId(),
                    adminUser, jwtUtils.getExpiration(), TimeUnit.MILLISECONDS);

            // 更新最后登录信息
            adminUser.setLastLoginTime(LocalDateTime.now());
            adminUser.setLastLoginIp(ip);
            adminUserMapper.updateById(adminUser);

            // 记录登录成功日志
            recordLoginLog(adminUser.getId(), loginDTO.getUsername(), ip, userAgent, 1, "登录成功");

            return AdminLoginVO.builder().token(token).build();
        } catch (Exception e) {
            // 记录登录失败日志
            String errorMessage = e.getMessage();
            if (e instanceof CaptchaErrorException) errorMessage = ((CaptchaErrorException) e).getResultEnum().getMessage();
            else if (e instanceof AccountNotFoundException) errorMessage = ((AccountNotFoundException) e).getResultEnum().getMessage();
            else if (e instanceof PasswordErrorException) errorMessage = ((PasswordErrorException) e).getResultEnum().getMessage();
            else if (e instanceof AccountForbiddenException) errorMessage = ((AccountForbiddenException) e).getResultEnum().getMessage();

            Long adminId = (adminUser != null) ? adminUser.getId() : null;
            recordLoginLog(adminId, loginDTO.getUsername(), ip, userAgent, 0, errorMessage);
            throw e;
        }
    }

    /**
     * 获取管理员信息
     *
     * @param adminId 管理员ID
     * @return AdminInfoVO 管理员信息，包括角色和权限
     */
    @SneakyThrows
    @Override
    public AdminInfoVO getAdminInfo(Long adminId) {
        AdminUser adminUser = adminUserMapper.selectById(adminId);
        if (Objects.isNull(adminUser)) throw new AccountNotFoundException(ResultEnum.USER_NOT_FOUND);
        AdminRole adminRole = adminRoleMapper.selectById(adminUser.getRoleId());

        List<String> roles = Collections.singletonList(adminRole.getRoleCode());
        List<String> permissions = Collections.emptyList();

        if (StringUtils.hasText(adminRole.getPermissions())) {
            permissions = objectMapper.readValue(adminRole.getPermissions(), new TypeReference<List<String>>() {});
        }

        return AdminInfoVO.builder()
                .id(adminUser.getId())
                .username(adminUser.getUsername())
                .realName(adminUser.getRealName())
                .avatar(adminUser.getAvatar())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    /**
     * 注销管理员登录
     * 从Redis中删除缓存的管理员信息
     */
    @Override
    public void logout() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;

        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        try {
            Claims claims = jwtUtils.parseToken(token);
            Long adminId = claims.get("adminId", Long.class);
            redisTemplate.delete(SystemConstants.REDIS_ADMIN_USER_KEY + adminId);
        } catch (Exception e) {
            // Token无效或已过期，无需处理
        }
    }

    /**
     * 修改管理员密码
     *
     * @param adminId     管理员ID
     * @param passwordDTO 修改密码DTO，包括旧密码、新密码和确认密码
     */
    @Override
    public void changePassword(Long adminId, AdminChangePasswordDTO passwordDTO) {
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmNewPassword())) {
            throw new GeneralBusinessException("两次输入的新密码不一致");
        }

        AdminUser adminUser = adminUserMapper.selectById(adminId);
        if (Objects.isNull(adminUser)) throw new AccountNotFoundException(ResultEnum.USER_NOT_FOUND);
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), adminUser.getPassword())) {
            throw new PasswordErrorException(ResultEnum.PASSWORD_ERROR);
        }

        adminUser.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        adminUserMapper.updateById(adminUser);

        // 修改密码后强制下线
        redisTemplate.delete(SystemConstants.REDIS_ADMIN_USER_KEY + adminId);
    }

    /**
     * 重置管理员密码
     *
     * @param targetAdminId 目标管理员ID
     * @param currentAdminId 当前操作管理员ID
     * @return 新生成的密码
     */
    @Override
    public String resetPassword(Long targetAdminId, Long currentAdminId) {
        AdminUser targetUser = adminUserMapper.selectById(targetAdminId);
        if (Objects.isNull(targetUser)) throw new AccountNotFoundException(ResultEnum.USER_NOT_FOUND);

        String newPassword = UUID.randomUUID().toString().substring(0, 8);
        targetUser.setPassword(passwordEncoder.encode(newPassword));
        adminUserMapper.updateById(targetUser);

        redisTemplate.delete(SystemConstants.REDIS_ADMIN_USER_KEY + targetAdminId);
        return newPassword;
    }

    /**
     * 记录管理员登录日志
     *
     * @param adminId   管理员ID
     * @param username  登录用户名
     * @param ip        登录IP
     * @param userAgent 用户代理信息
     * @param status    登录状态（0:失败，1:成功）
     * @param message   失败原因或成功信息
     */
    private void recordLoginLog(Long adminId, String username, String ip, String userAgent, int status, String message) {
        AdminLoginLog log = new AdminLoginLog();
        log.setAdminId(adminId);
        log.setUsername(username);
        log.setLoginIp(ip);
        log.setUserAgent(userAgent);
        log.setStatus(status);
        log.setFailReason(status == 0 ? message : null);
        adminLoginLogMapper.insert(log);
    }
}
