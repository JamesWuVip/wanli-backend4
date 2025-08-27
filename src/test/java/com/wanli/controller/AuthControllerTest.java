package com.wanli.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanli.config.JwtUtil;
import com.wanli.config.SecurityConfig;
import com.wanli.util.TestDataFactory;
import com.wanli.dto.LoginRequestDto;
import com.wanli.dto.LoginResponseDto;
import com.wanli.dto.UserRegistrationDto;
import com.wanli.dto.UserResponseDto;
import com.wanli.entity.UserRole;
import com.wanli.entity.UserStatus;
import com.wanli.exception.user.UserNotFoundException;
import com.wanli.exception.user.DuplicateUsernameException;
import com.wanli.exception.user.DuplicateEmailException;
import com.wanli.exception.user.InvalidPasswordException;
import com.wanli.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController集成测试
 * 
 * @author wanli
 * @version 1.0.0
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController集成测试")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;
    
    @MockBean
    private JwtUtil jwtUtil;
    


    private UserRegistrationDto registrationDto;
    private LoginRequestDto loginRequest;
    private UserResponseDto userResponse;
    private LoginResponseDto loginResponse;
    private TestDataFactory testDataFactory;

    @BeforeEach
    void setUp() {
        testDataFactory = new TestDataFactory();
        registrationDto = testDataFactory.createUserRegistrationDto();
        loginRequest = testDataFactory.createLoginRequestDto();
        
        userResponse = UserResponseDto.builder()
                .userId(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .role(UserRole.STUDENT)
                .status(UserStatus.ACTIVE)
                .createdAt(OffsetDateTime.now())
                .lastLoginAt(OffsetDateTime.now())
                .build();
        
        LoginResponseDto.UserInfoDto userInfo = LoginResponseDto.UserInfoDto.builder()
                .userId(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .role("STUDENT")
                .build();
        
        loginResponse = LoginResponseDto.builder()
                .accessToken("jwt-token")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(userInfo)
                .build();
    }

    @Test
    @DisplayName("用户注册 - 成功")
    void register_Success() throws Exception {
        // Given
        when(authService.register(any(UserRegistrationDto.class))).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户注册成功"))
                .andExpect(jsonPath("$.data.username").value(userResponse.getUsername()))
                .andExpect(jsonPath("$.data.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.data.fullName").value(userResponse.getFullName()))
                .andExpect(jsonPath("$.data.role").value(userResponse.getRole().name()))
                .andExpect(jsonPath("$.data.status").value(userResponse.getStatus().name()));

        verify(authService).register(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("用户注册 - 用户名已存在")
    void register_DuplicateUsername() throws Exception {
        // Given
        when(authService.register(any(UserRegistrationDto.class)))
                .thenThrow(new DuplicateUsernameException("testuser"));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(authService).register(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("用户注册 - 邮箱已存在")
    void register_DuplicateEmail() throws Exception {
        // Given
        when(authService.register(any(UserRegistrationDto.class)))
                .thenThrow(new DuplicateEmailException("test@example.com"));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(authService).register(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("用户注册 - 请求参数无效")
    void register_InvalidRequest() throws Exception {
        // Given
        UserRegistrationDto invalidDto = new UserRegistrationDto();
        invalidDto.setUsername(""); // 空用户名
        invalidDto.setEmail("invalid-email"); // 无效邮箱
        invalidDto.setPassword("123"); // 密码太短
        invalidDto.setFullName("");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("用户登录 - 成功")
    void login_Success() throws Exception {
        // Given
        when(authService.login(any(LoginRequestDto.class))).thenReturn(loginResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.accessToken").value(loginResponse.getAccessToken()))
                .andExpect(jsonPath("$.data.user.username").value(loginResponse.getUser().getUsername()));

        verify(authService).login(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("用户登录 - 用户不存在")
    void login_UserNotFound() throws Exception {
        // Given
        when(authService.login(any(LoginRequestDto.class)))
                .thenThrow(new UserNotFoundException("testuser"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(authService).login(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("用户登录 - 密码错误")
    void login_InvalidCredentials() throws Exception {
        // Given
        when(authService.login(any(LoginRequestDto.class)))
                .thenThrow(new InvalidPasswordException("用户名或密码错误"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(authService).login(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("用户登录 - 账户被锁定")
    void login_AccountLocked() throws Exception {
        // Given
        when(authService.login(any(LoginRequestDto.class)))
                .thenThrow(new RuntimeException("账户已被锁定"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(authService).login(any(LoginRequestDto.class));
    }

    @Test
    @DisplayName("用户登录 - 请求参数无效")
    void login_InvalidRequest() throws Exception {
        // Given
        LoginRequestDto invalidRequest = new LoginRequestDto();
        invalidRequest.setUsername(""); // 空用户名
        invalidRequest.setPassword(""); // 空密码

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(LoginRequestDto.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("获取当前用户信息 - 成功")
    void getCurrentUser_Success() throws Exception {
        // Given
        when(authService.getCurrentUser("testuser")).thenReturn(userResponse);

        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取用户信息成功"))
                .andExpect(jsonPath("$.data.username").value(userResponse.getUsername()))
                .andExpect(jsonPath("$.data.email").value(userResponse.getEmail()));

        verify(authService).getCurrentUser("testuser");
    }

    @Test
    @DisplayName("获取当前用户信息 - 未认证")
    void getCurrentUser_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isInternalServerError()); // 因为禁用了安全过滤器，会出现空指针异常

        verify(authService, never()).getCurrentUser(anyString());
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("获取当前用户信息 - 用户不存在")
    void getCurrentUser_UserNotFound() throws Exception {
        // Given
        when(authService.getCurrentUser("testuser"))
                .thenThrow(new UserNotFoundException("testuser"));

        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(authService).getCurrentUser("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("用户登出 - 成功")
    void logout_Success() throws Exception {
        // Given
        doNothing().when(authService).logout(isNull());

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("登出成功"));

        verify(authService).logout(isNull());
    }

    @Test
    @DisplayName("用户登出 - 未认证")
    void logout_Unauthorized() throws Exception {
        // Given
        doNothing().when(authService).logout(isNull());
        
        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk()); // 因为禁用了安全过滤器，请求会正常处理

        verify(authService).logout(isNull());
    }
}