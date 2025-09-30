package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.AdminUserDTO;
import com.sf.zhimengjing.common.model.vo.AdminUserVO;
import com.sf.zhimengjing.entity.admin.AdminUser;
import com.sf.zhimengjing.mapper.admin.AdminUserMapper;
import com.sf.zhimengjing.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @Title: AdminUserServiceImpl
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.admin.impl
 * @description: 后台管理员业务实现类，包含管理员分页查询、创建、更新及逻辑删除功能
 */
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询后台管理员
     *
     * @param pageNum  当前页码
     * @param pageSize 每页记录数
     * @return IPage<AdminUserVO> 分页结果
     */
    @Override
    public IPage<AdminUserVO> pageAdminUsers(int pageNum, int pageSize, String username, String realName,Integer status) {
        Page<AdminUser> page = new Page<>(pageNum, pageSize);


        LambdaQueryWrapper<AdminUser> queryWrapper = new LambdaQueryWrapper<AdminUser>()
                .like(StringUtils.isNotBlank(username), AdminUser::getUsername, username)
                .like(StringUtils.isNotBlank(realName), AdminUser::getRealName, realName)
                .eq(status != null, AdminUser::getStatus, status)
                .orderByAsc(AdminUser::getId);

        return adminUserMapper.selectPage(page, queryWrapper).convert(user -> {
            AdminUserVO vo = new AdminUserVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName());
            vo.setRoleId(user.getRoleId());
            vo.setStatus(user.getStatus());
            vo.setLastLoginTime(user.getLastLoginTime());
            vo.setCreateTime(user.getCreateTime());
            vo.setUpdateTime(user.getUpdateTime());
            return vo;
        });
    }

    /**
     * 创建后台管理员
     *
     * @param adminUserDTO 管理员数据传输对象
     * @param creatorId    操作人ID
     */
    @Override
    public void createAdminUser(AdminUserDTO adminUserDTO, Long creatorId) {
        if (StringUtils.isBlank(adminUserDTO.getPassword())) {
            throw new GeneralBusinessException("创建管理员时必须设置密码");
        }
        if (adminUserMapper.selectCount(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, adminUserDTO.getUsername())) > 0) {
            throw new GeneralBusinessException("用户名已存在");
        }

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(adminUserDTO.getUsername());
        adminUser.setPassword(passwordEncoder.encode(adminUserDTO.getPassword()));
        adminUser.setRealName(adminUserDTO.getRealName());
        adminUser.setRoleId(adminUserDTO.getRoleId());
        adminUser.setStatus(adminUserDTO.getStatus());
        adminUser.setEmail(adminUserDTO.getEmail());
        adminUser.setPhone(adminUserDTO.getPhone());
        adminUser.setAvatar(adminUserDTO.getAvatar());
        adminUser.setCreateBy(creatorId);
        adminUser.setUpdateBy(creatorId);

        adminUserMapper.insert(adminUser);
    }

    /**
     * 更新后台管理员
     *
     * @param id           管理员ID
     * @param adminUserDTO 管理员更新数据
     * @param updaterId    操作人ID
     */
    @Override
    public void updateAdminUser(Long id, AdminUserDTO adminUserDTO, Long updaterId) {
        AdminUser adminUser = adminUserMapper.selectById(id);
        if (adminUser == null) {
            throw new GeneralBusinessException("管理员不存在");
        }
        if (!adminUser.getUsername().equals(adminUserDTO.getUsername()) &&
                adminUserMapper.selectCount(new LambdaQueryWrapper<AdminUser>()
                        .eq(AdminUser::getUsername, adminUserDTO.getUsername())) > 0) {
            throw new GeneralBusinessException("用户名已存在");
        }

        adminUser.setUsername(adminUserDTO.getUsername());
        adminUser.setRealName(adminUserDTO.getRealName());
        adminUser.setRoleId(adminUserDTO.getRoleId());
        adminUser.setStatus(adminUserDTO.getStatus());
        adminUser.setEmail(adminUserDTO.getEmail());
        adminUser.setPhone(adminUserDTO.getPhone());
        adminUser.setAvatar(adminUserDTO.getAvatar());
        adminUser.setUpdateBy(updaterId);

        if (StringUtils.isNotBlank(adminUserDTO.getPassword())) {
            adminUser.setPassword(passwordEncoder.encode(adminUserDTO.getPassword()));
        }

        adminUserMapper.updateById(adminUser);
    }

    /**
     * 删除后台管理员 (逻辑删除)
     * 将用户状态设置为0 (禁用)，并记录操作人ID。
     *
     * @param id         被删除管理员ID
     * @param operatorId 操作人ID
     */
    @Override
    public void deleteAdminUser(Long id, Long operatorId) {
        if (id.equals(operatorId)) {
            throw new GeneralBusinessException("不允许删除自己");
        }
        if (id == 1L) {
            throw new GeneralBusinessException("不允许删除超级管理员");
        }

        AdminUser adminUser = adminUserMapper.selectById(id);
        if (adminUser == null) {
            throw new GeneralBusinessException("管理员不存在");
        }

        int result = adminUserMapper.deleteById(id);
        if (result == 0) {
            throw new GeneralBusinessException("管理员不存在，删除失败");
        }

    }

    /**
     * 根据ID获取管理员用户详情的实现
     * @param id 用户ID
     * @return 用户详情VO
     */
    @Override
    public AdminUserVO getAdminUserById(Long id) {
        AdminUser adminUser = adminUserMapper.selectById(id);
        if (adminUser == null) {
            // 您可以根据需要抛出一个用户不存在的异常
            return null;
        }
        AdminUserVO adminUserVO = new AdminUserVO();
        BeanUtils.copyProperties(adminUser, adminUserVO);
        // 如果需要，这里还可以填充角色等其他信息
        return adminUserVO;
    }
}
