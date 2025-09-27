package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.entity.admin.SystemLog;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Title: SystemLogMapper
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.mapper.admin
 * @Description: 系统日志数据访问层
 */

public interface SystemLogMapper extends BaseMapper<SystemLog> {

    /**
     * 统计日志信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果
     */
    Map<String, Long> getLogStatistics(@Param("startTime") LocalDate startTime,
                                       @Param("endTime") LocalDate  endTime);

    /**
     * 清理过期日志
     * @param beforeTime 指定时间之前的日志将被删除
     * @return 删除的记录数
     */
    int cleanExpiredLogs(@Param("beforeTime") LocalDateTime beforeTime);


}