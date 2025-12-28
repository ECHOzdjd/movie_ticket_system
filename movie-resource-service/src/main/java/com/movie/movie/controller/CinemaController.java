package com.movie.movie.controller;

import com.movie.common.result.Result;
import com.movie.movie.entity.Cinema;
import com.movie.movie.service.CinemaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 影院控制器
 */
@Slf4j
@RestController
@RequestMapping("/cinema")
public class CinemaController {
    
    @Autowired
    private CinemaService cinemaService;
    
    /**
     * 查询所有影院
     */
    @GetMapping("/list")
    public Result<List<Cinema>> listCinemas() {
        List<Cinema> cinemas = cinemaService.getAllCinemas();
        return Result.success(cinemas);
    }
    
    /**
     * 根据ID查询影院
     */
    @GetMapping("/{id}")
    public Result<Cinema> getCinemaById(@PathVariable Long id) {
        Cinema cinema = cinemaService.getCinemaById(id);
        return Result.success(cinema);
    }
}

