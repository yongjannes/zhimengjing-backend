package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.entity.admin.SystemBackup;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @Title: SystemBackupMapper
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.mapper.admin
 * @Description: 系统备份数据访问层
 */
public interface SystemBackupMapper extends BaseMapper<SystemBackup> {

    /**
     * 获取备份统计信息
     * @return 统计结果
     */
    Map<String, Long> getBackupStatistics();

    /**
     * 获取备份磁盘使用情况
     * @return 磁盘使用统计
     */
    Map<String, Long> getDiskUsageStatistics();

    /**
     * 更新备份状态
     * @param id 备份ID
     * @param status 新状态
     * @param errorMessage 错误信息（可选）
     * @return 更新记录数
     */
    int updateBackupStatus(@Param("id") Long id,
                           @Param("status") String status,
                           @Param("errorMessage") String errorMessage);

}