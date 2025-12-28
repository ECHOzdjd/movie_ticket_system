package com.movie.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.common.exception.BusinessException;
import com.movie.common.result.ResultCode;
import com.movie.movie.entity.Movie;
import com.movie.movie.mapper.MovieMapper;
import com.movie.movie.service.MovieService;
import org.springframework.stereotype.Service;

/**
 * 影片服务实现
 */
@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, Movie> implements MovieService {
    
    @Override
    public Page<Movie> getReleasedMovies(Page<Movie> page) {
        LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Movie::getIsReleased, true)
                .orderByDesc(Movie::getReleaseDate);
        return page(page, wrapper);
    }
    
    @Override
    public Movie getMovieById(Long id) {
        Movie movie = getById(id);
        if (movie == null) {
            throw new BusinessException(ResultCode.MOVIE_NOT_FOUND);
        }
        return movie;
    }
}

