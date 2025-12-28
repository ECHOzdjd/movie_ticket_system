package com.movie.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.order.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {
    
    /**
     * 创建订单
     */
    Order createOrder(Long userId, Long scheduleId, List<String> seatNumbers);
    
    /**
     * 支付订单
     */
    void payOrder(String orderNo);
    
    /**
     * 取消订单
     */
    void cancelOrder(String orderNo);
    
    /**
     * 根据订单号查询订单
     */
    Order getOrderByOrderNo(String orderNo);
    
    /**
     * 根据用户ID查询订单列表
     */
    List<Order> getOrdersByUserId(Long userId);
    
    /**
     * 取消过期订单（定时任务调用）
     */
    void cancelExpiredOrders();
}

