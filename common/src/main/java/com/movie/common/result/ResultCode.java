package com.movie.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    
    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    
    // 参数相关 400-499
    PARAM_ERROR(400, "参数错误"),
    PARAM_MISSING(401, "参数缺失"),
    
    // 认证相关 401-403
    UNAUTHORIZED(401, "未授权，请先登录"),
    TOKEN_INVALID(402, "Token无效或已过期"),
    FORBIDDEN(403, "无权限访问"),
    
    // 业务相关 1000-1999
    USER_NOT_FOUND(1000, "用户不存在"),
    USER_ALREADY_EXISTS(1001, "用户已存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    
    MOVIE_NOT_FOUND(1100, "影片不存在"),
    SCHEDULE_NOT_FOUND(1101, "场次不存在"),
    CINEMA_NOT_FOUND(1102, "影院不存在"),
    
    SEAT_ALREADY_LOCKED(1200, "座位已被锁定"),
    SEAT_NOT_AVAILABLE(1201, "座位不可用"),
    SEAT_LOCK_EXPIRED(1202, "座位锁已过期"),
    
    ORDER_NOT_FOUND(1300, "订单不存在"),
    ORDER_STATUS_ERROR(1301, "订单状态错误"),
    ORDER_EXPIRED(1302, "订单已过期"),
    ORDER_CANCELED(1303, "订单已取消");
    
    private final Integer code;
    private final String message;
}

