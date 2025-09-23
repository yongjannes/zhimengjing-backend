package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.VipBenefitsDTO;
import com.sf.zhimengjing.entity.admin.VipMember;

import java.util.List;

/**
 * @Title: VipMemberService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: VIP会员服务接口，提供VIP会员相关业务操作
 */
public interface VipMemberService extends IService<VipMember> {

    /** 获取会员信息 */
    VipBenefitsDTO getMemberInfo(Long userId);

    /** 激活会员 */
    boolean activateMember(Long userId, Long levelId, Integer durationMonths);

    /** 续费会员 */
    boolean renewMember(Long userId, Integer durationMonths);

    /** 升级会员等级 */
    boolean upgradeMemberLevel(Long userId, Long targetLevelId);

    /** 降级会员等级 */
    boolean downgradeMemberLevel(Long userId, Long targetLevelId);

    /** 取消会员资格 */
    boolean cancelMembership(Long userId, String reason);

    /** 检查会员权益 */
    boolean checkMemberBenefit(Long userId, String benefitType);

    /** 使用解析次数 */
    boolean useAnalysisCount(Long userId, Integer count);

    /** 获取会员使用统计 */
    VipBenefitsDTO.UsageStatsDTO getUsageStats(Long userId);

    /** 获取即将到期的会员 */
    List<VipMember> getExpiringMembers(int daysBeforeExpiry);

    /** 处理到期会员 */
    boolean handleExpiredMembers();

    /** 分页查询会员列表 */
    IPage<VipMember> getMemberPage(Page<VipMember> page, String status, Long levelId);
}