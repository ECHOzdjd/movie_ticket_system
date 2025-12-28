package com.movie.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.movie.movie.entity.Movie;
import org.apache.ibatis.annotations.Mapper;

/**
 * 影片Mapper
 */
@Mapper
public interface MovieMapper extends BaseMapper<Movie> {
}

