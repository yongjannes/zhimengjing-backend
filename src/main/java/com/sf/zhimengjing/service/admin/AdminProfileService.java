package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.model.dto.AdminChangePasswordDTO;
import com.sf.zhimengjing.common.model.dto.AdminUpdateInfoDTO;
import com.sf.zhimengjing.common.model.vo.AdminLoginLogVO;
import com.sf.zhimengjing.common.model.vo.AdminProfileVO;

/**
 * @Title: AdminProfileService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @description: 管理员个人中心服务接口
 */
public interface AdminProfileService {

    /**
     * 获取管理员个人信息
     *
     * @param adminId 管理员ID
     * @return 管理员个人信息
     */
    AdminProfileVO getProfile(Long adminId);

    /**
     * 更新管理员个人信息
     *
     * @param adminId   管理员ID
     * @param updateDTO 更新信息DTO
     */
    void updateProfile(Long adminId, AdminUpdateInfoDTO updateDTO);

    /**
     * 修改密码
     *
     * @param adminId     管理员ID
     * @param passwordDTO 密码DTO
     */
    void changePassword(Long adminId, AdminChangePasswordDTO passwordDTO);

    /**
     * 获取登录日志
     *
     * @param adminId  管理员ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 登录日志分页数据
     */
    Page<AdminLoginLogVO> getLoginLogs(Long adminId, Integer pageNum, Integer pageSize);
}