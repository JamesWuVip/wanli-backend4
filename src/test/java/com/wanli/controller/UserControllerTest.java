package com.wanli.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanli.config.JwtUtil;
import com.wanli.util.TestDataFactory;
import com.wanli.common.ApiResponse;
import com.wanli.dto.UserCreateDto;
import com.wanli.dto.UserResponseDto;
import com.wanli.dto.UserUpdateDto;
import com.wanli.entity.User;
import com.wanli.entity.UserRole;
import com.wanli.entity.UserStatus;
import com.wanli.exception.user.DuplicateEmailException;
import com.wanli.exception.user.DuplicateUsernameException;
import com.wanli.exception.user.UserNotFoundException;
import com.wanli.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController集成测试
 * 
 * @author wanli
 * @version 1.0.0
 */
@WebMvcTest(UserController.class)
@EnableMethodSecurity(prePostEnabled = true)
@DisplayName("UserController集成测试")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtUtil jwtUtil;

    private UserResponseDto userResponse;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;
    private User user;
    private UUID userId;
    private TestDataFactory testDataFactory;

    @BeforeEach
    void setUp() {
        testDataFactory = new TestDataFactory();
        userId = UUID.randomUUID();
        
        user = testDataFactory.createDefaultUser();
        user.setId(userId);
        
        userResponse = UserResponseDto.builder()
                .userId(userId)
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(OffsetDateTime.now())
                .lastLoginAt(OffsetDateTime.now())
                .build();
        
        userCreateDto = testDataFactory.createUserCreateDto();
        userUpdateDto = testDataFactory.createUserUpdateDto();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("创建用户 - 成功")
    void createUser_Success() throws Exception {
        // Given
        when(userService.createUser(any(UserCreateDto.class))).thenReturn(user);

        // When & Then
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户创建成功"))
                .andExpect(jsonPath("$.data.username").value(userResponse.getUsername()))
                .andExpect(jsonPath("$.data.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.data.fullName").value(userResponse.getFullName()));

        verify(userService).createUser(any(UserCreateDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("创建用户 - 用户名已存在")
    void createUser_DuplicateUsername() throws Exception {
        // Given
        when(userService.createUser(any(UserCreateDto.class)))
                .thenThrow(new DuplicateUsernameException("testuser"));

        // When & Then
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService).createUser(any(UserCreateDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("创建用户 - 邮箱已存在")
    void createUser_DuplicateEmail() throws Exception {
        // Given
        when(userService.createUser(any(UserCreateDto.class)))
                .thenThrow(new DuplicateEmailException("test@example.com"));

        // When & Then
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService).createUser(any(UserCreateDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("创建用户 - 权限不足")
    void createUser_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(userService, never()).createUser(any(UserCreateDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("根据ID获取用户 - 成功")
    void getUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(userId)).thenReturn(user);

        // When & Then
        mockMvc.perform(get("/users/{id}", userId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.id").value(userId.toString()))
                .andExpect(jsonPath("$.data.username").value(userResponse.getUsername()));

        verify(userService).getUserById(userId);
    }

    @Test
    @WithMockUser
    @DisplayName("根据ID获取用户 - 用户不存在")
    void getUserById_NotFound() throws Exception {
        // Given
        when(userService.getUserById(userId))
                .thenThrow(new UserNotFoundException(userId.toString()));

        // When & Then
        mockMvc.perform(get("/users/{id}", userId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).getUserById(userId);
    }

    @Test
    @WithMockUser
    @DisplayName("根据用户名获取用户 - 成功")
    void getUserByUsername_Success() throws Exception {
        // Given
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);

        // When & Then
        mockMvc.perform(get("/users/username/{username}", user.getUsername())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.username").value(user.getUsername()));

        verify(userService).getUserByUsername(user.getUsername());
    }

    @Test
    @WithMockUser
    @DisplayName("根据用户名获取用户 - 用户不存在")
    void getUserByUsername_NotFound() throws Exception {
        // Given
        when(userService.getUserByUsername("testuser"))
                .thenThrow(new UserNotFoundException("testuser"));

        // When & Then
        mockMvc.perform(get("/users/username/{username}", "testuser")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).getUserByUsername("testuser");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("获取所有用户 - 成功")
    void getAllUsers_Success() throws Exception {
        // Given
        List<User> users = Arrays.asList(user);
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/users")
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].username").value(userResponse.getUsername()));

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("获取所有用户 - 权限不足")
    void getAllUsers_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/users")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(userService, never()).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("更新用户信息 - 成功")
    void updateUser_Success() throws Exception {
        // Given
        when(userService.updateUser(eq(userId), any(UserUpdateDto.class))).thenReturn(user);

        // When & Then
        mockMvc.perform(put("/users/{id}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户更新成功"))
                .andExpect(jsonPath("$.data.id").value(userId.toString()));

        verify(userService).updateUser(eq(userId), any(UserUpdateDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("更新用户信息 - 用户不存在")
    void updateUser_NotFound() throws Exception {
        // Given
        when(userService.updateUser(eq(userId), any(UserUpdateDto.class)))
                .thenThrow(new UserNotFoundException(userId.toString()));

        // When & Then
        mockMvc.perform(put("/users/{id}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(userId), any(UserUpdateDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("删除用户 - 成功")
    void deleteUser_Success() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(userId);

        // When & Then
        mockMvc.perform(delete("/users/{id}", userId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户删除成功"));

        verify(userService).deleteUser(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("删除用户 - 用户不存在")
    void deleteUser_NotFound() throws Exception {
        // Given
        doThrow(new UserNotFoundException(userId.toString()))
                .when(userService).deleteUser(userId);

        // When & Then
        mockMvc.perform(delete("/users/{id}", userId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(userId);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("删除用户 - 权限不足")
    void deleteUser_Forbidden() throws Exception {
        // When & Then
        mockMvc.perform(delete("/users/{id}", userId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(userService, never()).deleteUser(userId);
    }

    @Test
    @WithMockUser
    @DisplayName("检查用户名是否存在 - 存在")
    void checkUsernameExists_True() throws Exception {
        // Given
        when(userService.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/users/check/username/{username}", "testuser")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").value(true));

        verify(userService).existsByUsername("testuser");
    }

    @Test
    @WithMockUser
    @DisplayName("检查用户名是否存在 - 不存在")
    void checkUsernameExists_False() throws Exception {
        // Given
        when(userService.existsByUsername("testuser")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/users/check/username/{username}", "testuser")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").value(false));

        verify(userService).existsByUsername("testuser");
    }

    @Test
    @WithMockUser
    @DisplayName("检查邮箱是否存在 - 存在")
    void checkEmailExists_True() throws Exception {
        // Given
        when(userService.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/users/check/email/{email}", "test@example.com")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").value(true));

        verify(userService).existsByEmail("test@example.com");
    }

    @Test
    @WithMockUser
    @DisplayName("检查邮箱是否存在 - 不存在")
    void checkEmailExists_False() throws Exception {
        // Given
        when(userService.existsByEmail("test@example.com")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/users/check/email/{email}", "test@example.com")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").value(false));

        verify(userService).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("未认证访问 - 返回401")
    void unauthenticatedAccess_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getUserById(any(UUID.class));
    }
}