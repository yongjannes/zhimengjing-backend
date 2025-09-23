package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.VipLevelDTO;
import com.sf.zhimengjing.entity.admin.VipLevel;

import java.util.List;

/**
 * @Title: VipLevelService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: VIP等级服务接口，提供VIP等级相关业务操作
 */
public interface VipLevelService extends IService<VipLevel> {

    /** 获取所有VIP等级 */
    List<VipLevelDTO> getAllLevels();

    /** 获取启用的VIP等级 */
    List<VipLevelDTO> getActiveLevels();

    /** 根据ID获取VIP等级 */
    VipLevelDTO getLevelById(Long levelId);

    /** 创建VIP等级 */
    boolean createLevel(VipLevelDTO.LevelRequestDTO requestDTO);

    /** 更新VIP等级 */
    boolean updateLevel(Long levelId, VipLevelDTO.LevelRequestDTO requestDTO);

    /** 启用/禁用VIP等级 */
    boolean toggleLevelStatus(Long levelId, Boolean isActive);

    /** 删除VIP等级 */
    boolean deleteLevel(Long levelId);

    /** 获取等级权益详情 */
    VipLevelDTO.LevelStatsVO getLevelStats();

    /** 分页查询VIP等级 */
    IPage<VipLevelDTO> getLevelPage(Page<VipLevelDTO> page, String levelName, Boolean isActive);
}