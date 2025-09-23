package com.sf.zhimengjing.service.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.zhimengjing.common.model.dto.VipOrderDTO;
import com.sf.zhimengjing.entity.admin.VipOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * @Title: VipOrderService
 * @Author: 殇枫
 * @Package: com.sf.zhimengjing.service.admin
 * @Description: VIP订单服务接口，提供VIP订单相关业务操作
 */
public interface VipOrderService extends IService<VipOrder> {

    /** 创建VIP订单 */
    VipOrderDTO createOrder(VipOrderDTO.CreateOrderDTO createDTO, Long userId);

    /** 获取订单详情 */
    VipOrderDTO getOrderDetail(Long orderId);

    /** 获取订单详情（根据订单号） */
    VipOrderDTO getOrderByOrderNo(String orderNo);

    /** 获取用户订单列表 */
    IPage<VipOrderDTO> getUserOrders(Long userId, int page, int size, String status);

    /** 处理订单支付 */
    Map<String, String> processPayment(Long orderId, VipOrderDTO.PaymentDTO paymentDTO);

    /** 处理支付回调 */
    boolean handlePaymentCallback(String paymentMethod, Map<String, String> callbackData);

    /** 取消订单 */
    boolean cancelOrder(Long orderId, String reason);

    /** 申请退款 */
    boolean applyRefund(Long orderId, BigDecimal refundAmount, String reason);

    /** 处理退款 */
    boolean processRefund(Long orderId, String refundStatus, String refundReason);

    /** 获取订单统计 */
    Map<String, Object> getOrderStatistics(LocalDate startDate, LocalDate endDate);

    /** 分页查询订单列表 */
    IPage<VipOrderDTO> getOrderPage(Page<VipOrderDTO> page, String orderNo, String status, Long userId);
}