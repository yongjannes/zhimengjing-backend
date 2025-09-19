package com.sf.zhimengjing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.entity.AdminOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Title: AdminOperationLogMapper
 * @Author 殇枫
 * @Package com.sf.zhimengjing.mapper
 * @description: 管理员操作日志数据访问层接口
 */
@Mapper
public interface AdminOperationLogMapper extends BaseMapper<AdminOperationLog> {
}
