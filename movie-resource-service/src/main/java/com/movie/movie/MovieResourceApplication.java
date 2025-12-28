package com.movie.movie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 影片资源服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.movie.movie.mapper")
public class MovieResourceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MovieResourceApplication.class, args);
    }
}

