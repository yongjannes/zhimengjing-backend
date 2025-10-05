package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.enumerate.ResultEnum;
import com.sf.zhimengjing.common.exception.AccountNotFoundException;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.exception.PasswordErrorException;
import com.sf.zhimengjing.common.model.dto.AdminChangePasswordDTO;
import com.sf.zhimengjing.common.model.dto.AdminUpdateInfoDTO;
import com.sf.zhimengjing.common.model.vo.AdminLoginLogVO;
import com.sf.zhimengjing.common.model.vo.AdminProfileVO;
import com.sf.zhimengjing.entity.admin.AdminLoginLog;
import com.sf.zhimengjing.entity.admin.AdminRole;
import com.sf.zhimengjing.entity.admin.AdminUser;
import com.sf.zhimengjing.mapper.admin.AdminLoginLogMapper;
import com.sf.zhimengjing.mapper.admin.AdminRoleMapper;
import com.sf.zhimengjing.mapper.admin.AdminUserMapper;
import com.sf.zhimengjing.service.admin.AdminProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Title: AdminProfileServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @description: 管理员个人中心服务实现类
 */
@Service
@RequiredArgsConstructor
public class AdminProfileServiceImpl implements AdminProfileService {

    private final AdminUserMapper adminUserMapper;
    private final AdminRoleMapper adminRoleMapper;
    private final AdminLoginLogMapper adminLoginLogMapper;
    private final PasswordEncoder passwordEncoder;

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
}