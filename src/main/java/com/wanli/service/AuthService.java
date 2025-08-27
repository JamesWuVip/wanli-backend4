package com.wanli.service;

import com.wanli.dto.LoginRequestDto;
import com.wanli.dto.LoginResponseDto;
import com.wanli.dto.UserRegistrationDto;
import com.wanli.dto.UserResponseDto;

/**
 * 认证服务接口
 * 
 * @author wanli
 * @version 1.0.0
 */
public interface AuthService {
    
    /**
     * 用户注册
     * 
     * @param registrationDto 注册信息
     * @return 用户响应DTO
     */
    UserResponseDto register(UserRegistrationDto registrationDto);
    
    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 登录响应DTO
     */
    LoginResponseDto login(LoginRequestDto loginRequest);
    
    /**
     * 获取当前用户信息
     * 
     * @param username 用户名
     * @return 用户响应DTO
     */
    UserResponseDto getCurrentUser(String username);
    
    /**
     * 用户登出
     * 
     * @param token JWT令牌
     */
    void logout(String token);
}