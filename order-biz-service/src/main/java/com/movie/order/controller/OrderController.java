package com.movie.order.controller;

import com.movie.common.result.Result;
import com.movie.order.entity.Order;
import com.movie.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<Order> createOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(userId, request.getScheduleId(), request.getSeatNumbers());
        return Result.success(order);
    }
    
    /**
     * 支付订单
     */
    @PostMapping("/pay/{orderNo}")
    public Result<Void> payOrder(@PathVariable String orderNo) {
        orderService.payOrder(orderNo);
        return Result.success();
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderNo}")
    public Result<Void> cancelOrder(@PathVariable String orderNo) {
        orderService.cancelOrder(orderNo);
        return Result.success();
    }
    
    /**
     * 根据订单号查询订单
     */
    @GetMapping("/{orderNo}")
    public Result<Order> getOrderByOrderNo(@PathVariable String orderNo) {
        Order order = orderService.getOrderByOrderNo(orderNo);
        return Result.success(order);
    }
    
    /**
     * 查询当前用户的订单列表
     */
    @GetMapping("/list")
    public Result<List<Order>> getOrderList(@RequestHeader("X-User-Id") Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return Result.success(orders);
    }
    
    /**
     * 创建订单请求
     */
    public static class CreateOrderRequest {
        private Long scheduleId;
        private List<String> seatNumbers;
        
        // Getters and Setters
        public Long getScheduleId() { return scheduleId; }
        public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
        public List<String> getSeatNumbers() { return seatNumbers; }
        public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }
    }
}

