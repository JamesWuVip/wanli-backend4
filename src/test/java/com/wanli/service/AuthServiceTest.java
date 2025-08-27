package com.wanli.service;

import com.wanli.dto.LoginRequestDto;
import com.wanli.dto.LoginResponseDto;
import com.wanli.dto.UserRegistrationDto;
import com.wanli.dto.UserResponseDto;
import com.wanli.entity.User;
import com.wanli.entity.UserRole;
import com.wanli.entity.UserStatus;
import com.wanli.exception.user.UserNotFoundException;
import com.wanli.exception.user.InvalidPasswordException;
import com.wanli.service.impl.AuthServiceImpl;
import com.wanli.config.JwtUtil;
import com.wanli.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService单元测试
 * 
 * @author wanli
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService单元测试")
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private UserRegistrationDto registrationDto;
    private LoginRequestDto loginRequest;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.createDefaultUser();
        testUserId = testUser.getId();
        registrationDto = TestDataFactory.createUserRegistrationDto();
        loginRequest = TestDataFactory.createLoginRequestDto();
    }

    @Test
    @DisplayName("用户注册 - 成功")
    void register_Success() {
        // Given
        when(userService.register(any(UserRegistrationDto.class))).thenReturn(testUser);

        // When
        UserResponseDto result = authService.register(registrationDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(testUser.getId());
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(result.getFullName()).isEqualTo(testUser.getFullName());
        assertThat(result.getRole()).isEqualTo(testUser.getRole());
        assertThat(result.getStatus()).isEqualTo(testUser.getStatus());
        
        verify(userService).register(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("用户注册 - UserService抛出异常")
    void register_UserServiceThrowsException() {
        // Given
        when(userService.register(any(UserRegistrationDto.class)))
                .thenThrow(new RuntimeException("Registration failed"));

        // When & Then
        assertThatThrownBy(() -> authService.register(registrationDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Registration failed");
        
        verify(userService).register(any(UserRegistrationDto.class));
        // 确保convertToUserResponseDto不会被调用，因为userService.register抛出了异常
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("用户登录 - 成功")
    void login_Success() {
        // Given
        String token = "jwt-token";
        Long expirationTime = 3600L;
        Authentication authentication = mock(Authentication.class);
        
        when(userService.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPasswordHash()))
                .thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(authentication)).thenReturn(token);
        when(jwtUtil.getExpirationTime()).thenReturn(expirationTime);

        // When
        LoginResponseDto result = authService.login(loginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(token);
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getUser().getUsername()).isEqualTo(testUser.getUsername());
        
        verify(userService).findByUsername("testuser");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(authentication);
        verify(userService).handleLoginSuccess("testuser");
    }

    @Test
    @DisplayName("用户登录 - 用户不存在")
    void login_UserNotFound() {
        // Given
        when(userService.findByUsername("testuser"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UserNotFoundException.class);
        
        verify(userService).findByUsername("testuser");
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtUtil, never()).generateToken(any());
        verify(userService, never()).handleLoginSuccess(any());
        verify(userService, never()).handleLoginFailure(any());
    }

    @Test
    @DisplayName("用户登录 - 用户未激活")
    void login_UserInactive() {
        // Given
        testUser.setStatus(UserStatus.INACTIVE);
        when(userService.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(DisabledException.class);
        
        verify(userService).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtUtil, never()).generateToken(any());
        verify(userService, never()).handleLoginSuccess(any());
        verify(userService, never()).handleLoginFailure(any());
    }

    @Test
    @DisplayName("用户登录 - 账户被锁定")
    void login_AccountLocked() {
        // Given
        testUser.setLockedUntil(OffsetDateTime.now().plusHours(1));
        when(userService.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(LockedException.class);
        
        verify(userService).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtUtil, never()).generateToken(any());
        verify(userService, never()).handleLoginSuccess(any());
        verify(userService).handleLoginFailure("testuser");
    }

    @Test
    @DisplayName("用户登录 - 密码错误")
    void login_InvalidCredentials() {
        // Given
        when(userService.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPasswordHash()))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
        
        verify(userService).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPasswordHash());
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtUtil, never()).generateToken(any());
        verify(userService, never()).handleLoginSuccess(any());
        verify(userService).handleLoginFailure("testuser");
    }

    @Test
    @DisplayName("获取当前用户 - 成功")
    void getCurrentUser_Success() {
        // Given
        String username = testUser.getUsername();
        when(userService.findByUsername(username)).thenReturn(Optional.of(testUser));

        // When
        UserResponseDto result = authService.getCurrentUser(username);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        
        verify(userService).findByUsername(username);
    }

    @Test
    @DisplayName("获取当前用户 - 用户不存在")
    void getCurrentUser_UserNotFound() {
        // Given
        String username = "nonexistent";
        when(userService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.getCurrentUser(username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("用户不存在: {0}");
        
        verify(userService).findByUsername("nonexistent");
    }

    @Test
    @DisplayName("获取当前用户 - 无认证信息")
    void getCurrentUser_NoAuthentication() {
        // Given
        String username = "anonymous";
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.getCurrentUser(username))
                .isInstanceOf(UserNotFoundException.class);
        
        verify(userService).findByUsername(username);
    }

    @Test
    @DisplayName("用户登出 - 成功")
    void logout_Success() {
        // When & Then
        assertThatCode(() -> authService.logout("test-token"))
                .doesNotThrowAnyException();
        
        // 验证SecurityContext被清除
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("转换用户响应DTO - 正确转换")
    void convertToUserResponseDto_CorrectConversion() {
        // Given
        UserRegistrationDto dto = TestDataFactory.createUserRegistrationDto();
        when(userService.register(any(UserRegistrationDto.class))).thenReturn(testUser);

        // When
        UserResponseDto result = authService.register(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(testUser.getId());
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(result.getFullName()).isEqualTo(testUser.getFullName());
        assertThat(result.getRole()).isEqualTo(testUser.getRole());
        assertThat(result.getStatus()).isEqualTo(testUser.getStatus());
        assertThat(result.getCreatedAt()).isEqualTo(testUser.getCreatedAt());
        assertThat(result.getLastLoginAt()).isEqualTo(testUser.getLastLoginAt());
        assertThat(result.isActive()).isEqualTo(testUser.getStatus() == UserStatus.ACTIVE);
        
        verify(userService).register(any(UserRegistrationDto.class));
    }
}