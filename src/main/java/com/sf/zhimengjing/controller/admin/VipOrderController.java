package com.sf.zhimengjing.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.zhimengjing.common.model.dto.VipOrderDTO;
import com.sf.zhimengjing.common.util.SecurityUtils;
import com.sf.zhimengjing.service.admin.VipOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * @Title: VipLevelController
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.controller.admin
 * @description: VIP订单管理控制器，提供订单的创建、查询、支付、取消、退款及统计接口
 */
@RestController
@RequestMapping("/api/vip/orders")
@RequiredArgsConstructor
@Tag(name = "VIP订单管理接口")
public class VipOrderController {

    private final VipOrderService vipOrderService;

    /** 1. 创建VIP订单 */
    @PostMapping
    @Operation(summary = "1. 创建VIP订单")
    public VipOrderDTO createOrder(@Parameter(description = "创建订单请求数据")
                                   @Valid @RequestBody VipOrderDTO.CreateOrderDTO createDTO) {
        Long userId = SecurityUtils.getUserId();
        return vipOrderService.createOrder(createDTO, userId);
    }

    /** 2. 获取订单详情 */
    @GetMapping("/{orderId}")
    @Operation(summary = "2. 获取订单详情")
    public VipOrderDTO getOrderDetail(@Parameter(description = "订单ID") @PathVariable Long orderId) {
        return vipOrderService.getOrderDetail(orderId);
    }

    /** 3. 根据订单号获取订单详情 */
    @GetMapping("/by-order-no/{orderNo}")
    @Operation(summary = "3. 根据订单号获取订单详情")
    public VipOrderDTO getOrderByOrderNo(@Parameter(description = "订单号") @PathVariable String orderNo) {
        return vipOrderService.getOrderByOrderNo(orderNo);
    }

    /** 4. 获取我的订单列表 */
    @GetMapping("/my-orders")
    @Operation(summary = "4. 获取我的订单列表")
    public IPage<VipOrderDTO> getMyOrders(
            @Parameter(description = "页码，默认1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量，默认10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "订单状态，可选") @RequestParam(required = false) String status) {
        Long userId = SecurityUtils.getUserId();
        return vipOrderService.getUserOrders(userId, page, size, status);
    }

    /** 5. 处理订单支付 */
    @PostMapping("/{orderId}/payment")
    @Operation(summary = "5. 处理订单支付")
    public Map<String, String> processPayment(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "支付信息") @Valid @RequestBody VipOrderDTO.PaymentDTO paymentDTO) {
        return vipOrderService.processPayment(orderId, paymentDTO);
    }

    /** 6. 取消订单 */
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "6. 取消订单")
    public Boolean cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "取消原因，可选") @RequestParam(required = false) String reason) {
        return vipOrderService.cancelOrder(orderId, reason);
    }

    /** 7. 申请退款 */
    @PostMapping("/{orderId}/refund")
    @Operation(summary = "7. 申请退款")
    public Boolean applyRefund(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "退款金额") @RequestParam BigDecimal refundAmount,
            @Parameter(description = "退款原因") @RequestParam String reason) {
        return vipOrderService.applyRefund(orderId, refundAmount, reason);
    }

    /** 8. 获取订单统计 */
    @GetMapping("/statistics")
    @Operation(summary = "8. 获取订单统计")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getOrderStatistics(
            @Parameter(description = "开始日期，格式 yyyy-MM-dd")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期，格式 yyyy-MM-dd")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return vipOrderService.getOrderStatistics(startDate, endDate);
    }

    /** 9. 分页查询订单列表（后台管理） */
    @GetMapping("/page")
    @Operation(summary = "9. 分页查询订单列表（后台管理）")
    @PreAuthorize("hasRole('ADMIN')")
    public IPage<VipOrderDTO> getOrderPage(
            @Parameter(description = "页码，默认1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量，默认10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "订单号，可选") @RequestParam(required = false) String orderNo,
            @Parameter(description = "订单状态，可选") @RequestParam(required = false) String status,
            @Parameter(description = "用户ID，可选") @RequestParam(required = false) Long userId) {
        Page<VipOrderDTO> pageParam = new Page<>(page, size);
        return vipOrderService.getOrderPage(pageParam, orderNo, status, userId);
    }

    /** 10. 处理退款（后台管理） */
    @PostMapping("/{orderId}/process-refund")
    @Operation(summary = "10. 处理退款（后台管理）")
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean processRefund(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "退款状态 (例如 'success', 'failed')") @RequestParam String refundStatus,
            @Parameter(description = "退款原因或备注") @RequestParam(required = false) String refundReason) {
        return vipOrderService.processRefund(orderId, refundStatus, refundReason);
    }
}
