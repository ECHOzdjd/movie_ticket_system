package com.movie.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.movie.order.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    User register(String username, String password, String phone, String email);
    
    /**
     * 用户登录
     */
    String login(String username, String password);
    
    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);
    
    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);
}

