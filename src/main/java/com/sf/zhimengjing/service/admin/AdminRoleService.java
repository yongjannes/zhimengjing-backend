package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.AdminRoleDTO;
import com.sf.zhimengjing.entity.admin.AdminRole;
import java.util.List;

/**
 * @Title: AdminRoleService
 * @Package: com.sf.zhimengjing.service.admin
 * @description: 后台角色管理服务接口
 */
public interface AdminRoleService extends IService<AdminRole> {

    /**
     * 获取所有角色列表
     * @return 角色列表
     */
    List<AdminRole> getRoleList();

    /**
     * 新增角色
     * @param adminRoleDTO 角色信息
     */
    void addRole(AdminRoleDTO adminRoleDTO);

    /**
     * 更新角色信息
     * @param adminRoleDTO 角色信息
     */
    void updateRole(AdminRoleDTO adminRoleDTO);

    /**
     * 根据ID删除角色
     * @param roleId 角色ID
     */
    void deleteRole(Long roleId);
}