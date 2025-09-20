package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.zhimengjing.common.model.dto.AdminUserDTO;
import com.sf.zhimengjing.common.model.vo.AdminUserVO;

/**
 * @Title: AdminUserService
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.admin
 * @description: 后台管理员业务接口，提供增删改查和分页查询功能
 */
public interface AdminUserService {

    /**
     * 分页查询后台管理员列表
     *
     * @param pageNum  页码，从1开始
     * @param pageSize 每页记录数
     * @return 分页结果，包含管理员列表和分页信息
     */
    IPage<AdminUserVO> pageAdminUsers(int pageNum, int pageSize);

    /**
     * 创建后台管理员
     *
     * @param adminUserDTO 创建数据对象
     * @param creatorId    创建人ID
     */
    void createAdminUser(AdminUserDTO adminUserDTO, Long creatorId);

    /**
     * 更新后台管理员
     *
     * @param id           管理员ID
     * @param adminUserDTO 更新数据对象
     * @param updaterId    操作人ID
     */
    void updateAdminUser(Long id, AdminUserDTO adminUserDTO, Long updaterId);

    /**
     * 删除后台管理员
     *
     * @param id         管理员ID，即要被删除的管理员
     * @param operatorId 操作人ID，即执行删除操作的管理员
     */
    void deleteAdminUser(Long id, Long operatorId);

}
