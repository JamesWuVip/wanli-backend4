package com.wanli.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanli.common.ApiResponse;
import com.wanli.common.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT认证入口点
 * 处理未认证的请求
 * 
 * @author wanli
 * @version 1.0.0
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        ApiResponse<Void> apiResponse = ApiResponse.error(
            ErrorCode.UNAUTHORIZED.getCode(),
            ErrorCode.UNAUTHORIZED.getMessage()
        );
        
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}