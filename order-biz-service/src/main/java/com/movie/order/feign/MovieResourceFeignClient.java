package com.movie.order.feign;

import com.movie.common.dto.ScheduleDTO;
import com.movie.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 影片资源服务Feign客户端
 */
@FeignClient(name = "movie-resource-service", path = "/schedule", fallback = MovieResourceFeignClientFallback.class)
public interface MovieResourceFeignClient {
    
    /**
     * 根据ID查询场次详情
     */
    @GetMapping("/{id}")
    Result<ScheduleDTO> getScheduleById(@PathVariable("id") Long id);
    
    /**
     * 增加已售座位数
     */
    @PutMapping("/{id}/increase-sold-seats")
    Result<Void> increaseSoldSeats(@PathVariable("id") Long id, @RequestParam("count") Integer count);
    
    /**
     * 减少已售座位数
     */
    @PutMapping("/{id}/decrease-sold-seats")
    Result<Void> decreaseSoldSeats(@PathVariable("id") Long id, @RequestParam("count") Integer count);
}
