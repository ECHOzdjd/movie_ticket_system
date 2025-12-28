package com.movie.common.constant;

/**
 * 通用常量
 */
public class CommonConstants {
    
    /**
     * Token Header 名称
     */
    public static final String TOKEN_HEADER = "Authorization";
    
    /**
     * Token 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";
    
    /**
     * 用户ID请求头
     */
    public static final String USER_ID_HEADER = "X-User-Id";
    
    /**
     * 座位锁前缀
     */
    public static final String SEAT_LOCK_PREFIX = "seat:lock:";
    
    /**
     * 座位锁默认过期时间（秒）
     */
    public static final Long SEAT_LOCK_EXPIRE_TIME = 300L; // 5分钟
    
    /**
     * 订单默认过期时间（秒）
     */
    public static final Long ORDER_EXPIRE_TIME = 900L; // 15分钟
}

