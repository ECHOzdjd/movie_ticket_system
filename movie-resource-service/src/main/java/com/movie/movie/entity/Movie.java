package com.movie.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 影片实体
 */
@Data
@TableName("movie")
public class Movie {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 影片名称
     */
    private String name;
    
    /**
     * 影片类型
     */
    private String type;
    
    /**
     * 导演
     */
    private String director;
    
    /**
     * 主演
     */
    private String actors;
    
    /**
     * 简介
     */
    private String description;
    
    /**
     * 时长（分钟）
     */
    private Integer duration;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 封面图片
     */
    private String coverImage;
    
    /**
     * 上映日期
     */
    private LocalDateTime releaseDate;
    
    /**
     * 是否上映
     */
    private Boolean isReleased;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

