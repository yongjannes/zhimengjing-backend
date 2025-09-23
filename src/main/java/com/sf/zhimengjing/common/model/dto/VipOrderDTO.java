package com.sf.zhimengjing.common.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Title: VipOrderDTO
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.common.model.dto
 * @Description: VIP订单相关的数据传输对象，包括订单详情、创建订单请求、支付请求等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "VIP订单DTO")
public class VipOrderDTO implements Serializable {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "VIP等级ID")
    private Long levelId;

    @Schema(description = "VIP等级名称")
    private String levelName;

    @Schema(description = "订单类型（月卡/季卡/年卡）")
    private String orderType;

    @Schema(description = "购买时长（月）")
    private Integer durationMonths;

    @Schema(description = "原始金额")
    private BigDecimal originalAmount;

    @Schema(description = "应付金额")
    private BigDecimal payableAmount;

    @Schema(description = "实付金额")
    private BigDecimal paidAmount;

    @Schema(description = "折扣金额")
    private BigDecimal discountAmount;

    @Schema(description = "优惠券ID")
    private Long couponId;

    @Schema(description = "优惠券代码")
    private String couponCode;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "支付状态")
    private String paymentStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @Schema(description = "支付交易流水号")
    private String paymentTransactionId;

    @Schema(description = "退款状态")
    private String refundStatus;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "退款时间")
    private LocalDateTime refundTime;

    @Schema(description = "退款原因")
    private String refundReason;

    @Schema(description = "订单状态")
    private String orderStatus;

    @Schema(description = "是否自动续费")
    private Boolean autoRenew;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    // ---------------- 内部 DTO ----------------

    /**
     * @Title: CreateOrderDTO
     * @Author: 殇枫
     * @Description: 创建VIP订单请求DTO
     */
    @Data
    @Schema(description = "创建订单请求DTO")
    public static class CreateOrderDTO implements Serializable {

        @NotNull(message = "VIP等级ID不能为空")
        @Schema(description = "VIP等级ID", required = true)
        private Long levelId;

        @NotBlank(message = "订单类型不能为空")
        @Schema(description = "订单类型（月卡/季卡/年卡）", required = true)
        private String orderType;

        @NotNull(message = "购买时长不能为空")
        @Min(value = 1, message = "购买时长必须大于0")
        @Schema(description = "购买时长（月）", required = true)
        private Integer durationMonths;

        @Schema(description = "优惠券ID，可选")
        private Long couponId;

        @Schema(description = "是否自动续费，可选")
        private Boolean autoRenew;
    }

    /**
     * @Title: PaymentDTO
     * @Author: 殇枫
     * @Description: 支付请求DTO
     */
    @Data
    @Schema(description = "支付请求DTO")
    public static class PaymentDTO implements Serializable {

        @NotBlank(message = "支付方式不能为空")
        @Schema(description = "支付方式", required = true)
        private String paymentMethod;

        @Schema(description = "支付成功回调URL，可选")
        private String returnUrl;

        @Schema(description = "支付异步通知URL，可选")
        private String notifyUrl;
    }
}
