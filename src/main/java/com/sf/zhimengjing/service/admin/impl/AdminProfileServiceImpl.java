package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.enumerate.EmailTemplateEnum;
import com.sf.zhimengjing.common.enumerate.ResultEnum;
import com.sf.zhimengjing.common.exception.AccountNotFoundException;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.exception.PasswordErrorException;
import com.sf.zhimengjing.common.model.dto.AdminChangePasswordDTO;
import com.sf.zhimengjing.common.model.dto.AdminUpdateInfoDTO;
import com.sf.zhimengjing.common.model.vo.AdminLoginLogVO;
import com.sf.zhimengjing.common.model.vo.AdminProfileVO;
import com.sf.zhimengjing.common.util.EmailApi;
import com.sf.zhimengjing.entity.admin.AdminLoginLog;
import com.sf.zhimengjing.entity.admin.AdminRole;
import com.sf.zhimengjing.entity.admin.AdminUser;
import com.sf.zhimengjing.mapper.admin.AdminLoginLogMapper;
import com.sf.zhimengjing.mapper.admin.AdminRoleMapper;
import com.sf.zhimengjing.mapper.admin.AdminUserMapper;
import com.sf.zhimengjing.service.admin.AdminProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Title: AdminProfileServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @description: 管理员个人中心服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminProfileServiceImpl implements AdminProfileService {

    private final AdminUserMapper adminUserMapper;
    private final AdminRoleMapper adminRoleMapper;
    private final AdminLoginLogMapper adminLoginLogMapper;
    private final PasswordEncoder passwordEncoder;

    private final EmailApi emailApi;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public AdminProfileVO getProfile(Long adminId) {
        AdminUser adminUser = adminUserMapper.selectById(adminId);
        if (Objects.isNull(adminUser)) {
            throw new AccountNotFoundException(ResultEnum.USER_NOT_FOUND);
        }

        AdminRole adminRole = adminRoleMapper.selectById(adminUser.getRoleId());

        return AdminProfileVO.builder()
                .id(adminUser.getId())
                .username(adminUser.getUsername())
                .realName(adminUser.getRealName())
                .email(adminUser.getEmail())
                .phone(adminUser.getPhone())
                .avatar(adminUser.getAvatar())
                .roleId(adminUser.getRoleId())
                .roleName(adminRole != null ? adminRole.getRoleName() : null)
                .roleCode(adminRole != null ? adminRole.getRoleCode() : null)
                .status(adminUser.getStatus())
                .lastLoginTime(adminUser.getLastLoginTime())
                .lastLoginIp(adminUser.getLastLoginIp())
                .createTime(adminUser.getCreateTime())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long adminId, AdminUpdateInfoDTO updateDTO) {
        AdminUser adminUser = adminUserMapper.selectById(adminId);
        if (Objects.isNull(adminUser)) {
            throw new AccountNotFoundException(ResultEnum.USER_NOT_FOUND);
        }

        // 更新信息
        if (updateDTO.getRealName() != null) {
            adminUser.setRealName(updateDTO.getRealName());
        }
        if (updateDTO.getEmail() != null) {
            adminUser.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPhone() != null) {
            adminUser.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getAvatar() != null) {
            adminUser.setAvatar(updateDTO.getAvatar());
        }

        adminUser.setUpdateBy(adminId);
        adminUserMapper.updateById(adminUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long adminId, AdminChangePasswordDTO passwordDTO) {
        // 验证两次新密码是否一致
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmNewPassword())) {
            throw new GeneralBusinessException("两次输入的新密码不一致");
        }

        AdminUser adminUser = adminUserMapper.selectById(adminId);
        if (Objects.isNull(adminUser)) {
            throw new AccountNotFoundException(ResultEnum.USER_NOT_FOUND);
        }

        // 验证旧密码
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), adminUser.getPassword())) {
            throw new PasswordErrorException(ResultEnum.PASSWORD_ERROR);
        }

        // 更新密码
        adminUser.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        adminUser.setUpdateBy(adminId);
        adminUserMapper.updateById(adminUser);
    }

    @Override
    public Page<AdminLoginLogVO> getLoginLogs(Long adminId, Integer pageNum, Integer pageSize) {
        Page<AdminLoginLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AdminLoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminLoginLog::getAdminId, adminId)
                .orderByDesc(AdminLoginLog::getLoginTime);

        Page<AdminLoginLog> logPage = adminLoginLogMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<AdminLoginLogVO> voPage = new Page<>();
        BeanUtils.copyProperties(logPage, voPage, "records");
        voPage.setRecords(logPage.getRecords().stream().map(log -> {
            AdminLoginLogVO vo = new AdminLoginLogVO();
            BeanUtils.copyProperties(log, vo);
            return vo;
        }).collect(Collectors.toList()));

        return voPage;
    }

    @Override
    public void sendChangeEmailCode(Long adminId, String newEmail) {
        // 1. 检查新邮箱是否已被使用
        AdminUser existUser = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getEmail, newEmail)
                .ne(AdminUser::getId, adminId));

        if (Objects.nonNull(existUser)) {
            throw new GeneralBusinessException("该邮箱已被其他用户使用");
        }

        // 2. 检查发送频率
        String hashKey = "change:email:captcha:" + newEmail;
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(hashKey);

        String lastSendTimestamp = hashOps.get("lastSendTimestamp");
        String sendCount = hashOps.get("sendCount");

        // 判断发送次数是否超过限制
        if (StringUtils.isNotBlank(sendCount) && Integer.parseInt(sendCount) >= 5) {
            hashOps.expire(24, TimeUnit.HOURS);
            throw new GeneralBusinessException("发送次数过多，请24小时后再试");
        }

        // 判断发送频率是否过高
        if (StringUtils.isNotBlank(lastSendTimestamp)) {
            long lastSendTime = Long.parseLong(lastSendTimestamp);
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastSendTime;
            long interval = 60 * 1000; // 60秒
            if (elapsedTime < interval) {
                long remainingSeconds = (interval - elapsedTime) / 1000;
                throw new GeneralBusinessException("发送频繁，请" + remainingSeconds + "秒后再试");
            }
        }

        // 3. 更新发送次数
        int newSendCount = StringUtils.isNotBlank(sendCount) ? Integer.parseInt(sendCount) + 1 : 1;

        // 4. 生成新验证码（6位数字）
        String captcha = RandomStringUtils.randomNumeric(6);

        // 5. 发送邮件
        emailApi.sendHtmlEmailAsync(
                EmailTemplateEnum.FORGOT_PASSWORD_EMAIL_HTML.getSubject(),
                EmailTemplateEnum.FORGOT_PASSWORD_EMAIL_HTML.set(captcha),
                newEmail
        );


        // 6. 更新 Redis 中的信息
        hashOps.put("captcha", captcha);
        hashOps.put("adminId", String.valueOf(adminId));
        hashOps.put("lastSendTimestamp", String.valueOf(System.currentTimeMillis()));
        hashOps.put("sendCount", String.valueOf(newSendCount));
        hashOps.expire(5, TimeUnit.MINUTES); // 验证码5分钟有效

        log.info("修改邮箱验证码已发送到: {}", newEmail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeEmail(Long adminId, String newEmail, String captcha) {

        // 1. 验证码校验
        String hashKey = "change:email:captcha:" + newEmail;
        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(hashKey);
        String cachedCaptcha = hashOps.get("captcha");
        String cachedAdminId = hashOps.get("adminId");

        if (StringUtils.isBlank(cachedCaptcha)) {
            throw new GeneralBusinessException("验证码已过期或不存在");
        }

        if (!cachedCaptcha.equals(captcha)) {
            throw new GeneralBusinessException("验证码错误");
        }

        if (!String.valueOf(adminId).equals(cachedAdminId)) {
            throw new GeneralBusinessException("验证码不匹配");
        }

        // 2. 再次检查新邮箱是否已被使用
        AdminUser existUser = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getEmail, newEmail)
                .ne(AdminUser::getId, adminId));

        if (Objects.nonNull(existUser)) {
            throw new GeneralBusinessException("该邮箱已被其他用户使用");
        }

        // 3. 查找当前用户
        AdminUser adminUser = adminUserMapper.selectById(adminId);
        if (Objects.isNull(adminUser)) {
            throw new GeneralBusinessException("用户不存在");
        }

        // 4. 更新邮箱
        adminUser.setEmail(newEmail);
        adminUser.setUpdateTime(LocalDateTime.now());
        int updateCount = adminUserMapper.updateById(adminUser);

        if (updateCount <= 0) {
            throw new GeneralBusinessException("邮箱修改失败");
        }

        // 5. 删除验证码
        stringRedisTemplate.delete(hashKey);

        log.info("管理员邮箱已修改: id={}, newEmail={}", adminId, newEmail);
    }
}