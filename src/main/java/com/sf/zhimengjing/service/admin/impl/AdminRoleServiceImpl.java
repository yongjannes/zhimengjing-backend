package com.sf.zhimengjing.service.admin.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.AdminRoleDTO;
import com.sf.zhimengjing.common.model.dto.AdminRoleQueryDTO;
import com.sf.zhimengjing.common.model.vo.OptionVO;
import com.sf.zhimengjing.entity.admin.AdminRole;
import com.sf.zhimengjing.entity.admin.AdminUser;
import com.sf.zhimengjing.mapper.admin.AdminRoleMapper;
import com.sf.zhimengjing.mapper.admin.AdminUserMapper;
import com.sf.zhimengjing.service.admin.AdminRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public Page<AdminRole> getRoleListByPage(AdminRoleQueryDTO queryDTO) {
        Page<AdminRole> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<AdminRole> queryWrapper = new LambdaQueryWrapper<AdminRole>()
                .like(StrUtil.isNotBlank(queryDTO.getRoleName()), AdminRole::getRoleName, queryDTO.getRoleName())
                .eq(queryDTO.getStatus() != null, AdminRole::getStatus, queryDTO.getStatus())
                .orderByDesc(AdminRole::getIsSystem);
        return adminRoleMapper.selectPage(page, queryWrapper);
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
    public void deleteRoles(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new GeneralBusinessException("请选择要删除的角色");
        }

        List<Long> roleIds = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        for (Long roleId : roleIds) {
            AdminRole adminRole = adminRoleMapper.selectById(roleId);
            // 检查1：角色是否存在
            if (adminRole == null) {
                // 如果角色本身就不存在，直接抛出异常
                throw new GeneralBusinessException("操作失败，角色ID: " + roleId + " 不存在");
            }
            // 检查2：是否为系统角色
            if (Objects.equals(adminRole.getIsSystem(), 1)) {
                throw new GeneralBusinessException("系统内置角色 '"+ adminRole.getRoleName() +"' 不允许删除");
            }
            // 检查3：是否仍被用户使用
            Long userCount = adminUserMapper.selectCount(new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getRoleId, roleId));
            if (userCount > 0) {
                throw new GeneralBusinessException("角色 '"+ adminRole.getRoleName() +"' 仍有用户在使用，无法删除");
            }
        }

        int deletedCount = adminRoleMapper.deleteBatchIds(roleIds);

        if (deletedCount == 0) {
            throw new GeneralBusinessException("删除失败，可能角色已被删除，请刷新页面");
        }
    }

    /**
     * 根据ID获取角色详情
     *
     * @param roleId 角色ID
     * @return 角色详情
     */
    @Override
    public AdminRole getRoleById(Long roleId) {
        AdminRole role = adminRoleMapper.selectById(roleId);
        if (role == null) {
            throw new GeneralBusinessException("角色不存在");
        }
        return role;
    }

    @Override
    public List<OptionVO> getRoleCodeOptions() {
        return this.baseMapper.getRoleCodeOptions();
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