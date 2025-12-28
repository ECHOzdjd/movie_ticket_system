package com.movie.order.config;

import com.movie.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置
 */
@Configuration
public class JwtConfig {
    
    @Bean
    public JwtUtil jwtUtil(
            @Value("${jwt.secret:movie-ticket-system-secret-key-2024-very-long-secret-key-for-security}") String secret,
            @Value("${jwt.expiration:86400000}") Long expiration) {
        JwtUtil jwtUtil = new JwtUtil();
        jwtUtil.setSecret(secret);
        jwtUtil.setExpiration(expiration);
        return jwtUtil;
    }
}

