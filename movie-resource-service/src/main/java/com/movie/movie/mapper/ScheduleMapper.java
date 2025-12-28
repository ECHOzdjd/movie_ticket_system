package com.movie.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.movie.movie.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 场次Mapper
 */
@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {
}

