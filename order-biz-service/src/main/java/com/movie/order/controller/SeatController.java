package com.movie.order.controller;

import com.movie.common.result.Result;
import com.movie.order.service.SeatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 座位控制器
 */
@Slf4j
@RestController
@RequestMapping("/seat")
public class SeatController {
    
    @Autowired
    private SeatService seatService;
    
    /**
     * 查询场次的座位图
     */
    @GetMapping("/map/{scheduleId}")
    public Result<Map<String, Object>> getSeatMap(@PathVariable Long scheduleId) {
        Map<String, Object> seatMap = seatService.getSeatMap(scheduleId);
        return Result.success(seatMap);
    }
    
    /**
     * 锁定座位
     */
    @PostMapping("/lock")
    public Result<Boolean> lockSeats(@RequestBody LockSeatRequest request) {
        Boolean success = seatService.lockSeats(request.getScheduleId(), request.getSeatNumbers());
        return Result.success(success);
    }
    
    /**
     * 锁定座位请求
     */
    public static class LockSeatRequest {
        private Long scheduleId;
        private List<String> seatNumbers;
        
        // Getters and Setters
        public Long getScheduleId() { return scheduleId; }
        public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
        public List<String> getSeatNumbers() { return seatNumbers; }
        public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }
    }
}

