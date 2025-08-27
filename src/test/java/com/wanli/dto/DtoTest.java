package com.wanli.dto;

import com.wanli.entity.UserRole;
import com.wanli.entity.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DTO类单元测试
 * 测试所有DTO类的getter、setter、构造方法和builder方法
 * 
 * @author wanli
 * @version 1.0.0
 */
class DtoTest {

    @Test
    @DisplayName("测试LoginRequestDto - 默认构造方法")
    void testLoginRequestDto_DefaultConstructor() {
        // When
        LoginRequestDto dto = new LoginRequestDto();
        
        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getUsername()).isNull();
        assertThat(dto.getPassword()).isNull();
    }
    
    @Test
    @DisplayName("测试LoginRequestDto - 带参构造方法")
    void testLoginRequestDto_ParameterizedConstructor() {
        // Given
        String username = "testuser";
        String password = "password123";
        
        // When
        LoginRequestDto dto = new LoginRequestDto(username, password);
        
        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getPassword()).isEqualTo(password);
    }
    
    @Test
    @DisplayName("测试LoginRequestDto - Setter方法")
    void testLoginRequestDto_Setters() {
        // Given
        LoginRequestDto dto = new LoginRequestDto();
        String username = "newuser";
        String password = "newpassword";
        
        // When
        dto.setUsername(username);
        dto.setPassword(password);
        
        // Then
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getPassword()).isEqualTo(password);
    }
    
    @Test
    @DisplayName("测试UserRegistrationDto - 所有方法")
    void testUserRegistrationDto_AllMethods() {
        // Given
        UserRegistrationDto dto = new UserRegistrationDto();
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";
        String fullName = "Test User";
        UserRole role = UserRole.STUDENT;
        
        // When
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setEmail(email);
        dto.setFullName(fullName);
        dto.setRole(role);
        
        // Then
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getPassword()).isEqualTo(password);
        assertThat(dto.getEmail()).isEqualTo(email);
        assertThat(dto.getFullName()).isEqualTo(fullName);
        assertThat(dto.getRole()).isEqualTo(role);
    }
    
    @Test
    @DisplayName("测试UserCreateDto - 所有方法")
    void testUserCreateDto_AllMethods() {
        // Given
        UserCreateDto dto = new UserCreateDto();
        String username = "createuser";
        String password = "password123";
        String email = "create@example.com";
        String fullName = "Create User";
        UserRole role = UserRole.HQ_TEACHER;
        
        // When
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setEmail(email);
        dto.setFullName(fullName);
        dto.setRole(role);
        
        // Then
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getPassword()).isEqualTo(password);
        assertThat(dto.getEmail()).isEqualTo(email);
        assertThat(dto.getFullName()).isEqualTo(fullName);
        assertThat(dto.getRole()).isEqualTo(role);
    }
    
    @Test
    @DisplayName("测试UserCreateDto - 默认角色")
    void testUserCreateDto_DefaultRole() {
        // When
        UserCreateDto dto = new UserCreateDto();
        
        // Then
        assertThat(dto.getRole()).isEqualTo(UserRole.STUDENT);
    }
    
    @Test
    @DisplayName("测试UserUpdateDto - 所有方法")
    void testUserUpdateDto_AllMethods() {
        // Given
        UserUpdateDto dto = new UserUpdateDto();
        String email = "update@example.com";
        String fullName = "Updated User";
        UserRole role = UserRole.BRANCH_TEACHER;
        
        // When
        dto.setEmail(email);
        dto.setFullName(fullName);
        dto.setRole(role);
        
        // Then
        assertThat(dto.getEmail()).isEqualTo(email);
        assertThat(dto.getFullName()).isEqualTo(fullName);
        assertThat(dto.getRole()).isEqualTo(role);
    }
    
    @Test
    @DisplayName("测试UserResponseDto - Builder模式")
    void testUserResponseDto_Builder() {
        // Given
        UUID userId = UUID.randomUUID();
        String username = "responseuser";
        String email = "response@example.com";
        String fullName = "Response User";
        UserRole role = UserRole.ADMIN;
        UserStatus status = UserStatus.ACTIVE;
        OffsetDateTime createdAt = OffsetDateTime.now();
        OffsetDateTime lastLoginAt = OffsetDateTime.now();
        boolean isActive = true;
        
        // When
        UserResponseDto dto = UserResponseDto.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .fullName(fullName)
                .role(role)
                .status(status)
                .createdAt(createdAt)
                .lastLoginAt(lastLoginAt)
                .isActive(isActive)
                .build();
        
        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getEmail()).isEqualTo(email);
        assertThat(dto.getFullName()).isEqualTo(fullName);
        assertThat(dto.getRole()).isEqualTo(role);
        assertThat(dto.getStatus()).isEqualTo(status);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getLastLoginAt()).isEqualTo(lastLoginAt);
        assertThat(dto.isActive()).isEqualTo(isActive);
    }
    
    @Test
    @DisplayName("测试UserResponseDto - 无参构造方法")
    void testUserResponseDto_NoArgsConstructor() {
        // When
        UserResponseDto dto = new UserResponseDto();
        
        // Then
        assertThat(dto).isNotNull();
    }
    
    @Test
    @DisplayName("测试UserResponseDto - 全参构造方法")
    void testUserResponseDto_AllArgsConstructor() {
        // Given
        UUID userId = UUID.randomUUID();
        String username = "allargs";
        String email = "allargs@example.com";
        String fullName = "All Args User";
        UserRole role = UserRole.STUDENT;
        UserStatus status = UserStatus.INACTIVE;
        OffsetDateTime createdAt = OffsetDateTime.now();
        OffsetDateTime lastLoginAt = OffsetDateTime.now();
        boolean isActive = false;
        
        // When
        UserResponseDto dto = new UserResponseDto(userId, username, email, fullName, 
                role, status, createdAt, lastLoginAt, isActive);
        
        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getEmail()).isEqualTo(email);
        assertThat(dto.getFullName()).isEqualTo(fullName);
        assertThat(dto.getRole()).isEqualTo(role);
        assertThat(dto.getStatus()).isEqualTo(status);
        assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        assertThat(dto.getLastLoginAt()).isEqualTo(lastLoginAt);
        assertThat(dto.isActive()).isEqualTo(isActive);
    }
    
    @Test
    @DisplayName("测试LoginResponseDto - Builder模式")
    void testLoginResponseDto_Builder() {
        // Given
        String accessToken = "test-token";
        String tokenType = "Bearer";
        Long expiresIn = 3600L;
        LoginResponseDto.UserInfoDto userInfo = new LoginResponseDto.UserInfoDto();
        
        // When
        LoginResponseDto dto = LoginResponseDto.builder()
                .accessToken(accessToken)
                .tokenType(tokenType)
                .expiresIn(expiresIn)
                .user(userInfo)
                .build();
        
        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getAccessToken()).isEqualTo(accessToken);
        assertThat(dto.getTokenType()).isEqualTo(tokenType);
        assertThat(dto.getExpiresIn()).isEqualTo(expiresIn);
        assertThat(dto.getUser()).isEqualTo(userInfo);
    }
    
    @Test
    @DisplayName("测试LoginResponseDto - 带参构造方法")
    void testLoginResponseDto_ParameterizedConstructor() {
        // Given
        String accessToken = "param-token";
        String tokenType = "Bearer";
        Long expiresIn = 7200L;
        LoginResponseDto.UserInfoDto userInfo = new LoginResponseDto.UserInfoDto();
        
        // When
        LoginResponseDto dto = new LoginResponseDto(accessToken, tokenType, expiresIn, userInfo);
        
        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getAccessToken()).isEqualTo(accessToken);
        assertThat(dto.getTokenType()).isEqualTo(tokenType);
        assertThat(dto.getExpiresIn()).isEqualTo(expiresIn);
        assertThat(dto.getUser()).isEqualTo(userInfo);
    }
    
    @Test
    @DisplayName("测试LoginResponseDto.UserInfoDto - 无参构造方法")
    void testUserInfoDto_NoArgsConstructor() {
        // When
        LoginResponseDto.UserInfoDto dto = new LoginResponseDto.UserInfoDto();
        
        // Then
        assertThat(dto).isNotNull();
    }
    
    @Test
    @DisplayName("测试LoginResponseDto.UserInfoDto - 带参构造方法")
    void testUserInfoDto_ParameterizedConstructor() {
        // Given
        UUID userId = UUID.randomUUID();
        String username = "infouser";
        String email = "info@example.com";
        String fullName = "Info User";
        String role = "STUDENT";
        OffsetDateTime lastLoginAt = OffsetDateTime.now();
        
        // When
        LoginResponseDto.UserInfoDto dto = new LoginResponseDto.UserInfoDto(
                userId, username, email, fullName, role, lastLoginAt);
        
        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getEmail()).isEqualTo(email);
        assertThat(dto.getFullName()).isEqualTo(fullName);
        assertThat(dto.getRole()).isEqualTo(role);
        assertThat(dto.getLastLoginAt()).isEqualTo(lastLoginAt);
    }
    
    @Test
    @DisplayName("测试LoginResponseDto.UserInfoDto - Builder模式")
    void testUserInfoDto_Builder() {
        // Given
        UUID userId = UUID.randomUUID();
        String username = "builderuser";
        String email = "builder@example.com";
        String fullName = "Builder User";
        String role = "HQ_TEACHER";
        OffsetDateTime lastLoginAt = OffsetDateTime.now();
        
        // When
        LoginResponseDto.UserInfoDto dto = LoginResponseDto.UserInfoDto.builder()
                .userId(userId)
                .username(username)
                .email(email)
                .fullName(fullName)
                .role(role)
                .lastLoginAt(lastLoginAt)
                .build();
        
        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getEmail()).isEqualTo(email);
        assertThat(dto.getFullName()).isEqualTo(fullName);
        assertThat(dto.getRole()).isEqualTo(role);
        assertThat(dto.getLastLoginAt()).isEqualTo(lastLoginAt);
    }
    
    @Test
    @DisplayName("测试LoginResponseDto - Setter方法")
    void testLoginResponseDto_Setters() {
        // Given
        LoginResponseDto dto = new LoginResponseDto();
        String accessToken = "setter-token";
        String tokenType = "Bearer";
        Long expiresIn = 1800L;
        LoginResponseDto.UserInfoDto userInfo = new LoginResponseDto.UserInfoDto();
        
        // When
        dto.setAccessToken(accessToken);
        dto.setTokenType(tokenType);
        dto.setExpiresIn(expiresIn);
        dto.setUser(userInfo);
        
        // Then
        assertThat(dto.getAccessToken()).isEqualTo(accessToken);
        assertThat(dto.getTokenType()).isEqualTo(tokenType);
        assertThat(dto.getExpiresIn()).isEqualTo(expiresIn);
        assertThat(dto.getUser()).isEqualTo(userInfo);
    }
    
    @Test
    @DisplayName("测试LoginResponseDto.UserInfoDto - Setter方法")
    void testUserInfoDto_Setters() {
        // Given
        LoginResponseDto.UserInfoDto dto = new LoginResponseDto.UserInfoDto();
        UUID userId = UUID.randomUUID();
        String username = "setteruser";
        String email = "setter@example.com";
        String fullName = "Setter User";
        String role = "ADMIN";
        OffsetDateTime lastLoginAt = OffsetDateTime.now();
        
        // When
        dto.setUserId(userId);
        dto.setUsername(username);
        dto.setEmail(email);
        dto.setFullName(fullName);
        dto.setRole(role);
        dto.setLastLoginAt(lastLoginAt);
        
        // Then
        assertThat(dto.getUserId()).isEqualTo(userId);
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getEmail()).isEqualTo(email);
        assertThat(dto.getFullName()).isEqualTo(fullName);
        assertThat(dto.getRole()).isEqualTo(role);
        assertThat(dto.getLastLoginAt()).isEqualTo(lastLoginAt);
    }
}