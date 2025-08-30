package com.wanli.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token黑名单服务
 * 用于管理已登出的JWT Token
 * 
 * @author wanli
 * @version 1.0.0
 */
@Service
public class TokenBlacklistService {
    
    // 使用内存存储黑名单Token（生产环境建议使用Redis）
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    /**
     * 将Token加入黑名单
     * @param token JWT Token
     */
    public void blacklistToken(String token) {
        if (token != null && !token.trim().isEmpty()) {
            blacklistedTokens.add(token);
        }
    }
    
    /**
     * 检查Token是否在黑名单中
     * @param token JWT Token
     * @return 是否在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        return token != null && blacklistedTokens.contains(token);
    }
    
    /**
     * 清理黑名单（可用于定期清理过期Token）
     */
    public void clearBlacklist() {
        blacklistedTokens.clear();
    }
    
    /**
     * 获取黑名单大小
     * @return 黑名单Token数量
     */
    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }
}