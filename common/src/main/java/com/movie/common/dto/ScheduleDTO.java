package com.movie.common.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 场次DTO（用于服务间传输）
 */
@Data
public class ScheduleDTO {
    
    private Long id;
    
    private Long movieId;
    
    private Long cinemaId;
    
    private String hallName;
    
    private LocalDateTime showTime;
    
    private BigDecimal price;
    
    private Integer totalSeats;
    
    private Integer soldSeats;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

