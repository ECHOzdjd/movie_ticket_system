package com.movie.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 场次排期实体
 */
@Data
@TableName("schedule")
public class Schedule {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 影片ID
     */
    private Long movieId;
    
    /**
     * 影院ID
     */
    private Long cinemaId;
    
    /**
     * 放映厅名称
     */
    private String hallName;
    
    /**
     * 放映时间
     */
    private LocalDateTime showTime;
    
    /**
     * 票价
     */
    private BigDecimal price;
    
    /**
     * 总座位数
     */
    private Integer totalSeats;
    
    /**
     * 已售座位数
     */
    private Integer soldSeats;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

