package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.ConfigChangeHistoryDTO;
import com.sf.zhimengjing.common.model.vo.ConfigChangeHistoryVO;
import com.sf.zhimengjing.entity.admin.ConfigChangeHistory;
import com.sf.zhimengjing.mapper.admin.ConfigChangeHistoryMapper;
import com.sf.zhimengjing.service.admin.ConfigChangeHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: ConfigChangeHistoryServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @Description: 配置变更历史服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigChangeHistoryServiceImpl extends ServiceImpl<ConfigChangeHistoryMapper, ConfigChangeHistory> implements ConfigChangeHistoryService {

    @Override
    public IPage<ConfigChangeHistoryVO> getChangeHistoryList(ConfigChangeHistoryDTO dto) {
        LambdaQueryWrapper<ConfigChangeHistory> wrapper = new LambdaQueryWrapper<ConfigChangeHistory>()
                .like(StringUtils.hasText(dto.getKeyword()), ConfigChangeHistory::getConfigKey, dto.getKeyword())
                .eq(StringUtils.hasText(dto.getConfigKey()), ConfigChangeHistory::getConfigKey, dto.getConfigKey())
                .eq(StringUtils.hasText(dto.getChangeType()), ConfigChangeHistory::getChangeType, dto.getChangeType())
                .eq(dto.getChangedBy() != null, ConfigChangeHistory::getChangedBy, dto.getChangedBy())
                .ge(dto.getStartTime() != null, ConfigChangeHistory::getChangeTime, dto.getStartTime())
                .le(dto.getEndTime() != null, ConfigChangeHistory::getChangeTime, dto.getEndTime())
                .orderByDesc(ConfigChangeHistory::getChangeTime);

        Page<ConfigChangeHistory> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<ConfigChangeHistory> entityPage = this.page(page, wrapper);

        return entityPage.convert(this::convertToVO);
    }

    @Override
    public Boolean recordConfigChange(String configKey, String oldValue, String newValue,
                                      String changeType, String changeReason, Long changedBy,
                                      String ipAddress, String userAgent) {

        ConfigChangeHistory history = new ConfigChangeHistory();
        history.setConfigKey(configKey);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setChangeType(changeType);
        history.setChangeReason(changeReason);
        history.setChangedBy(changedBy);
        history.setChangeTime(LocalDateTime.now());
        history.setIpAddress(ipAddress);
        history.setUserAgent(userAgent);

        boolean saved = this.save(history);
        if (saved) {
            log.info("记录配置变更历史: {} {} -> {}", configKey, oldValue, newValue);
        }

        return saved;
    }

    @Override
    public List<ConfigChangeHistoryVO> getHistoryByConfigKey(String configKey, Integer limit) {
        List<ConfigChangeHistory> histories = baseMapper.getHistoryByConfigKey(configKey, limit);
        return histories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfigChangeHistoryVO> getHistoryByUser(Long userId, LocalDateTime startTime,
                                                        LocalDateTime endTime, Integer limit) {
        List<ConfigChangeHistory> histories = baseMapper.getHistoryByUser(userId, startTime, endTime, limit);
        return histories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public String cleanExpiredHistory(Integer daysToKeep) {
        if (daysToKeep == null || daysToKeep < 0) {
            throw new IllegalArgumentException("保留天数必须大于等于0");
        }
        try {
            LocalDateTime beforeTime = LocalDateTime.now().minusDays(daysToKeep);

            // 1. 先检查是否存在需要清理的记录
            LambdaQueryWrapper<ConfigChangeHistory> countWrapper = new LambdaQueryWrapper<ConfigChangeHistory>()
                    .lt(ConfigChangeHistory::getChangeTime, beforeTime);
            long count = this.count(countWrapper);

            if (count == 0) {
                String message = String.format("没有找到 %d 天前的配置变更历史记录，无需执行清理操作。", daysToKeep);
                log.info(message);
                return message;
            }

            // 2. 如果存在，则执行清理
            int deletedCount = baseMapper.cleanExpiredHistory(beforeTime);
            String message = String.format("成功清理了 %d 条 %d 天前的过期配置变更历史记录。", deletedCount, daysToKeep);
            log.info(message);
            return message;

        } catch (Exception e) {
            log.error("清理过期配置变更历史时发生错误", e);
            throw new GeneralBusinessException("清理过期历史记录失败，请稍后重试");
        }
    }


    /** 实体转VO */
    private ConfigChangeHistoryVO convertToVO(ConfigChangeHistory entity) {
        ConfigChangeHistoryVO vo = new ConfigChangeHistoryVO();
        vo.setId(entity.getId());
        vo.setConfigKey(entity.getConfigKey());
        vo.setOldValue(entity.getOldValue());
        vo.setNewValue(entity.getNewValue());
        vo.setChangeType(entity.getChangeType());
        vo.setChangeReason(entity.getChangeReason());
        vo.setChangedBy(String.valueOf(entity.getChangedBy()));
        vo.setChangeTime(entity.getChangeTime());
        vo.setIpAddress(entity.getIpAddress());
        vo.setUserAgent(entity.getUserAgent());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}