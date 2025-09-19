package com.sf.zhimengjing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.entity.AdminRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Title: AdminRoleMapper
 * @Author 殇枫
 * @Package com.sf.zhimengjing.mapper
 * @description: 管理员角色数据访问层接口
 */
@Mapper
public interface AdminRoleMapper extends BaseMapper<AdminRole> {
}
