package com.movie.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.movie.movie.entity.Cinema;
import org.apache.ibatis.annotations.Mapper;

/**
 * 影院Mapper
 */
@Mapper
public interface CinemaMapper extends BaseMapper<Cinema> {
}

