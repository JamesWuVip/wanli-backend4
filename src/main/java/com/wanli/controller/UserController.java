package com.wanli.controller;

import com.wanli.common.ApiResponse;
import com.wanli.dto.UserCreateDto;
import com.wanli.dto.UserResponseDto;
import com.wanli.dto.UserUpdateDto;
import com.wanli.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * 
 * @author wanli
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户CRUD操作相关接口")
public class UserController {
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;
    
    /**
     * 创建用户（管理员权限）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建用户", description = "管理员创建新用户")
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(
            @Valid @RequestBody UserCreateDto userCreateDto) {
        
        log.info("Admin creating user: {}", userCreateDto.getUsername());
        
        UserResponseDto user = userService.createUser(userCreateDto);
        
        log.info("User created successfully: {}", user.getUsername());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("用户创建成功", user));
    }
    
    /**
     * 获取所有用户（分页）
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取所有用户", description = "管理员获取所有用户列表（分页）")
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getAllUsers(Pageable pageable) {
        
        log.info("Getting all users with pagination: {}", pageable);
        
        Page<UserResponseDto> users = userService.getAllUsers(pageable);
        
        return ResponseEntity.ok(ApiResponse.success("获取用户列表成功", users));
    }
    
    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "根据用户ID获取用户详细信息")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        
        log.info("Getting user by id: {}", id);
        
        UserResponseDto user = userService.getUserById(id);
        
        return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", user));
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "更新指定用户的信息")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        
        log.info("Updating user: {}", id);
        
        UserResponseDto user = userService.updateUser(id, userUpdateDto);
        
        log.info("User updated successfully: {}", user.getUsername());
        
        return ResponseEntity.ok(ApiResponse.success("用户信息更新成功", user));
    }
    
    /**
     * 删除用户（管理员权限）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除用户", description = "管理员删除指定用户")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        
        log.info("Admin deleting user: {}", id);
        
        userService.deleteUser(id);
        
        log.info("User deleted successfully: {}", id);
        
        return ResponseEntity.ok(ApiResponse.success("用户删除成功", null));
    }
}