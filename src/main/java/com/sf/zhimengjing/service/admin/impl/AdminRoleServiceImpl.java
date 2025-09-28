package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.AdminRoleDTO;
import com.sf.zhimengjing.entity.admin.AdminRole;
import com.sf.zhimengjing.entity.admin.AdminUser;
import com.sf.zhimengjing.mapper.admin.AdminRoleMapper;
import com.sf.zhimengjing.mapper.admin.AdminUserMapper;
import com.sf.zhimengjing.service.admin.AdminRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Title: AdminRoleServiceImpl
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @description: 后台角色管理服务实现类
 */
@Service
@RequiredArgsConstructor
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {

    private final AdminRoleMapper adminRoleMapper;
    private final AdminUserMapper adminUserMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<AdminRole> getRoleList() {
        return adminRoleMapper.selectList(null);
    }

    @Override
    public void addRole(AdminRoleDTO adminRoleDTO) {
        // 检查角色编码是否已存在
        if (adminRoleMapper.selectOne(new LambdaQueryWrapper<AdminRole>().eq(AdminRole::getRoleCode, adminRoleDTO.getRoleCode())) != null) {
            throw new GeneralBusinessException("角色编码已存在");
        }

        AdminRole adminRole = convertDtoToEntity(adminRoleDTO);
        adminRoleMapper.insert(adminRole);
    }

    @Override
    public void updateRole(AdminRoleDTO adminRoleDTO) {
        if (adminRoleDTO.getId() == null) {
            throw new GeneralBusinessException("更新角色时ID不能为空");
        }
        // 检查要更新的角色是否存在
        AdminRole existingRole = adminRoleMapper.selectById(adminRoleDTO.getId());
        if (existingRole == null) {
            throw new GeneralBusinessException("角色不存在");
        }

        // 检查角色编码是否与其它角色冲突
        AdminRole roleWithSameCode = adminRoleMapper.selectOne(new LambdaQueryWrapper<AdminRole>().eq(AdminRole::getRoleCode, adminRoleDTO.getRoleCode()));
        if (roleWithSameCode != null && !Objects.equals(roleWithSameCode.getId(), adminRoleDTO.getId())) {
            throw new GeneralBusinessException("角色编码已被其他角色使用");
        }

        AdminRole adminRole = convertDtoToEntity(adminRoleDTO);
        adminRoleMapper.updateById(adminRole);
    }

    @Override
    public void deleteRole(Long roleId) {
        // 检查是否有用户正在使用该角色
        Long userCount = adminUserMapper.selectCount(new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getRoleId, roleId));
        if (userCount > 0) {
            throw new GeneralBusinessException("无法删除，仍有用户在使用该角色");
        }
        // 系统内置角色不允许删除
        AdminRole adminRole = adminRoleMapper.selectById(roleId);
        if (adminRole != null && Objects.equals(adminRole.getIsSystem(), 1)) {
            throw new GeneralBusinessException("系统内置角色不允许删除");
        }

        adminRoleMapper.deleteById(roleId);
    }

    /**
     * 将 DTO 转换为 Entity
     */
    private AdminRole convertDtoToEntity(AdminRoleDTO dto) {
        AdminRole entity = new AdminRole();
        entity.setId(dto.getId());
        entity.setRoleName(dto.getRoleName());
        entity.setRoleCode(dto.getRoleCode());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());

        // 将 List<String> 类型的权限列表转换为 JSON 字符串
        if (!CollectionUtils.isEmpty(dto.getPermissions())) {
            try {
                entity.setPermissions(objectMapper.writeValueAsString(dto.getPermissions()));
            } catch (JsonProcessingException e) {
                throw new GeneralBusinessException("权限列表序列化失败");
            }
        } else {
            entity.setPermissions("[]"); // 默认为空数组
        }

        return entity;
    }
}