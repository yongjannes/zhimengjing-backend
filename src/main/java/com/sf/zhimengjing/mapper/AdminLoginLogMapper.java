package com.sf.zhimengjing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.entity.AdminLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Title: AdminLoginLogMapper
 * @Author 殇枫
 * @Package com.sf.zhimengjing.mapper
 * @description: 管理员登录日志数据访问层接口
 */
@Mapper
public interface AdminLoginLogMapper extends BaseMapper<AdminLoginLog> {
}
