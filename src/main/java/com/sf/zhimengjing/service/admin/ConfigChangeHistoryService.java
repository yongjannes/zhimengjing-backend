package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.ConfigChangeHistoryDTO;
import com.sf.zhimengjing.common.model.vo.ConfigChangeHistoryVO;
import com.sf.zhimengjing.entity.admin.ConfigChangeHistory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: ConfigChangeHistoryService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: 配置变更历史服务接口
 */
public interface ConfigChangeHistoryService extends IService<ConfigChangeHistory> {

    /** 分页查询配置变更历史列表 */
    IPage<ConfigChangeHistoryVO> getChangeHistoryList(ConfigChangeHistoryDTO dto);

    /** 记录配置变更历史 */
    Boolean recordConfigChange(String configKey, String oldValue, String newValue,
                               String changeType, String changeReason, Long changedBy,
                               String ipAddress, String userAgent);

    /** 根据配置键获取变更历史 */
    List<ConfigChangeHistoryVO> getHistoryByConfigKey(String configKey, Integer limit);

    /** 根据用户ID获取变更历史 */
    List<ConfigChangeHistoryVO> getHistoryByUser(Long userId, LocalDateTime startTime,
                                                 LocalDateTime endTime, Integer limit);

    /** 清理过期的变更历史记录 */
    String cleanExpiredHistory(Integer daysToKeep);
}