package com.wanli.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wanli.dto.UserCreateDto;
import com.wanli.dto.UserRegistrationDto;
import com.wanli.dto.UserUpdateDto;
import com.wanli.entity.User;
import com.wanli.entity.UserRole;
import com.wanli.entity.UserStatus;
import com.wanli.exception.user.DuplicateEmailException;
import com.wanli.exception.user.DuplicateUsernameException;
import com.wanli.exception.user.InvalidPasswordException;
import com.wanli.exception.user.UserNotFoundException;
import com.wanli.repository.UserRepository;
import com.wanli.service.impl.UserServiceImpl;
import com.wanli.util.TestDataFactory;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private TestDataFactory testDataFactory;
    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testDataFactory = new TestDataFactory();
        testUser = testDataFactory.createDefaultUser();
        testUserId = UUID.randomUUID();
        testUser.setId(testUserId);
    }

    @Test
    @DisplayName("用户注册 - 成功")
    void registerUser_Success() {
        // Given
        UserRegistrationDto registrationDto = testDataFactory.createUserRegistrationDto();
        User savedUser = testDataFactory.createUser(
            registrationDto.getUsername(),
            registrationDto.getEmail(),
            registrationDto.getPassword(),
            registrationDto.getRole(),
            UserStatus.ACTIVE
        );
        
        when(userRepository.existsByUsername(registrationDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registrationDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.register(registrationDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(registrationDto.getUsername());
        assertThat(result.getEmail()).isEqualTo(registrationDto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("用户注册 - 用户名已存在")
    void registerUser_DuplicateUsername() {
        // Given
        UserRegistrationDto registrationDto = testDataFactory.createUserRegistrationDto();
        when(userRepository.existsByUsername(registrationDto.getUsername())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.register(registrationDto))
                .isInstanceOf(DuplicateUsernameException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("用户注册 - 邮箱已存在")
    void registerUser_DuplicateEmail() {
        // Given
        UserRegistrationDto registrationDto = testDataFactory.createUserRegistrationDto();
        when(userRepository.existsByUsername(registrationDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registrationDto.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.register(registrationDto))
                .isInstanceOf(DuplicateEmailException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("根据用户名查找用户 - 成功")
    void findByUsername_Success() {
        // Given
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByUsername(testUser.getUsername());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    @DisplayName("根据用户名查找用户 - 用户不存在")
    void findByUsername_NotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByUsername("nonexistent");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("根据邮箱查找用户 - 成功")
    void findByEmail_Success() {
        // Given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByEmail(testUser.getEmail());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    @DisplayName("检查用户名是否存在 - 存在")
    void existsByUsername_True() {
        // Given
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(true);

        // When
        boolean result = userService.existsByUsername(testUser.getUsername());

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("检查邮箱是否存在 - 不存在")
    void existsByEmail_False() {
        // Given
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        // When
        boolean result = userService.existsByEmail("new@example.com");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("更新用户信息 - 成功")
    void updateUser_Success() {
        // Given
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail("newemail@example.com");
        updateDto.setFullName("New Full Name");
        updateDto.setRole(UserRole.HQ_TEACHER);

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.updateUser(testUserId, updateDto);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("更新用户信息 - 用户不存在")
    void updateUser_UserNotFound() {
        // Given
        UserUpdateDto updateDto = new UserUpdateDto();
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(testUserId, updateDto))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("激活用户 - 成功")
    void activateUser_Success() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.activateUser(testUserId);

        // Then
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("停用用户 - 成功")
    void deactivateUser_Success() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.deactivateUser(testUserId);

        // Then
        verify(userRepository).save(testUser);
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    @DisplayName("锁定用户 - 成功")
    void lockUser_Success() {
        // Given
        int lockDurationMinutes = 30;

        // When
        userService.lockUser(testUserId, lockDurationMinutes);

        // Then
        verify(userRepository).lockUser(eq(testUserId), any(OffsetDateTime.class));
    }

    @Test
    @DisplayName("解锁用户 - 成功")
    void unlockUser_Success() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.unlockUser(testUserId);

        // Then
        verify(userRepository).save(testUser);
        assertThat(testUser.getLoginAttempts()).isEqualTo(0);
        assertThat(testUser.getLockedUntil()).isNull();
    }

    @Test
    @DisplayName("更新最后登录时间 - 成功")
    void updateLastLoginTime_Success() {
        // When
        userService.updateLastLoginTime(testUserId);

        // Then
        verify(userRepository).updateLastLoginTime(eq(testUserId), any(OffsetDateTime.class));
    }

    @Test
    @DisplayName("处理登录失败 - 用户存在")
    void handleLoginFailure_UserExists() {
        // Given
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.handleLoginFailure(testUser.getUsername());

        // Then
        verify(userRepository).save(testUser);
        assertThat(testUser.getLoginAttempts()).isGreaterThan(0);
    }

    @Test
    @DisplayName("处理登录失败 - 用户不存在")
    void handleLoginFailure_UserNotExists() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        userService.handleLoginFailure("nonexistent");

        // Then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("处理登录成功 - 成功")
    void handleLoginSuccess_Success() {
        // Given
        testUser.setLoginAttempts(3);
        testUser.setLockedUntil(OffsetDateTime.now().plusMinutes(30));
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.handleLoginSuccess(testUser.getUsername());

        // Then
        verify(userRepository).save(testUser);
        assertThat(testUser.getLoginAttempts()).isEqualTo(0);
        assertThat(testUser.getLockedUntil()).isNull();
    }

    @Test
    @DisplayName("解锁过期用户 - 成功")
    void unlockExpiredUsers_Success() {
        // Given
        when(userRepository.unlockExpiredUsers(any(OffsetDateTime.class))).thenReturn(5);

        // When
        int result = userService.unlockExpiredUsers();

        // Then
        assertThat(result).isEqualTo(5);
        verify(userRepository).unlockExpiredUsers(any(OffsetDateTime.class));
    }

    @Test
    @DisplayName("根据状态查找用户 - 成功")
    void findByStatus_Success() {
        // Given
        List<User> users = Arrays.asList(testUser, testDataFactory.createTeacherUser());
        when(userRepository.findByStatus(UserStatus.ACTIVE)).thenReturn(users);

        // When
        List<User> result = userService.findByStatus(UserStatus.ACTIVE);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(users);
    }

    @Test
    @DisplayName("分页查找所有用户 - 成功")
    void findAll_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Arrays.asList(testUser, testDataFactory.createTeacherUser());
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<User> result = userService.findAll(pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("删除用户 - 成功")
    void deleteUser_Success() {
        // Given
        when(userRepository.existsById(testUserId)).thenReturn(true);

        // When
        userService.deleteUser(testUserId);

        // Then
        verify(userRepository).deleteById(testUserId);
    }

    @Test
    @DisplayName("删除用户 - 用户不存在")
    void deleteUser_UserNotFound() {
        // Given
        when(userRepository.existsById(testUserId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(testUserId))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository, never()).deleteById(testUserId);
    }

    @Test
    @DisplayName("修改密码 - 成功")
    void changePassword_Success() {
        // Given
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String encodedNewPassword = "encodedNewPassword";
        
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(oldPassword, testUser.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.changePassword(testUserId, oldPassword, newPassword);

        // Then
        verify(userRepository).save(testUser);
        assertThat(testUser.getPasswordHash()).isEqualTo(encodedNewPassword);
    }

    @Test
    @DisplayName("修改密码 - 旧密码错误")
    void changePassword_InvalidOldPassword() {
        // Given
        String oldPassword = "wrongPassword";
        String newPassword = "newPassword";
        
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(oldPassword, testUser.getPasswordHash())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(testUserId, oldPassword, newPassword))
                .isInstanceOf(InvalidPasswordException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("重置密码 - 成功")
    void resetPassword_Success() {
        // Given
        String newPassword = "newPassword";
        String encodedNewPassword = "encodedNewPassword";
        testUser.setLoginAttempts(3);
        testUser.setLockedUntil(OffsetDateTime.now().plusMinutes(30));
        
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.resetPassword(testUserId, newPassword);

        // Then
        verify(userRepository).save(testUser);
        assertThat(testUser.getPasswordHash()).isEqualTo(encodedNewPassword);
        assertThat(testUser.getLoginAttempts()).isEqualTo(0);
        assertThat(testUser.getLockedUntil()).isNull();
    }

    @Test
    @DisplayName("创建用户 - 成功")
    void createUser_Success() {
        // Given
        UserCreateDto userCreateDto = testDataFactory.createUserCreateDto();
        User newUser = testDataFactory.createUser("newuser", "new@example.com", "password", UserRole.STUDENT, UserStatus.ACTIVE);
        when(userRepository.existsByUsername(userCreateDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userCreateDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userCreateDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User result = userService.createUser(userCreateDto);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("根据ID获取用户 - 成功")
    void getUserById_Success() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getUserById(testUserId);

        // Then
        assertThat(result).isEqualTo(testUser);
    }

    @Test
    @DisplayName("根据ID获取用户 - 用户不存在")
    void getUserById_NotFound() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(testUserId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("根据用户名获取用户 - 成功")
    void getUserByUsername_Success() {
        // Given
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getUserByUsername(testUser.getUsername());

        // Then
        assertThat(result).isEqualTo(testUser);
    }

    @Test
    @DisplayName("获取所有用户 - 成功")
    void getAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(testUser, testDataFactory.createTeacherUser());
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(users);
    }
}