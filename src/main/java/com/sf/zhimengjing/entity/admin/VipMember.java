package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Title: VipMember
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: VIP会员实体类，用于存储用户VIP会员信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vip_members")
public class VipMember extends BaseEntity {

    /** 用户ID */
    private Long userId;

    /** VIP等级ID */
    private Long levelId;

    /** 会员编号 */
    private String membershipNo;

    /** 会员状态 */
    private String status;

    /** 会员开始日期 */
    private LocalDate startDate;

    /** 会员到期日期 */
    private LocalDate expireDate;

    /** 是否自动续费 */
    private Boolean autoRenew;

    /** 总支付金额 */
    private BigDecimal totalPaidAmount;

    /** 剩余解析次数 */
    private Integer remainingAnalysisCount;

    /** 已使用解析次数 */
    private Integer usedAnalysisCount;

    /** 升级时间 */
    private LocalDateTime upgradeDate;

    /** 降级时间 */
    private LocalDateTime downgradeDate;

    /** 最后支付时间 */
    private LocalDateTime lastPaymentDate;

    /** 删除标志（逻辑删除：0-正常，1-已删除） */
    @TableLogic
    private Integer deleteFlag;
}