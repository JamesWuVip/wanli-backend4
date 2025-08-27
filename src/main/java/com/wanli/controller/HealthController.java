package com.wanli.controller;

import com.wanli.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 
 * @author wanli
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "系统监控", description = "系统健康检查和信息接口")
public class HealthController implements HealthIndicator {
    
    private static final Logger log = LoggerFactory.getLogger(HealthController.class);
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查系统运行状态")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        
        log.debug("Health check requested");
        
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("service", "wanli-backend");
        healthInfo.put("version", "1.0.0");
        
        return ResponseEntity.ok(ApiResponse.success("系统运行正常", healthInfo));
    }
    
    /**
     * 系统信息接口
     */
    @GetMapping("/info")
    @Operation(summary = "系统信息", description = "获取系统基本信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> info() {
        
        log.debug("System info requested");
        
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("application", "万里后端系统");
        systemInfo.put("version", "1.0.0");
        systemInfo.put("springBootVersion", "3.5.0");
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResponse.success("获取系统信息成功", systemInfo));
    }
    
    @Override
    public Health health() {
        return Health.up()
                .withDetail("service", "wanli-backend")
                .withDetail("version", "1.0.0")
                .withDetail("timestamp", LocalDateTime.now())
                .build();
    }
}