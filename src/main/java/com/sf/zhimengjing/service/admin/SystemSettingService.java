package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.SystemSettingDTO;
import com.sf.zhimengjing.common.model.vo.SystemSettingVO;
import com.sf.zhimengjing.entity.admin.SystemSetting;

import java.util.Map;

/**
 * @Title: SystemSettingService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: 系统配置服务接口
 */
public interface SystemSettingService extends IService<SystemSetting> {

    /** 分页查询系统配置 */
    IPage<SystemSettingVO> getSettingList(SystemSettingDTO dto);

    /** 根据ID获取配置 */
    SystemSettingVO getSettingById(Long id);

    /** 根据键名获取配置 */
    SystemSettingVO getSettingByKey(String settingKey);

    /** 创建系统配置 */
    SystemSettingVO createSetting(SystemSettingDTO dto);

    /** 更新系统配置 */
    SystemSettingVO updateSetting(Long id, SystemSettingDTO dto);

    /** 删除系统配置 */
    Boolean deleteSetting(Long id);

    /** 根据分类获取配置 */
    Map<String, Object> getSettingsByCategory(String category);

    /** 批量更新配置 */
    Boolean batchUpdateSettings(Map<String, String> settings);

    /** 刷新配置缓存 */
    Boolean refreshCache();

    /** 获取配置统计信息 */
    Map<String, Long> getSettingStatistics();
}