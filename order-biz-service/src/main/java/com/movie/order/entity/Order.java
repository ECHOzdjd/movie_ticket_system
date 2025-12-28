package com.movie.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("`order`")
public class Order {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 场次ID
     */
    private Long scheduleId;
    
    /**
     * 座位信息（JSON格式）
     */
    private String seats;
    
    /**
     * 座位数量
     */
    private Integer seatCount;
    
    /**
     * 订单金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 订单状态：0-待支付，1-已完成，2-已取消
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

