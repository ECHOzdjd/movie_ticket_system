package com.movie.order.service;

import java.util.List;
import java.util.Map;

/**
 * 座位服务接口
 */
public interface SeatService {
    
    /**
     * 查询场次的座位图
     */
    Map<String, Object> getSeatMap(Long scheduleId);
    
    /**
     * 锁定座位
     */
    Boolean lockSeats(Long scheduleId, List<String> seatNumbers);
    
    /**
     * 释放座位锁
     */
    void unlockSeats(Long scheduleId, List<String> seatNumbers);
    
    /**
     * 检查座位是否可用
     */
    Boolean checkSeatsAvailable(Long scheduleId, List<String> seatNumbers);
}

