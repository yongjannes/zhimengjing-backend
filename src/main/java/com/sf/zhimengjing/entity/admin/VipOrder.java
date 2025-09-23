package com.sf.zhimengjing.entity.admin;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sf.zhimengjing.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Title: VipOrder
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.entity.admin
 * @Description: VIP订单实体类，用于存储VIP购买订单信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vip_orders")
public class VipOrder extends BaseEntity {

    /** 订单号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** VIP等级ID */
    private Long levelId;

    /** 订单类型 */
    private String orderType;

    /** 购买时长(月) */
    private Integer durationMonths;

    /** 原价金额 */
    private BigDecimal originalAmount;

    /** 应付金额 */
    private BigDecimal payableAmount;

    /** 实付金额 */
    private BigDecimal paidAmount;

    /** 优惠金额 */
    private BigDecimal discountAmount;

    /** 优惠券ID */
    private Long couponId;

    /** 支付方式 */
    private String paymentMethod;

    /** 支付状态 */
    private String paymentStatus;

    /** 支付时间 */
    private LocalDateTime paymentTime;

    /** 支付交易ID */
    private String paymentTransactionId;

    /** 退款状态 */
    private String refundStatus;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 退款时间 */
    private LocalDateTime refundTime;

    /** 退款原因 */
    private String refundReason;

    /** 订单状态 */
    private String orderStatus;

    /** 是否自动续费 */
    private Boolean autoRenew;

    /** 续费提醒已发送 */
    private Boolean renewRemindSent;

    /** 到期提醒已发送 */
    private Boolean expiredRemindSent;

    /** 删除标志（逻辑删除：0-正常，1-已删除） */
    @TableLogic
    private Integer deleteFlag;
}