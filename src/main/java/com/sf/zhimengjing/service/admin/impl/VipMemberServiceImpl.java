package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.model.dto.VipBenefitsDTO;
import com.sf.zhimengjing.entity.admin.VipLevel;
import com.sf.zhimengjing.entity.admin.VipMember;
import com.sf.zhimengjing.mapper.admin.VipMemberMapper;
import com.sf.zhimengjing.service.admin.VipLevelService;
import com.sf.zhimengjing.service.admin.VipMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Title: VipMemberServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @Description: VIP会员服务实现类，提供VIP会员相关业务逻辑
 */
@Service
@RequiredArgsConstructor
public class VipMemberServiceImpl extends ServiceImpl<VipMemberMapper, VipMember> implements VipMemberService {

    private final VipLevelService vipLevelService;

    /** 获取会员信息 */
    @Override
    public VipBenefitsDTO getMemberInfo(Long userId) {
        VipMember member = this.getOne(new LambdaQueryWrapper<VipMember>().eq(VipMember::getUserId, userId));
        if (member == null) {
            return null;
        }

        VipLevel level = vipLevelService.getById(member.getLevelId());
        if (level == null) {
            return null;
        }

        VipBenefitsDTO dto = new VipBenefitsDTO();
        BeanUtils.copyProperties(member, dto);
        dto.setLevelName(level.getLevelName());
        dto.setDailyAnalysisLimit(level.getDailyAnalysisLimit());
        dto.setStorageSpaceMb(level.getStorageSpaceMb());
        dto.setPrioritySupport(level.getPrioritySupport());
        dto.setAdFree(level.getAdFree());
        dto.setDiscountRate(level.getDiscountRate());

        return dto;
    }

    /** 激活会员 */
    @Override
    @Transactional
    public boolean activateMember(Long userId, Long levelId, Integer durationMonths) {
        // 检查用户是否已有会员资格
        VipMember existingMember = this.getOne(new LambdaQueryWrapper<VipMember>().eq(VipMember::getUserId, userId));
        if (existingMember != null) {
            throw new RuntimeException("用户已有会员资格");
        }

        VipLevel level = vipLevelService.getById(levelId);
        if (level == null) {
            throw new RuntimeException("VIP等级不存在");
        }

        VipMember member = new VipMember();
        member.setUserId(userId);
        member.setLevelId(levelId);
        member.setMembershipNo(generateMembershipNo());
        member.setStatus("active");
        member.setStartDate(LocalDate.now());
        member.setExpireDate(LocalDate.now().plusMonths(durationMonths));
        member.setAutoRenew(false);
        member.setTotalPaidAmount(BigDecimal.ZERO);
        member.setRemainingAnalysisCount(level.getDailyAnalysisLimit());
        member.setUsedAnalysisCount(0);

        return this.save(member);
    }

    /** 续费会员 */
    @Override
    @Transactional
    public boolean renewMember(Long userId, Integer durationMonths) {
        VipMember member = this.getOne(new LambdaQueryWrapper<VipMember>().eq(VipMember::getUserId, userId));
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        // 延长到期时间
        LocalDate newExpireDate = member.getExpireDate().plusMonths(durationMonths);
        member.setExpireDate(newExpireDate);
        member.setStatus("active");

        return this.updateById(member);
    }

    /** 升级会员等级 */
    @Override
    @Transactional
    public boolean upgradeMemberLevel(Long userId, Long targetLevelId) {
        VipMember member = this.getOne(new LambdaQueryWrapper<VipMember>().eq(VipMember::getUserId, userId));
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        VipLevel targetLevel = vipLevelService.getById(targetLevelId);
        if (targetLevel == null) {
            throw new RuntimeException("目标等级不存在");
        }

        member.setLevelId(targetLevelId);
        member.setUpgradeDate(LocalDateTime.now());
        member.setRemainingAnalysisCount(targetLevel.getDailyAnalysisLimit());

        return this.updateById(member);
    }

    /** 降级会员等级 */
    @Override
    @Transactional
    public boolean downgradeMemberLevel(Long userId, Long targetLevelId) {
        VipMember member = this.getOne(new LambdaQueryWrapper<VipMember>().eq(VipMember::getUserId, userId));
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        VipLevel targetLevel = vipLevelService.getById(targetLevelId);
        if (targetLevel == null) {
            throw new RuntimeException("目标等级不存在");
        }

        member.setLevelId(targetLevelId);
        member.setDowngradeDate(LocalDateTime.now());
        member.setRemainingAnalysisCount(targetLevel.getDailyAnalysisLimit());

        return this.updateById(member);
    }

    /** 取消会员资格 */
    @Override
    @Transactional
    public boolean cancelMembership(Long userId, String reason) {
        VipMember member = this.getOne(new LambdaQueryWrapper<VipMember>().eq(VipMember::getUserId, userId));
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }

        member.setStatus("cancelled");
        return this.updateById(member);
    }

    /** 检查会员权益 */
    @Override
    public boolean checkMemberBenefit(Long userId, String benefitType) {
        VipMember member = this.getOne(new LambdaQueryWrapper<VipMember>().eq(VipMember::getUserId, userId));
        if (member == null || !"active".equals(member.getStatus())) {
            return false;
        }

        // 检查会员是否过期
        if (member.getExpireDate().isBefore(LocalDate.now())) {
            member.setStatus("expired");
            this.updateById(member);
            return false;
        }

        VipLevel level = vipLevelService.getById(member.getLevelId());
        if (level == null) {
            return false;
        }

        // 根据权益类型检查
        switch (benefitType) {
            case "priority_support":
                return level.getPrioritySupport();
            case "ad_free":
                return level.getAdFree();
            case "analysis_count":
                return member.getRemainingAnalysisCount() > 0;
            default:
                return false;
        }
    }

    /** 使用解析次数 */
    @Override
    @Transactional
    public boolean useAnalysisCount(Long userId, Integer count) {
        VipMember member = this.getOne(new LambdaQueryWrapper<VipMember>().eq(VipMember::getUserId, userId));
        if (member == null || !"active".equals(member.getStatus())) {
            throw new RuntimeException("会员不存在或状态异常");
        }

        if (member.getRemainingAnalysisCount() < count) {
            throw new RuntimeException("解析次数不足");
        }

        member.setRemainingAnalysisCount(member.getRemainingAnalysisCount() - count);
        member.setUsedAnalysisCount(member.getUsedAnalysisCount() + count);

        return this.updateById(member);
    }

    /** 获取会员使用统计 */
    @Override
    public VipBenefitsDTO.UsageStatsDTO getUsageStats(Long userId) {
        VipMember member = this.getOne(new LambdaQueryWrapper<VipMember>().eq(VipMember::getUserId, userId));
        if (member == null) {
            return null;
        }

        VipLevel level = vipLevelService.getById(member.getLevelId());
        if (level == null) {
            return null;
        }

        VipBenefitsDTO.UsageStatsDTO stats = new VipBenefitsDTO.UsageStatsDTO();
        stats.setTodayAnalysisUsed(0); // 这里需要根据实际业务逻辑计算
        stats.setTodayAnalysisLimit(level.getDailyAnalysisLimit());
        stats.setMonthAnalysisUsed(member.getUsedAnalysisCount());
        stats.setMonthAnalysisLimit(level.getDailyAnalysisLimit() * 30); // 假设按30天计算
        stats.setStorageUsedMb(0L); // 这里需要根据实际业务逻辑计算
        stats.setStorageLimitMb(level.getStorageSpaceMb().longValue());
        stats.setUsagePercentage(BigDecimal.valueOf(0.0)); // 这里需要根据实际业务逻辑计算

        return stats;
    }

    /** 获取即将到期的会员 */
    @Override
    public List<VipMember> getExpiringMembers(int daysBeforeExpiry) {
        LocalDate targetDate = LocalDate.now().plusDays(daysBeforeExpiry);
        LambdaQueryWrapper<VipMember> wrapper = new LambdaQueryWrapper<VipMember>()
                .eq(VipMember::getStatus, "active")
                .le(VipMember::getExpireDate, targetDate)
                .orderByAsc(VipMember::getExpireDate);

        return this.list(wrapper);
    }

    /** 处理到期会员 */
    @Override
    @Transactional
    public boolean handleExpiredMembers() {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<VipMember> wrapper = new LambdaQueryWrapper<VipMember>()
                .eq(VipMember::getStatus, "active")
                .lt(VipMember::getExpireDate, today);

        List<VipMember> expiredMembers = this.list(wrapper);
        for (VipMember member : expiredMembers) {
            member.setStatus("expired");
        }

        return this.updateBatchById(expiredMembers);
    }

    /** 分页查询会员列表 */
    @Override
    public IPage<VipMember> getMemberPage(Page<VipMember> page, String status, Long levelId) {
        LambdaQueryWrapper<VipMember> wrapper = new LambdaQueryWrapper<VipMember>()
                .eq(status != null, VipMember::getStatus, status)
                .eq(levelId != null, VipMember::getLevelId, levelId)
                .orderByDesc(VipMember::getCreateTime);

        return this.page(page, wrapper);
    }

    /** 生成会员编号 */
    private String generateMembershipNo() {
        return "VIP" + System.currentTimeMillis();
    }
}