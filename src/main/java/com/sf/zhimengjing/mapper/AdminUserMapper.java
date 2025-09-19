package com.sf.zhimengjing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Title: AdminUserMapper
 * @Author 殇枫
 * @Package com.sf.zhimengjing.mapper
 * @Description: 管理员用户数据访问层接口
 * @Date: 2025-09-19
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}
