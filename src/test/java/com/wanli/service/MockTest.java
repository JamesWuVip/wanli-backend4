package com.wanli.service;

import com.wanli.dto.UserRegistrationDto;
import com.wanli.entity.User;
import com.wanli.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MockTest {
    
    @Mock
    private UserService userService;
    
    private TestDataFactory testDataFactory = new TestDataFactory();
    
    @Test
    void testMockConfiguration() {
        // Given
        User testUser = testDataFactory.createDefaultUser();
        testUser.onCreate(); // 模拟JPA的@PrePersist行为
        UserRegistrationDto registrationDto = testDataFactory.createUserRegistrationDto();
        
        System.out.println("testUser ID: " + testUser.getId());
        System.out.println("testUser username: " + testUser.getUsername());
        System.out.println("testUser createdAt: " + testUser.getCreatedAt());
        
        when(userService.register(registrationDto)).thenReturn(testUser);
        
        // When
        User result = userService.register(registrationDto);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(result.getCreatedAt()).isNotNull();
    }
}