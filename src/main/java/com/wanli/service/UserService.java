package com.wanli.service;

import com.wanli.dto.UserCreateDto;
import com.wanli.dto.UserRegistrationDto;
import com.wanli.dto.UserUpdateDto;
import com.wanli.entity.User;
import com.wanli.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户服务接口
 * 
 * @author wanli
 * @version 1.0.0
 */
public interface UserService {
    
    /**
     * 用户注册
     * 
     * @param registrationDto 注册信息
     * @return 注册后的用户
     */
    User register(UserRegistrationDto registrationDto);
    
    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据用户ID查找用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    Optional<User> findById(UUID id);
    
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
    
    /**
     * 更新用户信息
     * 
     * @param userId 用户ID
     * @param updateDto 更新信息
     * @return 更新后的用户
     */
    User updateUser(UUID userId, UserUpdateDto updateDto);
    
    /**
     * 激活用户
     * 
     * @param userId 用户ID
     */
    void activateUser(UUID userId);
    
    /**
     * 停用用户
     * 
     * @param userId 用户ID
     */
    void deactivateUser(UUID userId);
    
    /**
     * 锁定用户
     * 
     * @param userId 用户ID
     * @param lockDurationMinutes 锁定时长（分钟）
     */
    void lockUser(UUID userId, int lockDurationMinutes);
    
    /**
     * 解锁用户
     * 
     * @param userId 用户ID
     */
    void unlockUser(UUID userId);
    
    /**
     * 更新最后登录时间
     * 
     * @param userId 用户ID
     */
    void updateLastLoginTime(UUID userId);
    
    /**
     * 处理登录失败
     * 
     * @param username 用户名
     */
    void handleLoginFailure(String username);
    
    /**
     * 处理登录成功
     * 
     * @param username 用户名
     */
    void handleLoginSuccess(String username);
    
    /**
     * 解锁过期的用户
     * 
     * @return 解锁的用户数量
     */
    int unlockExpiredUsers();
    
    /**
     * 根据状态查找用户
     * 
     * @param status 用户状态
     * @return 用户列表
     */
    List<User> findByStatus(UserStatus status);
    
    /**
     * 分页查询所有用户
     * 
     * @param pageable 分页参数
     * @return 用户分页数据
     */
    Page<User> findAll(Pageable pageable);
    
    /**
     * 删除用户
     * 
     * @param userId 用户ID
     */
    void deleteUser(UUID userId);
    
    /**
     * 修改密码
     * 
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(UUID userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(UUID userId, String newPassword);
    
    /**
     * 创建用户
     * 
     * @param userCreateDto 用户创建信息
     * @return 创建的用户
     */
    User createUser(UserCreateDto userCreateDto);
    
    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User getUserById(UUID id);
    
    /**
     * 根据用户名获取用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 获取所有用户
     * 
     * @return 用户列表
     */
    List<User> getAllUsers();
}