package com.wanli.repository;

import com.wanli.entity.User;
import com.wanli.entity.UserRole;
import com.wanli.entity.UserStatus;
import com.wanli.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * UserRepository数据访问层测试
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
@DisplayName("UserRepository测试")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private TestDataFactory testDataFactory;

    @BeforeEach
    void setUp() {
        // 每个测试方法都会创建新的用户实例，避免实体冲突
    }

    @Test
    @DisplayName("根据用户名查找用户 - 存在")
    void findByUsername_UserExists() {
        // Given
        User testUser = TestDataFactory.createDefaultUser();
        testUser = userRepository.save(testUser);
        
        // When
        Optional<User> found = userRepository.findByUsername(testUser.getUsername());
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(testUser.getUsername());
        assertThat(found.get().getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    @DisplayName("根据用户名查找用户 - 不存在")
    void findByUsername_UserNotExists() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistent");
        
        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("根据邮箱查找用户 - 存在")
    void findByEmail_UserExists() {
        // Given
        User testUser = TestDataFactory.createDefaultUser();
        testUser = userRepository.save(testUser);
        
        // When
        Optional<User> found = userRepository.findByEmail(testUser.getEmail());
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(testUser.getEmail());
        assertThat(found.get().getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    @DisplayName("根据邮箱查找用户 - 不存在")
    void findByEmail_UserNotExists() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        
        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("检查用户名是否存在 - 存在")
    void existsByUsername_UserExists() {
        // Given
        User testUser = TestDataFactory.createDefaultUser();
        testUser = userRepository.save(testUser);
        
        // When
        boolean exists = userRepository.existsByUsername(testUser.getUsername());
        
        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("检查用户名是否存在 - 不存在")
    void existsByUsername_UserNotExists() {
        // When
        boolean exists = userRepository.existsByUsername("nonexistent");
        
        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("检查邮箱是否存在 - 存在")
    void existsByEmail_EmailExists() {
        // Given
        User testUser = TestDataFactory.createDefaultUser();
        testUser = userRepository.save(testUser);
        
        // When
        boolean exists = userRepository.existsByEmail(testUser.getEmail());
        
        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("检查邮箱是否存在 - 不存在")
    void existsByEmail_EmailNotExists() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");
        
        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("根据用户状态查找用户")
    void findByStatus_Success() {
        // Given
        User activeUser = TestDataFactory.createDefaultUser();
        activeUser.setStatus(UserStatus.ACTIVE);
        
        User inactiveUser = TestDataFactory.createInactiveUser();
        
        activeUser = userRepository.save(activeUser);
        inactiveUser = userRepository.save(inactiveUser);
        
        // When
        List<User> activeUsers = userRepository.findByStatus(UserStatus.ACTIVE);
        List<User> inactiveUsers = userRepository.findByStatus(UserStatus.INACTIVE);
        
        // Then
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getStatus()).isEqualTo(UserStatus.ACTIVE);
        
        assertThat(inactiveUsers).hasSize(1);
        assertThat(inactiveUsers.get(0).getStatus()).isEqualTo(UserStatus.INACTIVE);
    }

    @Test
    @DisplayName("查找锁定的用户")
    void findLockedUsers_Success() {
        // Given
        User lockedUser = TestDataFactory.createDefaultUser();
        lockedUser.setLockedUntil(OffsetDateTime.now().plusHours(1)); // 锁定1小时
        
        User unlockedUser = TestDataFactory.createDefaultUser();
        unlockedUser.setLockedUntil(null);
        
        userRepository.save(lockedUser);
        userRepository.save(unlockedUser);
        
        // When
        List<User> lockedUsers = userRepository.findLockedUsers(OffsetDateTime.now());
        
        // Then
        assertThat(lockedUsers).hasSize(1);
        assertThat(lockedUsers.get(0).getUsername()).isEqualTo(lockedUser.getUsername());
    }

    @Test
    @DisplayName("查找需要解锁的用户")
    void findUsersToUnlock_Success() {
        // Given
        User expiredLockUser = TestDataFactory.createDefaultUser();
        expiredLockUser.setLockedUntil(OffsetDateTime.now().minusHours(1)); // 锁定已过期
        
        User stillLockedUser = TestDataFactory.createDefaultUser();
        stillLockedUser.setLockedUntil(OffsetDateTime.now().plusHours(1)); // 仍在锁定期
        
        userRepository.save(expiredLockUser);
        userRepository.save(stillLockedUser);
        
        // When
        List<User> usersToUnlock = userRepository.findUsersToUnlock(OffsetDateTime.now());
        
        // Then
        assertThat(usersToUnlock).hasSize(1);
        assertThat(usersToUnlock.get(0).getUsername()).isEqualTo(expiredLockUser.getUsername());
    }

    @Test
    @DisplayName("批量解锁用户")
    void unlockExpiredUsers_Success() {
        // Given
        User expiredLockUser1 = TestDataFactory.createDefaultUser();
        expiredLockUser1.setLockedUntil(OffsetDateTime.now().minusHours(1));
        expiredLockUser1.setLoginAttempts(3);
        
        User expiredLockUser2 = TestDataFactory.createDefaultUser();
        expiredLockUser2.setLockedUntil(OffsetDateTime.now().minusHours(2));
        expiredLockUser2.setLoginAttempts(5);
        
        userRepository.save(expiredLockUser1);
        userRepository.save(expiredLockUser2);
        entityManager.flush();
        
        // When
        int unlockedCount = userRepository.unlockExpiredUsers(OffsetDateTime.now());
        entityManager.flush();
        entityManager.clear();
        
        // Then
        assertThat(unlockedCount).isEqualTo(2);
        
        // 验证用户已解锁
        User updatedUser1 = userRepository.findByUsername(expiredLockUser1.getUsername()).orElse(null);
        User updatedUser2 = userRepository.findByUsername(expiredLockUser2.getUsername()).orElse(null);
        
        assertThat(updatedUser1).isNotNull();
        assertThat(updatedUser1.getLockedUntil()).isNull();
        assertThat(updatedUser1.getLoginAttempts()).isEqualTo(0);
        
        assertThat(updatedUser2).isNotNull();
        assertThat(updatedUser2.getLockedUntil()).isNull();
        assertThat(updatedUser2.getLoginAttempts()).isEqualTo(0);
    }

    @Test
    @DisplayName("更新用户最后登录时间")
    void updateLastLoginTime_Success() {
        // Given
        User testUser = TestDataFactory.createDefaultUser();
        testUser = userRepository.save(testUser);
        entityManager.flush();
        OffsetDateTime loginTime = OffsetDateTime.now();
        
        // When
        int updatedCount = userRepository.updateLastLoginTime(testUser.getId(), loginTime);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        assertThat(updatedCount).isEqualTo(1);
        
        User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        // Convert to UTC for comparison since database stores in UTC
        assertThat(updatedUser.getLastLoginAt().toInstant()).isEqualTo(loginTime.toInstant());
    }

    @Test
    @DisplayName("重置用户登录尝试次数")
    void resetLoginAttempts_Success() {
        // Given
        User testUser = TestDataFactory.createDefaultUser();
        testUser.setLoginAttempts(5);
        testUser = userRepository.save(testUser);
        entityManager.flush();
        
        // When
        int updatedCount = userRepository.resetLoginAttempts(testUser.getId());
        entityManager.flush();
        entityManager.clear();
        
        // Then
        assertThat(updatedCount).isEqualTo(1);
        
        User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getLoginAttempts()).isEqualTo(0);
    }

    @Test
    @DisplayName("增加用户登录尝试次数")
    void incrementLoginAttempts_Success() {
        // Given
        User testUser = TestDataFactory.createDefaultUser();
        testUser.setLoginAttempts(2);
        testUser = userRepository.save(testUser);
        entityManager.flush();
        
        // When
        int updatedCount = userRepository.incrementLoginAttempts(testUser.getId());
        entityManager.flush();
        entityManager.clear();
        
        // Then
        assertThat(updatedCount).isEqualTo(1);
        
        User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getLoginAttempts()).isEqualTo(3);
    }

    @Test
    @DisplayName("锁定用户账户")
    void lockUser_Success() {
        // Given
        User testUser = TestDataFactory.createDefaultUser();
        testUser = userRepository.save(testUser);
        entityManager.flush();
        OffsetDateTime lockUntil = OffsetDateTime.now().plusHours(2);
        
        // When
        int updatedCount = userRepository.lockUser(testUser.getId(), lockUntil);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        assertThat(updatedCount).isEqualTo(1);
        
        User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        // Convert to UTC for comparison since database stores in UTC
        assertThat(updatedUser.getLockedUntil().toInstant()).isEqualTo(lockUntil.toInstant());
    }

    @Test
    @DisplayName("保存和查找用户 - 完整流程")
    void saveAndFind_CompleteFlow() {
        // Given
        User newUser = TestDataFactory.createUser(
                "newuser", 
                "newuser@example.com", 
                "password123", 
                UserRole.HQ_TEACHER, 
                UserStatus.ACTIVE
        );
        
        // When
        User savedUser = userRepository.save(newUser);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        
        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("newuser");
        assertThat(foundUser.get().getEmail()).isEqualTo("newuser@example.com");
        assertThat(foundUser.get().getRole()).isEqualTo(UserRole.HQ_TEACHER);
        assertThat(foundUser.get().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}