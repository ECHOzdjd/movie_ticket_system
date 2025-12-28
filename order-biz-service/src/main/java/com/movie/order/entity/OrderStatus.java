package com.movie.order.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {
    
    PENDING(0, "待支付"),
    COMPLETED(1, "已完成"),
    CANCELED(2, "已取消");
    
    private final Integer code;
    private final String desc;
}

