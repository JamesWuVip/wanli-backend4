package com.wanli.controller;

import com.wanli.common.ApiResponse;
import com.wanli.config.JwtUtil;
import com.wanli.security.CustomUserDetailsService;
import com.wanli.security.JwtAuthenticationEntryPoint;
import com.wanli.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * HealthController单元测试
 * 
 * @author wanli
 * @version 1.0.0
 */
@WebMvcTest(HealthController.class)
@TestPropertySource(properties = {
        "spring.application.name=test-app",
        "spring.profiles.active=test"
})
@AutoConfigureMockMvc(addFilters = false)
class HealthControllerTest {

    @MockBean
    private JwtUtil jwtUtil;
    
    @MockBean
    private CustomUserDetailsService customUserDetailsService;
    
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("健康检查接口 - 返回系统运行状态")
    void health_ShouldReturnSystemStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("系统运行正常"))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.application").value("test-app"))
                .andExpect(jsonPath("$.data.profile").value("test"))
                .andExpect(jsonPath("$.data.version").value("1.0.0"))
                .andExpect(jsonPath("$.data.timestamp").exists());
    }

    @Test
    @DisplayName("系统信息接口 - 返回系统基本信息")
    void info_ShouldReturnSystemInfo() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取系统信息成功"))
                .andExpect(jsonPath("$.data.application").value("test-app"))
                .andExpect(jsonPath("$.data.profile").value("test"))
                .andExpect(jsonPath("$.data.version").value("1.0.0"))
                .andExpect(jsonPath("$.data.javaVersion").exists())
                .andExpect(jsonPath("$.data.osName").exists())
                .andExpect(jsonPath("$.data.osVersion").exists())
                .andExpect(jsonPath("$.data.timestamp").exists());
    }

    @Test
    @DisplayName("健康检查接口 - 验证响应格式")
    void health_ShouldReturnCorrectResponseFormat() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.status").isString())
                .andExpect(jsonPath("$.data.application").isString())
                .andExpect(jsonPath("$.data.profile").isString())
                .andExpect(jsonPath("$.data.version").isString());
    }

    @Test
    @DisplayName("系统信息接口 - 验证响应格式")
    void info_ShouldReturnCorrectResponseFormat() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").isBoolean())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.data").isMap())
                .andExpect(jsonPath("$.data.application").isString())
                .andExpect(jsonPath("$.data.profile").isString())
                .andExpect(jsonPath("$.data.version").isString())
                .andExpect(jsonPath("$.data.javaVersion").isString())
                .andExpect(jsonPath("$.data.osName").isString())
                .andExpect(jsonPath("$.data.osVersion").isString());
    }
}