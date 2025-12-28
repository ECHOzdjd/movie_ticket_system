package com.movie.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.common.constant.CommonConstants;
import com.movie.common.dto.ScheduleDTO;
import com.movie.common.exception.BusinessException;
import com.movie.common.result.ResultCode;
import com.movie.order.entity.Order;
import com.movie.order.entity.OrderStatus;
import com.movie.order.feign.MovieResourceFeignClient;
import com.movie.order.mapper.OrderMapper;
import com.movie.order.service.OrderService;
import com.movie.order.service.SeatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    @Autowired
    private SeatService seatService;
    
    @Autowired
    private MovieResourceFeignClient movieResourceFeignClient;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long userId, Long scheduleId, List<String> seatNumbers) {
        // 查询场次信息
        var scheduleResult = movieResourceFeignClient.getScheduleById(scheduleId);
        if (scheduleResult.getCode() != 200 || scheduleResult.getData() == null) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_FOUND);
        }
        
        ScheduleDTO schedule = scheduleResult.getData();
        
        // 锁定座位
        Boolean lockSuccess = seatService.lockSeats(scheduleId, seatNumbers);
        if (!lockSuccess) {
            throw new BusinessException(ResultCode.SEAT_ALREADY_LOCKED);
        }
        
        try {
            // 计算订单金额
            BigDecimal totalAmount = schedule.getPrice().multiply(BigDecimal.valueOf(seatNumbers.size()));
            
            // 创建订单
            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setUserId(userId);
            order.setScheduleId(scheduleId);
            try {
                order.setSeats(objectMapper.writeValueAsString(seatNumbers));
            } catch (JsonProcessingException e) {
                log.error("序列化座位信息失败", e);
                throw new BusinessException("创建订单失败");
            }
            order.setSeatCount(seatNumbers.size());
            order.setTotalAmount(totalAmount);
            order.setStatus(OrderStatus.PENDING.getCode());
            order.setCreateTime(LocalDateTime.now());
            order.setExpireTime(LocalDateTime.now().plusSeconds(CommonConstants.ORDER_EXPIRE_TIME));
            order.setUpdateTime(LocalDateTime.now());
            
            save(order);
            return order;
        } catch (Exception e) {
            // 如果创建订单失败，释放座位锁
            seatService.unlockSeats(scheduleId, seatNumbers);
            throw e;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(String orderNo) {
        Order order = getOrderByOrderNo(orderNo);
        if (order.getStatus() != OrderStatus.PENDING.getCode()) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        
        // 更新订单状态
        order.setStatus(OrderStatus.COMPLETED.getCode());
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        updateById(order);
        
        // 增加场次的已售座位数
        movieResourceFeignClient.increaseSoldSeats(order.getScheduleId(), order.getSeatCount());
        
        // 释放座位锁（因为已经支付，座位不再需要锁定）
        try {
            List<String> seatNumbers = objectMapper.readValue(order.getSeats(), List.class);
            seatService.unlockSeats(order.getScheduleId(), seatNumbers);
        } catch (Exception e) {
            log.error("释放座位锁失败", e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo) {
        Order order = getOrderByOrderNo(orderNo);
        if (order.getStatus() != OrderStatus.PENDING.getCode()) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        
        // 更新订单状态
        order.setStatus(OrderStatus.CANCELED.getCode());
        order.setUpdateTime(LocalDateTime.now());
        updateById(order);
        
        // 释放座位锁
        try {
            List<String> seatNumbers = objectMapper.readValue(order.getSeats(), List.class);
            seatService.unlockSeats(order.getScheduleId(), seatNumbers);
        } catch (Exception e) {
            log.error("释放座位锁失败", e);
        }
    }
    
    @Override
    public Order getOrderByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = getOne(wrapper);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        return order;
    }
    
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime);
        return list(wrapper);
    }
    
    @Override
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    @Transactional(rollbackFor = Exception.class)
    public void cancelExpiredOrders() {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getStatus, OrderStatus.PENDING.getCode())
                .le(Order::getExpireTime, LocalDateTime.now());
        
        List<Order> expiredOrders = list(wrapper);
        for (Order order : expiredOrders) {
            log.info("自动取消过期订单: {}", order.getOrderNo());
            cancelOrder(order.getOrderNo());
        }
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

