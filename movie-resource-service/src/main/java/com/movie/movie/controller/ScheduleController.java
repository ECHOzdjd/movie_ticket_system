package com.movie.movie.controller;

import com.movie.common.dto.ScheduleDTO;
import com.movie.common.result.Result;
import com.movie.movie.entity.Schedule;
import com.movie.movie.service.ScheduleService;
import org.springframework.beans.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 场次控制器
 */
@Slf4j
@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    
    @Autowired
    private ScheduleService scheduleService;
    
    /**
     * 根据影片ID查询场次
     */
    @GetMapping("/movie/{movieId}")
    public Result<List<Schedule>> getSchedulesByMovieId(@PathVariable Long movieId) {
        List<Schedule> schedules = scheduleService.getSchedulesByMovieId(movieId);
        return Result.success(schedules);
    }
    
    /**
     * 根据影院ID查询场次
     */
    @GetMapping("/cinema/{cinemaId}")
    public Result<List<Schedule>> getSchedulesByCinemaId(@PathVariable Long cinemaId) {
        List<Schedule> schedules = scheduleService.getSchedulesByCinemaId(cinemaId);
        return Result.success(schedules);
    }
    
    /**
     * 根据ID查询场次详情
     */
    @GetMapping("/{id}")
    public Result<ScheduleDTO> getScheduleById(@PathVariable Long id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        ScheduleDTO dto = new ScheduleDTO();
        BeanUtils.copyProperties(schedule, dto);
        return Result.success(dto);
    }
    
    /**
     * 增加已售座位数
     */
    @PutMapping("/{id}/increase-sold-seats")
    public Result<Void> increaseSoldSeats(@PathVariable Long id, @RequestParam Integer count) {
        scheduleService.increaseSoldSeats(id, count);
        return Result.success();
    }
    
    /**
     * 减少已售座位数
     */
    @PutMapping("/{id}/decrease-sold-seats")
    public Result<Void> decreaseSoldSeats(@PathVariable Long id, @RequestParam Integer count) {
        scheduleService.decreaseSoldSeats(id, count);
        return Result.success();
    }
}

