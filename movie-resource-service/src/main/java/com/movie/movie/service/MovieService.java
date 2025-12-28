package com.movie.movie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.movie.entity.Movie;

/**
 * 影片服务接口
 */
public interface MovieService extends IService<Movie> {
    
    /**
     * 分页查询上映影片
     */
    Page<Movie> getReleasedMovies(Page<Movie> page);
    
    /**
     * 根据ID查询影片详情
     */
    Movie getMovieById(Long id);
}

