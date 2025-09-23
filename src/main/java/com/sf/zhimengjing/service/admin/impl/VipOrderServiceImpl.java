package com.sf.zhimengjing.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.zhimengjing.common.exception.GeneralBusinessException;
import com.sf.zhimengjing.common.model.dto.VipOrderDTO;
import com.sf.zhimengjing.entity.admin.VipLevel;
import com.sf.zhimengjing.entity.admin.VipOrder;
import com.sf.zhimengjing.mapper.admin.VipOrderMapper;
import com.sf.zhimengjing.service.admin.VipLevelService;
import com.sf.zhimengjing.service.admin.VipOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title: VipOrderServiceImpl
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin.impl
 * @Description: VIP订单服务实现类，提供VIP订单相关业务逻辑
 */
@Service
@RequiredArgsConstructor
public class VipOrderServiceImpl extends ServiceImpl<VipOrderMapper, VipOrder> implements VipOrderService {

    private final VipLevelService vipLevelService;

    /** 创建VIP订单 */
    @Override
    @Transactional
    public VipOrderDTO createOrder(VipOrderDTO.CreateOrderDTO createDTO, Long userId) {
        VipLevel level = vipLevelService.getById(createDTO.getLevelId());
        if (level == null) {
            throw new GeneralBusinessException("VIP等级不存在");
        }

        VipOrder order = new VipOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setLevelId(createDTO.getLevelId());
        order.setOrderType(createDTO.getOrderType());
        order.setDurationMonths(createDTO.getDurationMonths());
        order.setCouponId(createDTO.getCouponId());
        order.setAutoRenew(createDTO.getAutoRenew());

        // 计算价格
        BigDecimal originalAmount = calculateOriginalAmount(level, createDTO.getDurationMonths());
        order.setOriginalAmount(originalAmount);
        order.setPayableAmount(originalAmount);
        order.setPaidAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPaymentStatus("pending");
        order.setOrderStatus("pending");

        this.save(order);

        return convertToDTO(order);
    }

    /** 获取订单详情 */
    @Override
    public VipOrderDTO getOrderDetail(Long orderId) {
        VipOrder order = this.getById(orderId);
        return convertToDTO(order);
    }

    /** 获取订单详情（根据订单号） */
    @Override
    public VipOrderDTO getOrderByOrderNo(String orderNo) {
        VipOrder order = this.getOne(new LambdaQueryWrapper<VipOrder>().eq(VipOrder::getOrderNo, orderNo));
        return convertToDTO(order);
    }

    /** 获取用户订单列表 */
    @Override
    public IPage<VipOrderDTO> getUserOrders(Long userId, int page, int size, String status) {
        Page<VipOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<VipOrder> wrapper = new LambdaQueryWrapper<VipOrder>()
                .eq(VipOrder::getUserId, userId)
                .eq(status != null, VipOrder::getOrderStatus, status)
                .orderByDesc(VipOrder::getCreateTime);

        IPage<VipOrder> orderPage = this.page(pageParam, wrapper);
        return orderPage.convert(this::convertToDTO);
    }

    /** 处理订单支付 */
    @Override
    @Transactional
    public Map<String, String> processPayment(Long orderId, VipOrderDTO.PaymentDTO paymentDTO) {
        VipOrder order = this.getById(orderId);
        if (order == null) {
            throw new GeneralBusinessException("订单不存在");
        }

        if (!"pending".equals(order.getOrderStatus())) {
            throw new GeneralBusinessException("订单状态异常");
        }

        // 更新支付信息
        order.setPaymentMethod(paymentDTO.getPaymentMethod());
        order.setPaymentStatus("processing");
        this.updateById(order);

        // 这里应该调用具体的支付接口
        Map<String, String> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("paymentUrl", "https://payment.example.com/pay?orderNo=" + order.getOrderNo());
        result.put("qrCode", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");

        return result;
    }

    /** 处理支付回调 */
    @Override
    @Transactional
    public boolean handlePaymentCallback(String paymentMethod, Map<String, String> callbackData) {
        String orderNo = callbackData.get("orderNo");
        String paymentStatus = callbackData.get("paymentStatus");
        String transactionId = callbackData.get("transactionId");

        VipOrder order = this.getOne(new LambdaQueryWrapper<VipOrder>().eq(VipOrder::getOrderNo, orderNo));
        if (order == null) {
            return false;
        }

        if ("success".equals(paymentStatus)) {
            order.setPaymentStatus("success");
            order.setOrderStatus("paid");
            order.setPaymentTime(LocalDateTime.now());
            order.setPaymentTransactionId(transactionId);
            order.setPaidAmount(order.getPayableAmount());
        } else {
            order.setPaymentStatus("failed");
            order.setOrderStatus("cancelled");
        }

        return this.updateById(order);
    }

    /** 取消订单 */
    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, String reason) {
        VipOrder order = this.getById(orderId);
        if (order == null) {
            throw new GeneralBusinessException("订单不存在");
        }

        if (!"pending".equals(order.getOrderStatus())) {
            throw new GeneralBusinessException("订单状态不允许取消");
        }

        order.setOrderStatus("cancelled");
        return this.updateById(order);
    }

    /** 申请退款 */
    @Override
    @Transactional
    public boolean applyRefund(Long orderId, BigDecimal refundAmount, String reason) {
        VipOrder order = this.getById(orderId);
        if (order == null) {
            throw new GeneralBusinessException("订单不存在");
        }

        if (!"paid".equals(order.getOrderStatus())) {
            throw new GeneralBusinessException("订单状态不允许退款");
        }

        order.setRefundStatus("refunding");
        order.setRefundAmount(refundAmount);
        order.setRefundReason(reason);

        return this.updateById(order);
    }

    /** 处理退款 */
    @Override
    @Transactional
    public boolean processRefund(Long orderId, String refundStatus, String refundReason) {
        VipOrder order = this.getById(orderId);
        if (order == null) {
            throw new GeneralBusinessException("订单不存在");
        }

        order.setRefundStatus(refundStatus);
        if ("success".equals(refundStatus)) {
            order.setRefundTime(LocalDateTime.now());
            order.setOrderStatus("refunded");
        }

        return this.updateById(order);
    }

    /** 获取订单统计 */
    @Override
    public Map<String, Object> getOrderStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> stats = new HashMap<>();

        LambdaQueryWrapper<VipOrder> baseWrapper = new LambdaQueryWrapper<VipOrder>()
                .ge(startDate != null, VipOrder::getCreateTime, startDate.atStartOfDay())
                .le(endDate != null, VipOrder::getCreateTime, endDate.plusDays(1).atStartOfDay());

        // 总订单数
        long totalOrders = this.count(baseWrapper);
        stats.put("totalOrders", totalOrders);

        // 已支付订单数
        long paidOrders = this.count(baseWrapper.clone().eq(VipOrder::getOrderStatus, "paid"));
        stats.put("paidOrders", paidOrders);

        // 待支付订单数
        long pendingOrders = this.count(baseWrapper.clone().eq(VipOrder::getOrderStatus, "pending"));
        stats.put("pendingOrders", pendingOrders);

        // 已取消订单数
        long cancelledOrders = this.count(baseWrapper.clone().eq(VipOrder::getOrderStatus, "cancelled"));
        stats.put("cancelledOrders", cancelledOrders);

        // 支付成功率
        double successRate = totalOrders > 0 ? (double) paidOrders / totalOrders * 100 : 0.0;
        stats.put("successRate", successRate);

        return stats;
    }

    /** 分页查询订单列表 */
    @Override
    public IPage<VipOrderDTO> getOrderPage(Page<VipOrderDTO> page, String orderNo, String status, Long userId) {
        LambdaQueryWrapper<VipOrder> wrapper = new LambdaQueryWrapper<VipOrder>()
                .like(orderNo != null, VipOrder::getOrderNo, orderNo)
                .eq(status != null, VipOrder::getOrderStatus, status)
                .eq(userId != null, VipOrder::getUserId, userId)
                .orderByDesc(VipOrder::getCreateTime);

        IPage<VipOrder> orderPage = this.page(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        return orderPage.convert(this::convertToDTO);
    }

    /** 计算原价金额 */
    private BigDecimal calculateOriginalAmount(VipLevel level, Integer durationMonths) {
        if (durationMonths <= 1) {
            return level.getMonthlyPrice();
        } else if (durationMonths <= 3) {
            return level.getQuarterlyPrice();
        } else if (durationMonths <= 12) {
            return level.getYearlyPrice();
        } else {
            // 超过12个月按年费计算
            return level.getYearlyPrice().multiply(BigDecimal.valueOf(durationMonths / 12));
        }
    }

    /** 生成订单号 */
    private String generateOrderNo() {
        return "VIP" + System.currentTimeMillis();
    }

    /** 实体转换 DTO */
    private VipOrderDTO convertToDTO(VipOrder entity) {
        if (entity == null) return null;

        VipOrderDTO dto = new VipOrderDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}