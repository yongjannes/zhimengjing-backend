package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.SystemLogDTO;
import com.sf.zhimengjing.common.model.vo.SystemLogVO;
import com.sf.zhimengjing.entity.admin.SystemLog;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.util.Map;

/**
 * @Title: SystemLogService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: 系统日志服务接口
 */
public interface SystemLogService extends IService<SystemLog> {

    /** 分页查询系统日志 */
    IPage<SystemLogVO> getLogList(SystemLogDTO dto);

    /** 记录系统日志 */
    Boolean recordLog(SystemLog log);

    /** 获取日志统计信息 */
    Map<String, Long> getLogStatistics(LocalDate startTime, LocalDate  endTime);

    /** 清理过期日志 */
    Boolean cleanExpiredLogs(Integer daysToKeep);

    /** 导出日志 */
    void exportLogs(SystemLogDTO dto, HttpServletResponse response);
}