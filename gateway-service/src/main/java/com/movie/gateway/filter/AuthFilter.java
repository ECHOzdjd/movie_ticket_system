package com.movie.gateway.filter;

import com.movie.common.constant.CommonConstants;
import com.movie.common.result.Result;
import com.movie.common.result.ResultCode;
import com.movie.common.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 认证过滤器
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    
    private final JwtUtil jwtUtil;
    
    private final ObjectMapper objectMapper;
    
    public AuthFilter(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }
    
    /**
     * 白名单路径（不需要认证）
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/user/register",
            "/api/user/login",
            "/api/movie/**",
            "/api/cinema/**",
            "/api/schedule/**"
    );
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        // 检查是否为白名单路径
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }
        
        // 获取Token
        String token = getToken(request);
        if (token == null || token.isEmpty()) {
            return unauthorizedResponse(exchange);
        }
        
        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            return unauthorizedResponse(exchange);
        }
        
        // 从Token中获取用户ID，并添加到请求头
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId != null) {
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(CommonConstants.USER_ID_HEADER, userId.toString())
                    .build();
            exchange = exchange.mutate().request(mutatedRequest).build();
        }
        
        return chain.filter(exchange);
    }
    
    /**
     * 检查是否为白名单路径
     */
    private boolean isWhiteList(String path) {
        return WHITE_LIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
    
    /**
     * 从请求头获取Token
     */
    private String getToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(CommonConstants.TOKEN_HEADER);
        if (authHeader != null && authHeader.startsWith(CommonConstants.TOKEN_PREFIX)) {
            return authHeader.substring(CommonConstants.TOKEN_PREFIX.length());
        }
        return null;
    }
    
    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        Result<?> result = Result.fail(ResultCode.UNAUTHORIZED);
        try {
            String body = objectMapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("序列化响应失败", e);
            return response.setComplete();
        }
    }
    
    @Override
    public int getOrder() {
        return -100; // 优先级较高
    }
}

