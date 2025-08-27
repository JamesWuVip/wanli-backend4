package com.wanli.service;

import com.wanli.dto.UserCreateDto;
import com.wanli.dto.UserResponseDto;
import com.wanli.dto.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 用户服务接口
 * 
 * @author wanli
 * @version 1.0.0
 */
public interface UserService {
    
    /**
     * 创建用户
     * 
     * @param userCreateDto 用户创建DTO
     * @return 用户响应DTO
     */
    UserResponseDto createUser(UserCreateDto userCreateDto);
    
    /**
     * 获取所有用户（分页）
     * 
     * @param pageable 分页参数
     * @return 用户分页数据
     */
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    
    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户响应DTO
     */
    UserResponseDto getUserById(Long id);
    
    /**
     * 根据用户名获取用户
     * 
     * @param username 用户名
     * @return 用户响应DTO
     */
    UserResponseDto getUserByUsername(String username);
    
    /**
     * 更新用户信息
     * 
     * @param id 用户ID
     * @param userUpdateDto 用户更新DTO
     * @return 用户响应DTO
     */
    UserResponseDto updateUser(Long id, UserUpdateDto userUpdateDto);
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    void deleteUser(Long id);
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
}