package com.movie.order.service.impl;

import com.movie.common.constant.CommonConstants;
import com.movie.common.dto.ScheduleDTO;
import com.movie.common.exception.BusinessException;
import com.movie.common.result.ResultCode;
import com.movie.order.feign.MovieResourceFeignClient;
import com.movie.order.service.SeatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 座位服务实现
 */
@Slf4j
@Service
public class SeatServiceImpl implements SeatService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private MovieResourceFeignClient movieResourceFeignClient;
    
    @Override
    public Map<String, Object> getSeatMap(Long scheduleId) {
        // 查询场次信息
        var scheduleResult = movieResourceFeignClient.getScheduleById(scheduleId);
        if (scheduleResult.getCode() != 200 || scheduleResult.getData() == null) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_FOUND);
        }
        
        ScheduleDTO schedule = scheduleResult.getData();
        int totalSeats = schedule.getTotalSeats();
        int soldSeats = schedule.getSoldSeats();
        
        // 从Redis获取锁定的座位
        String lockKey = CommonConstants.SEAT_LOCK_PREFIX + scheduleId;
        Set<String> lockedSeats = redisTemplate.keys(lockKey + ":*");
        Set<String> lockedSeatNumbers = new HashSet<>();
        if (lockedSeats != null) {
            for (String key : lockedSeats) {
                String seatNumber = key.substring(key.lastIndexOf(":") + 1);
                lockedSeatNumbers.add(seatNumber);
            }
        }
        
        // 构建座位图（简化版本，实际应该从数据库或配置中获取座位布局）
        Map<String, Object> seatMap = new HashMap<>();
        seatMap.put("scheduleId", scheduleId);
        seatMap.put("totalSeats", totalSeats);
        seatMap.put("soldSeats", soldSeats);
        seatMap.put("availableSeats", totalSeats - soldSeats - lockedSeatNumbers.size());
        seatMap.put("lockedSeats", lockedSeatNumbers);
        
        return seatMap;
    }
    
    @Override
    public Boolean lockSeats(Long scheduleId, List<String> seatNumbers) {
        // 检查座位是否可用
        if (!checkSeatsAvailable(scheduleId, seatNumbers)) {
            throw new BusinessException(ResultCode.SEAT_ALREADY_LOCKED);
        }
        
        // 使用分布式锁锁定座位
        String lockKey = CommonConstants.SEAT_LOCK_PREFIX + scheduleId;
        for (String seatNumber : seatNumbers) {
            String seatLockKey = lockKey + ":" + seatNumber;
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(seatLockKey, "locked", CommonConstants.SEAT_LOCK_EXPIRE_TIME, TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(success)) {
                // 如果有一个座位锁定失败，释放已锁定的座位
                unlockSeats(scheduleId, seatNumbers.subList(0, seatNumbers.indexOf(seatNumber)));
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public void unlockSeats(Long scheduleId, List<String> seatNumbers) {
        String lockKey = CommonConstants.SEAT_LOCK_PREFIX + scheduleId;
        for (String seatNumber : seatNumbers) {
            String seatLockKey = lockKey + ":" + seatNumber;
            redisTemplate.delete(seatLockKey);
        }
    }
    
    @Override
    public Boolean checkSeatsAvailable(Long scheduleId, List<String> seatNumbers) {
        // 查询场次信息
        var scheduleResult = movieResourceFeignClient.getScheduleById(scheduleId);
        if (scheduleResult.getCode() != 200 || scheduleResult.getData() == null) {
            return false;
        }
        
        var schedule = scheduleResult.getData();
        int totalSeats = schedule.getTotalSeats();
        int soldSeats = schedule.getSoldSeats();
        
        // 检查座位号是否有效（简化版本，实际应该验证座位号格式和范围）
        if (seatNumbers.size() > totalSeats - soldSeats) {
            return false;
        }
        
        // 检查座位是否已被锁定或已售
        String lockKey = CommonConstants.SEAT_LOCK_PREFIX + scheduleId;
        for (String seatNumber : seatNumbers) {
            String seatLockKey = lockKey + ":" + seatNumber;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(seatLockKey))) {
                return false;
            }
        }
        
        return true;
    }
}

