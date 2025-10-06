package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.model.dto.VipBenefitsDTO;
import com.sf.zhimengjing.common.util.SecurityUtils;
import com.sf.zhimengjing.entity.admin.VipMember;
import com.sf.zhimengjing.service.admin.VipMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Title: VipMemberController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @description: VIP会员管理控制器，提供会员信息查询、激活、续费、升级、取消及统计接口
 */
@RestController
@RequestMapping("/api/vip/member")
@Tag(name = "VIP会员管理", description = "提供会员信息查询、激活、续费、升级、取消及统计接口")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ops:vip:manage')")
public class VipMemberController {

    private final VipMemberService vipMemberService;

    @GetMapping("/info")
    @Operation(summary = "1. 获取会员信息")
    public VipBenefitsDTO getMemberInfo() {
        Long userId = SecurityUtils.getUserId();
        return vipMemberService.getMemberInfo(userId);
    }

    @GetMapping("/usage-stats")
    @Operation(summary = "2. 获取会员使用统计")
    public VipBenefitsDTO.UsageStatsDTO getUsageStats() {
        Long userId = SecurityUtils.getUserId();
        return vipMemberService.getUsageStats(userId);
    }

    @PostMapping("/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "3. 激活会员")
    public Boolean activateMember(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "会员等级ID", required = true) @RequestParam Long levelId,
            @Parameter(description = "激活时长（月）", required = true) @RequestParam Integer durationMonths
    ) {
        return vipMemberService.activateMember(userId, levelId, durationMonths);
    }

    @PostMapping("/renew")
    @Operation(summary = "4. 续费会员")
    public Boolean renewMember(
            @Parameter(description = "续费时长（月）", required = true) @RequestParam Integer durationMonths
    ) {
        Long userId = SecurityUtils.getUserId();
        return vipMemberService.renewMember(userId, durationMonths);
    }

    @PostMapping("/upgrade")
    @Operation(summary = "5. 升级会员等级")
    public Boolean upgradeMemberLevel(
            @Parameter(description = "目标会员等级ID", required = true) @RequestParam Long targetLevelId
    ) {
        Long userId = SecurityUtils.getUserId();
        return vipMemberService.upgradeMemberLevel(userId, targetLevelId);
    }

    @PostMapping("/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "6. 取消会员资格")
    public Boolean cancelMembership(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "取消原因", required = true) @RequestParam String reason
    ) {
        return vipMemberService.cancelMembership(userId, reason);
    }

    @GetMapping("/check-benefit")
    @Operation(summary = "7. 检查会员权益")
    public Boolean checkMemberBenefit(
            @Parameter(description = "权益类型(priority_support/ad_free/analysis_count)", required = true)
            @RequestParam String benefitType
    ) {
        Long userId = SecurityUtils.getUserId();
        return vipMemberService.checkMemberBenefit(userId, benefitType);
    }

    @PostMapping("/use-analysis")
    @Operation(summary = "8. 使用解析次数")
    public Boolean useAnalysisCount(
            @Parameter(description = "使用次数", required = true) @RequestParam Integer count
    ) {
        Long userId = SecurityUtils.getUserId();
        return vipMemberService.useAnalysisCount(userId, count);
    }

    @PostMapping("/downgrade")
    @Operation(summary = "9. 降级会员等级")
    public Boolean downgradeMemberLevel(
            @Parameter(description = "目标会员等级ID", required = true) @RequestParam Long targetLevelId
    ) {
        Long userId = SecurityUtils.getUserId();
        return vipMemberService.downgradeMemberLevel(userId, targetLevelId);
    }

    @GetMapping("/admin/expiring")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "10. (管理员)获取即将到期的会员列表")
    public List<VipMember> getExpiringMembers(
            @Parameter(description = "到期前天数", required = true) @RequestParam int daysBeforeExpiry
    ) {
        return vipMemberService.getExpiringMembers(daysBeforeExpiry);
    }

    @PostMapping("/admin/handle-expired")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "11. (管理员)处理所有已到期的会员")
    public Boolean handleExpiredMembers() {
        return vipMemberService.handleExpiredMembers();
    }

    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "12. (管理员)分页查询会员列表")
    public IPage<VipMember> getMemberPage(
            @Parameter(description = "分页信息") Page<VipMember> page,
            @Parameter(description = "会员状态") @RequestParam(required = false) String status,
            @Parameter(description = "会员等级ID") @RequestParam(required = false) Long levelId
    ) {
        return vipMemberService.getMemberPage(page, status, levelId);
    }
}
