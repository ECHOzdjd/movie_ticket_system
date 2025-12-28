package com.movie.order.feign;

import com.movie.common.dto.ScheduleDTO;
import com.movie.common.result.Result;
import com.movie.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 影片资源服务Feign客户端降级处理
 */
@Slf4j
@Component
public class MovieResourceFeignClientFallback implements MovieResourceFeignClient {
    
    @Override
    public Result<ScheduleDTO> getScheduleById(Long id) {
        log.error("调用影片资源服务失败，执行降级处理，scheduleId: {}", id);
        return Result.fail(ResultCode.SCHEDULE_NOT_FOUND.getCode(), "服务暂时不可用，请稍后重试");
    }
    
    @Override
    public Result<Void> increaseSoldSeats(Long id, Integer count) {
        log.error("调用影片资源服务失败，执行降级处理，scheduleId: {}, count: {}", id, count);
        return Result.fail(500, "服务暂时不可用，请稍后重试");
    }
    
    @Override
    public Result<Void> decreaseSoldSeats(Long id, Integer count) {
        log.error("调用影片资源服务失败，执行降级处理，scheduleId: {}, count: {}", id, count);
        return Result.fail(500, "服务暂时不可用，请稍后重试");
    }
}

