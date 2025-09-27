package com.sf.zhimengjing.mapper.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.zhimengjing.entity.admin.ConfigChangeHistory;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: ConfigChangeHistoryMapper
 * @Author 殇枫
 * @Package com.sf.zhimengjing.mapper.admin
 * @description: 配置变更历史数据访问层
 */
public interface ConfigChangeHistoryMapper extends BaseMapper<ConfigChangeHistory> {

    /**
     * 根据配置键获取变更历史
     * @param configKey 配置键
     * @param limit 限制记录数
     * @return 变更历史列表
     */
    List<ConfigChangeHistory> getHistoryByConfigKey(@Param("configKey") String configKey,
                                                    @Param("limit") Integer limit);

    /**
     * 根据用户ID获取变更历史
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制记录数
     * @return 变更历史列表
     */
    List<ConfigChangeHistory> getHistoryByUser(@Param("userId") Long userId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("limit") Integer limit);

    /**
     * 清理过期的变更历史记录
     * @param beforeTime 指定时间之前的记录将被删除
     * @return 删除的记录数
     */
    int cleanExpiredHistory(@Param("beforeTime") LocalDateTime beforeTime);

}
