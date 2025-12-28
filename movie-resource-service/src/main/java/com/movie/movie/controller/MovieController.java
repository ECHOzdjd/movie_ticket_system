package com.movie.movie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.common.result.Result;
import com.movie.movie.entity.Movie;
import com.movie.movie.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 影片控制器
 */
@Slf4j
@RestController
@RequestMapping("/movie")
public class MovieController {
    
    @Autowired
    private MovieService movieService;
    
    /**
     * 分页查询上映影片
     */
    @GetMapping("/list")
    public Result<Page<Movie>> listReleasedMovies(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<Movie> page = new Page<>(current, size);
        Page<Movie> result = movieService.getReleasedMovies(page);
        return Result.success(result);
    }
    
    /**
     * 根据ID查询影片详情
     */
    @GetMapping("/{id}")
    public Result<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        return Result.success(movie);
    }
}

