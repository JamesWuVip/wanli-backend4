package com.wanli.service;

import com.wanli.dto.UserRegistrationDto;
import com.wanli.dto.UserResponseDto;
import com.wanli.entity.User;
import com.wanli.service.impl.AuthServiceImpl;
import com.wanli.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService简化测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService简化测试")
class AuthServiceSimpleTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.createDefaultUser();
        testUser.onCreate(); // 模拟JPA的@PrePersist行为
        registrationDto = TestDataFactory.createUserRegistrationDto();
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
        
        verify(userService).register(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("测试用户对象创建")
    void testUserCreation() {
        // When
        User user = TestDataFactory.createDefaultUser();
        user.onCreate(); // 模拟JPA的@PrePersist行为
        
        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNotNull();
        assertThat(user.getUsername()).startsWith("testuser_");
        assertThat(user.getEmail()).startsWith("test_");
    }
}