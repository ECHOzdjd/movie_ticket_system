package com.movie.movie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.movie.entity.Cinema;

import java.util.List;

/**
 * 影院服务接口
 */
public interface CinemaService extends IService<Cinema> {
    
    /**
     * 查询所有影院
     */
    List<Cinema> getAllCinemas();
    
    /**
     * 根据ID查询影院
     */
    Cinema getCinemaById(Long id);
}

