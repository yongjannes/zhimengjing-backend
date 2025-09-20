package com.sf.zhimengjing.service.admin.impl;

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
import com.sf.zhimengjing.entity.admin.AdminLoginLog;
import com.sf.zhimengjing.entity.admin.AdminRole;
import com.sf.zhimengjing.entity.admin.AdminUser;
import com.sf.zhimengjing.mapper.admin.AdminLoginLogMapper;
import com.sf.zhimengjing.mapper.admin.AdminRoleMapper;
import com.sf.zhimengjing.mapper.admin.AdminUserMapper;
import com.sf.zhimengjing.service.admin.AdminAuthService;
import com.sf.zhimengjing.service.admin.CaptchaService;
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
 * @Package: com.sf.zhimengjing.service.admin.impl
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
    // 账户锁定时间（分钟）
    private static final long LOCK_TIME_MINUTES = 5;

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
        String lockKey = "";

        try {
            // 1. 验证码校验
            String captchaId = loginDTO.getCaptchaKey();
            String userCaptcha = loginDTO.getCaptcha();
            String redisKey = "login:captcha:" + captchaId;
            String cacheCaptcha = stringRedisTemplate.opsForValue().get(redisKey);

            if (cacheCaptcha == null || !cacheCaptcha.equalsIgnoreCase(userCaptcha)) {
                throw new CaptchaErrorException(ResultEnum.CAPTCHA_ERROR);
            }
            stringRedisTemplate.delete(redisKey);

            // 2. 用户信息校验
            adminUser = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                    .eq(AdminUser::getUsername, loginDTO.getUsername()));
            if (Objects.isNull(adminUser)) {
                throw new AccountNotFoundException(ResultEnum.USER_NOT_FOUND);
            }

            // 定义 Redis 锁定键
            lockKey = "admin:lock:" + adminUser.getId();

            // 检查账户是否被锁定
            if (adminUser.getStatus() == 2) {
                if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(lockKey))) {
                    throw new AccountForbiddenException(ResultEnum.USER_DISABLED);
                } else {
                    // 自动解锁
                    adminUser.setStatus(1);
                    adminUser.setLoginFailCount(0);
                    adminUserMapper.updateById(adminUser);
                }
            }

            // 3. 密码校验
            if (!passwordEncoder.matches(loginDTO.getPassword(), adminUser.getPassword())) {
                if (adminUser.getRoleId() != 1L) { // 排除超级管理员
                    Integer failCount = Optional.ofNullable(adminUser.getLoginFailCount()).orElse(0) + 1;
                    adminUser.setLoginFailCount(failCount);

                    if (failCount >= 3) {
                        adminUser.setStatus(2); // 锁定
                        stringRedisTemplate.opsForValue().set(lockKey, "locked", LOCK_TIME_MINUTES, TimeUnit.MINUTES);
                    }
                    adminUserMapper.updateById(adminUser);
                }
                    throw new PasswordErrorException(ResultEnum.PASSWORD_ERROR);
            }

            // 4. 用户状态校验
            if (adminUser.getStatus() == 0) {
                throw new AccountForbiddenException(ResultEnum.USER_DISABLED);
            }

            // 5. 生成 JWT 并缓存用户信息
            String token = jwtUtils.generateToken(Map.of("adminId", adminUser.getId()), "admin");
            redisTemplate.opsForValue().set(SystemConstants.REDIS_ADMIN_USER_KEY + adminUser.getId(),
                    adminUser, jwtUtils.getExpiration(), TimeUnit.MILLISECONDS);

            // 6. 登录成功后重置失败次数，并更新登录信息
            adminUser.setLoginFailCount(0);
            adminUser.setLastLoginTime(LocalDateTime.now());
            adminUser.setLastLoginIp(ip);
            adminUserMapper.updateById(adminUser);

            // 7. 记录登录成功日志
            recordLoginLog(adminUser.getId(), loginDTO.getUsername(), ip, userAgent, 1, "登录成功");

            return AdminLoginVO.builder().token(token).build();
        }  catch (Exception e) {
            String errorMessage = e.getMessage(); // 获取默认的异常消息
            Long adminId = (adminUser != null) ? adminUser.getId() : null;

            // 【关键修改】: 根据异常类型，构造并抛出携带详细信息的特定异常
            if (e instanceof PasswordErrorException) {
                errorMessage = "密码错误";
                if (adminUser != null && adminUser.getRoleId() != 1L) {
                    int remainingAttempts = 3 - Optional.ofNullable(adminUser.getLoginFailCount()).orElse(0);
                    if (remainingAttempts <= 0) {
                        errorMessage = String.format("您的账户已被锁定，请在 %d 分钟后重试。", 5);
                    } else {
                        errorMessage = String.format("密码错误，还剩 %d 次尝试机会。", remainingAttempts);
                    }
                }
                recordLoginLog(adminId, loginDTO.getUsername(), ip, userAgent, 0, errorMessage);
                // 抛出带有动态消息的 PasswordErrorException
                throw new PasswordErrorException(errorMessage);

            } else if (e instanceof AccountForbiddenException) {
                errorMessage = String.format("您的账户已被禁用或锁定，请在 %d 分钟后重试或联系管理员。", 5);
                recordLoginLog(adminId, loginDTO.getUsername(), ip, userAgent, 0, errorMessage);
                // 抛出带有动态消息的 AccountForbiddenException
                throw new AccountForbiddenException(errorMessage);

            } else {
                // 对于其他不需要动态消息的异常，直接记录日志并重新抛出
                recordLoginLog(adminId, loginDTO.getUsername(), ip, userAgent, 0, e.getMessage());
                throw e;
            }
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
