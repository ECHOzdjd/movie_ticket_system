package com.movie.movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.movie.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 场次服务接口
 */
public interface ScheduleService extends IService<Schedule> {
    
    /**
     * 根据影片ID查询场次
     */
    List<Schedule> getSchedulesByMovieId(Long movieId);
    
    /**
     * 根据影院ID查询场次
     */
    List<Schedule> getSchedulesByCinemaId(Long cinemaId);
    
    /**
     * 根据ID查询场次详情
     */
    Schedule getScheduleById(Long id);
    
    /**
     * 增加已售座位数
     */
    void increaseSoldSeats(Long scheduleId, Integer count);
    
    /**
     * 减少已售座位数
     */
    void decreaseSoldSeats(Long scheduleId, Integer count);
}

