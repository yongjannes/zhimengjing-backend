package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.entity.admin.AdminPermission;
import com.sf.zhimengjing.mapper.admin.AdminPermissionMapper;
import com.sf.zhimengjing.service.admin.AdminPermissionService;
import org.springframework.stereotype.Service;

/**
 * @Title: AdminPermissionServiceImpl
 * @Author 殇枫
 * @Package com.sf.zhimengjing.service.admin.impl
 * @description: 权限服务实现类
 */
@Service
public class AdminPermissionServiceImpl extends ServiceImpl<AdminPermissionMapper, AdminPermission> implements AdminPermissionService {
}