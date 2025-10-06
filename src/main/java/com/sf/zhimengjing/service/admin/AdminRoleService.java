package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.AdminRoleDTO;
import com.sf.zhimengjing.common.model.dto.AdminRoleQueryDTO;
import com.sf.zhimengjing.common.model.vo.OptionVO;
import com.sf.zhimengjing.entity.admin.AdminRole;

import java.util.List;

/**
 * @Title: AdminRoleService
 * @Package: com.sf.zhimengjing.service.admin
 * @description: 后台角色管理服务接口
 */
public interface AdminRoleService extends IService<AdminRole> {

    /**
     * 分页获取所有角色列表
     * @param queryDTO 查询参数
     * @return 分页后的角色列表
     */
    Page<AdminRole> getRoleListByPage(AdminRoleQueryDTO queryDTO);

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
     * 根据ID批量删除角色 (逻辑删除)
     * @param ids 角色ID字符串，逗号分隔
     */
    void deleteRoles(String ids);

    /**
     * 根据ID获取角色详情
     * @param roleId 角色ID
     * @return 角色详情
     */
    AdminRole getRoleById(Long roleId);

    /**
     * 获取所有非系统角色的编码选项
     *
     * @return 角色编码选项列表
     */
    List<OptionVO> getRoleCodeOptions();
}