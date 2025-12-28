package com.movie.movie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.movie.common.exception.BusinessException;
import com.movie.common.result.ResultCode;
import com.movie.movie.entity.Cinema;
import com.movie.movie.mapper.CinemaMapper;
import com.movie.movie.service.CinemaService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 影院服务实现
 */
@Service
public class CinemaServiceImpl extends ServiceImpl<CinemaMapper, Cinema> implements CinemaService {
    
    @Override
    public List<Cinema> getAllCinemas() {
        return list();
    }
    
    @Override
    public Cinema getCinemaById(Long id) {
        Cinema cinema = getById(id);
        if (cinema == null) {
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }
        return cinema;
    }
}

