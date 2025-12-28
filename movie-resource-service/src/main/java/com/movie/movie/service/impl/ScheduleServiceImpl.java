package com.movie.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.common.exception.BusinessException;
import com.movie.common.result.ResultCode;
import com.movie.movie.entity.Schedule;
import com.movie.movie.mapper.ScheduleMapper;
import com.movie.movie.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 场次服务实现
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {
    
    @Override
    public List<Schedule> getSchedulesByMovieId(Long movieId) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getMovieId, movieId)
                .ge(Schedule::getShowTime, LocalDateTime.now())
                .orderByAsc(Schedule::getShowTime);
        return list(wrapper);
    }
    
    @Override
    public List<Schedule> getSchedulesByCinemaId(Long cinemaId) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getCinemaId, cinemaId)
                .ge(Schedule::getShowTime, LocalDateTime.now())
                .orderByAsc(Schedule::getShowTime);
        return list(wrapper);
    }
    
    @Override
    public Schedule getScheduleById(Long id) {
        Schedule schedule = getById(id);
        if (schedule == null) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_FOUND);
        }
        return schedule;
    }
    
    @Override
    public void increaseSoldSeats(Long scheduleId, Integer count) {
        LambdaUpdateWrapper<Schedule> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Schedule::getId, scheduleId)
                .setSql("sold_seats = sold_seats + " + count);
        update(wrapper);
    }
    
    @Override
    public void decreaseSoldSeats(Long scheduleId, Integer count) {
        LambdaUpdateWrapper<Schedule> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Schedule::getId, scheduleId)
                .setSql("sold_seats = sold_seats - " + count)
                .set(Schedule::getUpdateTime, LocalDateTime.now());
        update(wrapper);
    }
}

