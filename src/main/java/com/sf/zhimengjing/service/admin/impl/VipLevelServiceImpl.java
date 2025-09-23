package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.model.dto.VipLevelDTO;
import com.sf.zhimengjing.entity.admin.VipLevel;
import com.sf.zhimengjing.mapper.admin.VipLevelMapper;
import com.sf.zhimengjing.service.admin.VipLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: VipLevelServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @Description: VIP等级服务实现类，提供VIP等级相关业务逻辑
 */
@Service
@RequiredArgsConstructor
public class VipLevelServiceImpl extends ServiceImpl<VipLevelMapper, VipLevel> implements VipLevelService {

    /** 获取所有VIP等级 */
    @Override
    public List<VipLevelDTO> getAllLevels() {
        List<VipLevel> levels = this.list();
        return levels.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /** 获取启用的VIP等级 */
    @Override
    public List<VipLevelDTO> getActiveLevels() {
        LambdaQueryWrapper<VipLevel> wrapper = new LambdaQueryWrapper<VipLevel>()
                .eq(VipLevel::getIsActive, true)
                .orderByAsc(VipLevel::getLevelOrder);

        List<VipLevel> levels = this.list(wrapper);
        return levels.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /** 根据ID获取VIP等级 */
    @Override
    public VipLevelDTO getLevelById(Long levelId) {
        VipLevel level = this.getById(levelId);
        return convertToDTO(level);
    }

    /** 创建VIP等级 */
    @Override
    @Transactional
    public boolean createLevel(VipLevelDTO.LevelRequestDTO requestDTO) {
        // 检查等级编码是否已存在
        LambdaQueryWrapper<VipLevel> wrapper = new LambdaQueryWrapper<VipLevel>()
                .eq(VipLevel::getLevelCode, requestDTO.getLevelCode());

        if (this.count(wrapper) > 0) {
            throw new RuntimeException("等级编码已存在");
        }

        VipLevel level = new VipLevel();
        BeanUtils.copyProperties(requestDTO, level);

        // 设置默认值
        if (level.getLevelOrder() == null) {
            level.setLevelOrder(0);
        }
        if (level.getIsActive() == null) {
            level.setIsActive(true);
        }

        return this.save(level);
    }

    /** 更新VIP等级 */
    @Override
    @Transactional
    public boolean updateLevel(Long levelId, VipLevelDTO.LevelRequestDTO requestDTO) {
        VipLevel level = this.getById(levelId);
        if (level == null) {
            throw new RuntimeException("VIP等级不存在");
        }

        // 检查等级编码是否被其他记录使用
        if (!level.getLevelCode().equals(requestDTO.getLevelCode())) {
            LambdaQueryWrapper<VipLevel> wrapper = new LambdaQueryWrapper<VipLevel>()
                    .eq(VipLevel::getLevelCode, requestDTO.getLevelCode())
                    .ne(VipLevel::getId, levelId);

            if (this.count(wrapper) > 0) {
                throw new RuntimeException("等级编码已被其他等级使用");
            }
        }

        // 手动复制属性，只更新不为null的字段，解决覆盖问题
        if (requestDTO.getLevelCode() != null) {
            level.setLevelCode(requestDTO.getLevelCode());
        }
        if (requestDTO.getLevelName() != null) {
            level.setLevelName(requestDTO.getLevelName());
        }
        if (requestDTO.getLevelOrder() != null) {
            level.setLevelOrder(requestDTO.getLevelOrder());
        }
        if (requestDTO.getMonthlyPrice() != null) {
            level.setMonthlyPrice(requestDTO.getMonthlyPrice());
        }
        if (requestDTO.getQuarterlyPrice() != null) {
            level.setQuarterlyPrice(requestDTO.getQuarterlyPrice());
        }
        if (requestDTO.getYearlyPrice() != null) {
            level.setYearlyPrice(requestDTO.getYearlyPrice());
        }
        if (requestDTO.getDiscountRate() != null) {
            level.setDiscountRate(requestDTO.getDiscountRate());
        }
        if (requestDTO.getDailyAnalysisLimit() != null) {
            level.setDailyAnalysisLimit(requestDTO.getDailyAnalysisLimit());
        }
        if (requestDTO.getStorageSpaceMb() != null) {
            level.setStorageSpaceMb(requestDTO.getStorageSpaceMb());
        }
        if (requestDTO.getAiModelAccess() != null) {
            level.setAiModelAccess(String.valueOf(requestDTO.getAiModelAccess()));
        }
        if (requestDTO.getPrioritySupport() != null) {
            level.setPrioritySupport(requestDTO.getPrioritySupport());
        }
        if (requestDTO.getAdFree() != null) {
            level.setAdFree(requestDTO.getAdFree());
        }
        if (requestDTO.getDescription() != null) {
            level.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getIsActive() != null) {
            level.setIsActive(requestDTO.getIsActive());
        }

        return this.updateById(level);
    }

    /** 启用/禁用VIP等级 */
    @Override
    @Transactional
    public boolean toggleLevelStatus(Long levelId, Boolean isActive) {
        VipLevel level = this.getById(levelId);
        if (level == null) {
            throw new RuntimeException("VIP等级不存在");
        }

        level.setIsActive(isActive);
        return this.updateById(level);
    }

    /** 删除VIP等级 */
    @Override
    @Transactional
    public boolean deleteLevel(Long levelId) {
        VipLevel level = this.getById(levelId);
        if (level == null) {
            throw new RuntimeException("VIP等级不存在");
        }

        // 检查是否有会员使用该等级
        // 这里需要注入VipMemberService来检查
        // if (vipMemberService.countByLevelId(levelId) > 0) {
        //     throw new RuntimeException("该等级下还有会员，无法删除");
        // }

        return this.removeById(levelId);
    }

    /** 获取等级统计信息 */
    @Override
    public VipLevelDTO.LevelStatsVO getLevelStats() {
        VipLevelDTO.LevelStatsVO stats = new VipLevelDTO.LevelStatsVO();

        // 统计总等级数
        stats.setTotalLevels(this.count());

        // 统计启用等级数
        LambdaQueryWrapper<VipLevel> activeWrapper = new LambdaQueryWrapper<VipLevel>()
                .eq(VipLevel::getIsActive, true);
        stats.setActiveLevels(this.count(activeWrapper));

        // 统计禁用等级数
        LambdaQueryWrapper<VipLevel> inactiveWrapper = new LambdaQueryWrapper<VipLevel>()
                .eq(VipLevel::getIsActive, false);
        stats.setInactiveLevels(this.count(inactiveWrapper));

        // 这里需要注入VipMemberService来获取会员统计
        // stats.setTotalMembers(vipMemberService.count());

        return stats;
    }

    /** 分页查询VIP等级 */
    @Override
    public IPage<VipLevelDTO> getLevelPage(Page<VipLevelDTO> page, String levelName, Boolean isActive) {
        LambdaQueryWrapper<VipLevel> wrapper = new LambdaQueryWrapper<VipLevel>()
                .like(StringUtils.hasText(levelName), VipLevel::getLevelName, levelName)
                .eq(isActive != null, VipLevel::getIsActive, isActive)
                .orderByAsc(VipLevel::getLevelOrder);

        IPage<VipLevel> entityPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        return entityPage.convert(this::convertToDTO);
    }

    /** 实体转换 DTO */
    private VipLevelDTO convertToDTO(VipLevel entity) {
        if (entity == null) return null;

        VipLevelDTO dto = new VipLevelDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}