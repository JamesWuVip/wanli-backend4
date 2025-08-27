package com.wanli.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanli.common.ApiResponse;
import com.wanli.common.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * JwtAuthenticationEntryPoint单元测试
 * 
 * @author wanli
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws IOException {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    @DisplayName("处理未认证请求 - 返回401错误响应")
    void commence_ShouldReturnUnauthorizedResponse() throws Exception {
        // Given
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/users");
        when(authException.getMessage()).thenReturn("Authentication failed");

        // When
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Then
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // 验证响应内容
        printWriter.flush();
        String responseContent = stringWriter.toString();
        
        assertThat(responseContent).contains("\"success\":false");
        assertThat(responseContent).contains("\"code\":\"" + ErrorCode.AUTH_TOKEN_INVALID.getCode() + "\"");
        assertThat(responseContent).contains("\"message\":\"" + ErrorCode.AUTH_TOKEN_INVALID.getMessage() + "\"");
    }

    @Test
    @DisplayName("处理未认证请求 - POST请求")
    void commence_WithPostRequest_ShouldReturnUnauthorizedResponse() throws Exception {
        // Given
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/users");
        when(authException.getMessage()).thenReturn("Token expired");

        // When
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Then
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // 验证响应内容
        printWriter.flush();
        String responseContent = stringWriter.toString();
        
        assertThat(responseContent).contains("\"success\":false");
        assertThat(responseContent).contains("\"code\":\"" + ErrorCode.AUTH_TOKEN_INVALID.getCode() + "\"");
    }

    @Test
    @DisplayName("处理未认证请求 - 验证日志记录")
    void commence_ShouldLogUnauthorizedAccess() throws Exception {
        // Given
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/api/admin/users/123");
        when(authException.getMessage()).thenReturn("Invalid token");

        // When
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Then
        verify(request).getMethod();
        verify(request).getRequestURI();
        verify(authException).getMessage();
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("处理未认证请求 - 验证响应格式")
    void commence_ShouldReturnCorrectJsonFormat() throws Exception {
        // Given
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/api/profile");
        when(authException.getMessage()).thenReturn("No token provided");

        // When
        jwtAuthenticationEntryPoint.commence(request, response, authException);

        // Then
        printWriter.flush();
        String responseContent = stringWriter.toString();
        
        // 验证JSON格式正确
        assertThat(responseContent).isNotEmpty();
        assertThat(responseContent).contains("\"success\":false");
        assertThat(responseContent).contains("\"code\":");
        assertThat(responseContent).contains("\"message\":");
        
        // 验证JSON格式有效
        assertThat(responseContent).startsWith("{");
        assertThat(responseContent).endsWith("}");
    }
}