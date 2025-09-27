package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.SystemSettingDTO;
import com.sf.zhimengjing.common.model.vo.SystemSettingVO;
import com.sf.zhimengjing.entity.admin.SystemSetting;
import com.sf.zhimengjing.mapper.admin.SystemSettingMapper;
import com.sf.zhimengjing.service.admin.SystemSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title: SystemSettingServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @Description: 系统配置服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemSettingServiceImpl extends ServiceImpl<SystemSettingMapper, SystemSetting> implements SystemSettingService {

    @Override
    public IPage<SystemSettingVO> getSettingList(SystemSettingDTO dto) {
        // 构建查询条件
        LambdaQueryWrapper<SystemSetting> wrapper = new LambdaQueryWrapper<SystemSetting>()
                .like(StringUtils.hasText(dto.getKeyword()), SystemSetting::getSettingKey, dto.getKeyword())
                .or()
                .like(StringUtils.hasText(dto.getKeyword()), SystemSetting::getDescription, dto.getKeyword())
                .eq(StringUtils.hasText(dto.getCategory()), SystemSetting::getCategory, dto.getCategory())
                .orderByDesc(SystemSetting::getUpdateTime);

        // 执行分页查询
        Page<SystemSetting> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<SystemSetting> entityPage = this.page(page, wrapper);

        // 转换为VO并返回
        return entityPage.convert(this::convertToVO);
    }

    @Override
    public SystemSettingVO getSettingById(Long id) {
        SystemSetting setting = this.getById(id);
        if (setting == null) {
            throw new GeneralBusinessException("配置项不存在");
        }
        return convertToVO(setting);
    }

    @Override
    @Cacheable(value = "systemSettings", key = "#settingKey")
    public SystemSettingVO getSettingByKey(String settingKey) {
        SystemSetting setting = this.getOne(
                new LambdaQueryWrapper<SystemSetting>()
                        .eq(SystemSetting::getSettingKey, settingKey)
        );
        if (setting == null) {
            throw new GeneralBusinessException("配置项不存在");
        }
        return convertToVO(setting);
    }

    @Override
    @Transactional
    public SystemSettingVO createSetting(SystemSettingDTO dto) {
        // 检查键名是否已存在
        long count = this.count(
                new LambdaQueryWrapper<SystemSetting>()
                        .eq(SystemSetting::getSettingKey, dto.getSettingKey())
        );
        if (count > 0) {
            throw new GeneralBusinessException("配置键名已存在");
        }

        // 创建实体
        SystemSetting setting = new SystemSetting();
        setting.setSettingKey(dto.getSettingKey());
        setting.setSettingValue(dto.getSettingValue());
        setting.setSettingType(dto.getSettingType());
        setting.setCategory(dto.getCategory());
        setting.setDescription(dto.getDescription());
        setting.setIsEncrypted(dto.getIsEncrypted() != null && dto.getIsEncrypted());
        setting.setIsSystem(false);

        // 保存到数据库
        boolean saved = this.save(setting);
        if (!saved) {
            throw new GeneralBusinessException("创建配置失败");
        }

        return convertToVO(setting);
    }

    @Override
    @Transactional
    @CacheEvict(value = "systemSettings", key = "#result.settingKey")
    public SystemSettingVO updateSetting(Long id, SystemSettingDTO dto) {
        SystemSetting setting = this.getById(id);
        if (setting == null) {
            throw new GeneralBusinessException("配置项不存在");
        }

        // 检查系统配置是否可修改
        if (setting.getIsSystem() != null && setting.getIsSystem()) {
            throw new GeneralBusinessException("系统配置不能修改");
        }

        // 更新字段
        setting.setSettingValue(dto.getSettingValue());
        setting.setSettingType(dto.getSettingType());
        setting.setDescription(dto.getDescription());
        setting.setIsEncrypted(dto.getIsEncrypted() != null && dto.getIsEncrypted());

        // 保存更新
        boolean updated = this.updateById(setting);
        if (!updated) {
            throw new GeneralBusinessException("更新配置失败");
        }

        return convertToVO(setting);
    }

    @Override
    @Transactional
    public Boolean deleteSetting(Long id) {
        SystemSetting setting = this.getById(id);
        if (setting == null) {
            throw new GeneralBusinessException("配置项不存在");
        }

        // 检查系统配置是否可删除
        if (setting.getIsSystem() != null && setting.getIsSystem()) {
            throw new GeneralBusinessException("系统配置不能删除");
        }

        return this.removeById(id);
    }

    @Override
    public Map<String, Object> getSettingsByCategory(String category) {
        List<SystemSetting> settings = this.list(
                new LambdaQueryWrapper<SystemSetting>()
                        .eq(SystemSetting::getCategory, category)
        );

        Map<String, Object> result = new HashMap<>();
        settings.forEach(setting -> {
            result.put(setting.getSettingKey(), setting.getSettingValue());
        });

        return result;
    }

    @Override
    @Transactional
    public Boolean batchUpdateSettings(Map<String, String> settings) {
        settings.forEach((key, value) -> {
            SystemSetting setting = this.getOne(
                    new LambdaQueryWrapper<SystemSetting>()
                            .eq(SystemSetting::getSettingKey, key)
            );
            if (setting != null && (setting.getIsSystem() == null || !setting.getIsSystem())) {
                setting.setSettingValue(value);
                this.updateById(setting);
            }
        });
        return true;
    }

    @Override
    @CacheEvict(value = "systemSettings", allEntries = true)
    public Boolean refreshCache() {
        log.info("系统配置缓存已刷新");
        return true;
    }

    @Override
    public Map<String, Long> getSettingStatistics() {
        long totalSettings = this.count();
        long systemSettings = this.count(
                new LambdaQueryWrapper<SystemSetting>()
                        .eq(SystemSetting::getIsSystem, true)
        );
        long userSettings = totalSettings - systemSettings;

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalSettings", totalSettings);
        stats.put("systemSettings", systemSettings);
        stats.put("userSettings", userSettings);

        return stats;
    }

    /** 实体转VO */
    private SystemSettingVO convertToVO(SystemSetting entity) {
        SystemSettingVO vo = new SystemSettingVO();
        vo.setId(entity.getId());
        vo.setSettingKey(entity.getSettingKey());
        vo.setSettingValue(entity.getSettingValue());
        vo.setSettingType(entity.getSettingType());
        vo.setCategory(entity.getCategory());
        vo.setDescription(entity.getDescription());
        vo.setIsEncrypted(entity.getIsEncrypted());
        vo.setIsSystem(entity.getIsSystem());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
