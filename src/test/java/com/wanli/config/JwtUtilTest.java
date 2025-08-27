package com.wanli.config;

import com.wanli.base.TestBase;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JwtUtil单元测试
 * 
 * @author wanli
 * @version 1.0.0
 */
@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tdGVzdGluZy1wdXJwb3Nlcy1vbmx5LXRoaXMtaXMtYS12ZXJ5LWxvbmctc2VjcmV0LWtleQ==",
        "jwt.access-token-expiration=3600000", // 1小时
        "jwt.refresh-token-expiration=86400000" // 24小时
})
@DisplayName("JwtUtil单元测试")
class JwtUtilTest extends TestBase {

    @Autowired
    private JwtUtil jwtUtil;

    private UserDetails userDetails;
    private Authentication authentication;
    private String validToken;
    private String username;
    private Collection<GrantedAuthority> authorities;

    @BeforeEach
    void setUp() {
        username = "testuser";
        authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_STUDENT")
        );
        
        userDetails = User.builder()
                .username(username)
                .password("password")
                .authorities(authorities)
                .build();
        
        authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        
        validToken = jwtUtil.generateAccessToken(userDetails);
    }

    @Test
    @DisplayName("生成访问Token - 成功")
    void generateAccessToken_Success() {
        // When
        String token = jwtUtil.generateAccessToken(userDetails);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT应该有3个部分
        
        // 验证token中的用户名
        String extractedUsername = jwtUtil.extractUsername(token);
        assertThat(extractedUsername).isEqualTo(username);
        
        // 验证token中的权限
        List<String> extractedAuthorities = jwtUtil.extractAuthorities(token);
        assertThat(extractedAuthorities).containsExactlyInAnyOrder("ROLE_USER", "ROLE_STUDENT");
    }

    @Test
    @DisplayName("生成Token（基于Authentication） - 成功")
    void generateToken_Success() {
        // When
        String token = jwtUtil.generateToken(authentication);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        
        String extractedUsername = jwtUtil.extractUsername(token);
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("生成刷新Token - 成功")
    void generateRefreshToken_Success() {
        // When
        String refreshToken = jwtUtil.generateRefreshToken(username);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();
        assertThat(refreshToken.split("\\.")).hasSize(3);
        
        String extractedUsername = jwtUtil.extractUsername(refreshToken);
        assertThat(extractedUsername).isEqualTo(username);
        
        // 刷新token不应该包含权限信息
        List<String> extractedAuthorities = jwtUtil.extractAuthorities(refreshToken);
        assertThat(extractedAuthorities).isNull();
    }

    @Test
    @DisplayName("从Token中提取用户名 - 成功")
    void extractUsername_Success() {
        // When
        String extractedUsername = jwtUtil.extractUsername(validToken);

        // Then
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("从Token中提取过期时间 - 成功")
    void extractExpiration_Success() {
        // When
        Date expiration = jwtUtil.extractExpiration(validToken);

        // Then
        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfter(new Date());
        
        // 验证过期时间大约是1小时后（允许1分钟误差）
        long expectedExpiration = System.currentTimeMillis() + 3600000; // 1小时
        long actualExpiration = expiration.getTime();
        assertThat(Math.abs(actualExpiration - expectedExpiration)).isLessThan(60000); // 1分钟误差
    }

    @Test
    @DisplayName("从Token中提取权限列表 - 成功")
    void extractAuthorities_Success() {
        // When
        List<String> extractedAuthorities = jwtUtil.extractAuthorities(validToken);

        // Then
        assertThat(extractedAuthorities).isNotNull();
        assertThat(extractedAuthorities).containsExactlyInAnyOrder("ROLE_USER", "ROLE_STUDENT");
    }

    @Test
    @DisplayName("从Token中提取权限列表 - 刷新Token无权限")
    void extractAuthorities_RefreshToken_NoAuthorities() {
        // Given
        String refreshToken = jwtUtil.generateRefreshToken(username);

        // When
        List<String> extractedAuthorities = jwtUtil.extractAuthorities(refreshToken);

        // Then
        assertThat(extractedAuthorities).isNull();
    }

    @Test
    @DisplayName("检查Token是否过期 - 未过期")
    void isTokenExpired_NotExpired() {
        // When
        Boolean isExpired = jwtUtil.isTokenExpired(validToken);

        // Then
        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("检查Token是否过期 - 已过期")
    void isTokenExpired_Expired() {
        // Given - 创建一个已过期的token（过期时间设为-1毫秒）
        String expiredToken = createExpiredToken();

        // When
        Boolean isExpired = jwtUtil.isTokenExpired(expiredToken);

        // Then
        assertThat(isExpired).isTrue();
    }

    @Test
    @DisplayName("验证Token有效性 - 有效")
    void validateToken_Valid() {
        // When
        Boolean isValid = jwtUtil.validateToken(validToken, userDetails);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("验证Token有效性 - 用户名不匹配")
    void validateToken_UsernameMismatch() {
        // Given
        UserDetails differentUser = User.builder()
                .username("differentuser")
                .password("password")
                .authorities(authorities)
                .build();

        // When
        Boolean isValid = jwtUtil.validateToken(validToken, differentUser);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("验证Token有效性 - Token已过期")
    void validateToken_Expired() {
        // Given
        String expiredToken = createExpiredToken();

        // When
        Boolean isValid = jwtUtil.validateToken(expiredToken, userDetails);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("验证Token格式 - 有效格式")
    void validateTokenFormat_Valid() {
        // When
        Boolean isValid = jwtUtil.validateTokenFormat(validToken);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("验证Token格式 - 无效格式")
    void validateTokenFormat_Invalid() {
        // Given
        String invalidToken = "invalid.token.format";

        // When
        Boolean isValid = jwtUtil.validateTokenFormat(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("验证Token格式 - 空Token")
    void validateTokenFormat_Null() {
        // When & Then
        assertThatThrownBy(() -> jwtUtil.validateTokenFormat(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("验证Token格式 - 空字符串Token")
    void validateTokenFormat_EmptyString() {
        // When
        Boolean isValid = jwtUtil.validateTokenFormat("");

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("获取Token剩余有效时间 - 成功")
    void getTokenRemainingTime_Success() {
        // When
        Long remainingTime = jwtUtil.getTokenRemainingTime(validToken);

        // Then
        assertThat(remainingTime).isGreaterThan(0);
        assertThat(remainingTime).isLessThanOrEqualTo(3600L); // 不超过1小时
    }

    @Test
    @DisplayName("获取Token剩余有效时间 - 已过期")
    void getTokenRemainingTime_Expired() {
        // Given
        String expiredToken = createExpiredToken();

        // When
        Long remainingTime = jwtUtil.getTokenRemainingTime(expiredToken);

        // Then
        assertThat(remainingTime).isEqualTo(0L);
    }

    @Test
    @DisplayName("获取Token剩余有效时间 - 无效Token")
    void getTokenRemainingTime_InvalidToken() {
        // Given
        String invalidToken = "invalid.token";

        // When
        Long remainingTime = jwtUtil.getTokenRemainingTime(invalidToken);

        // Then
        assertThat(remainingTime).isEqualTo(0L);
    }

    @Test
    @DisplayName("获取Token过期时间 - 成功")
    void getExpirationTime_Success() {
        // When
        Date expirationTime = jwtUtil.getExpirationTime(validToken);

        // Then
        assertThat(expirationTime).isNotNull();
        assertThat(expirationTime).isAfter(new Date());
    }

    @Test
    @DisplayName("获取访问Token过期时间配置 - 成功")
    void getExpirationTime_Configuration() {
        // When
        Long expirationTime = jwtUtil.getExpirationTime();

        // Then
        assertThat(expirationTime).isEqualTo(3600L); // 1小时 = 3600秒
    }

    @Test
    @DisplayName("处理过期Token异常")
    void handleExpiredTokenException() {
        // Given
        String expiredToken = createExpiredToken();

        // When & Then
        assertThatThrownBy(() -> jwtUtil.extractUsername(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("处理格式错误Token异常")
    void handleMalformedTokenException() {
        // Given
        String malformedToken = "malformed.token";

        // When & Then
        assertThatThrownBy(() -> jwtUtil.extractUsername(malformedToken))
                .isInstanceOf(MalformedJwtException.class);
    }

    @Test
    @DisplayName("处理签名验证失败异常")
    void handleSecurityException() {
        // Given - 使用不同的密钥生成token
        String tokenWithWrongSignature = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTUxNjIzOTAyMn0.invalid_signature";

        // When & Then
        assertThatThrownBy(() -> jwtUtil.extractUsername(tokenWithWrongSignature))
                .isInstanceOf(SecurityException.class);
    }

    /**
     * 创建一个已过期的Token用于测试
     */
    private String createExpiredToken() {
        // 使用反射或者创建一个过期时间为过去时间的token
        // 这里简化处理，创建一个1毫秒就过期的token
        try {
            Thread.sleep(2); // 确保token过期
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 创建一个过期时间很短的token
        return io.jsonwebtoken.Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000)) // 2秒前签发
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // 1秒前过期
                .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 获取签名密钥（用于测试）
     */
    private javax.crypto.SecretKey getSigningKey() {
        String testSecret = "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tdGVzdGluZy1wdXJwb3Nlcy1vbmx5LXRoaXMtaXMtYS12ZXJ5LWxvbmctc2VjcmV0LWtleQ==";
        byte[] keyBytes = java.util.Base64.getDecoder().decode(testSecret);
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
    }
}