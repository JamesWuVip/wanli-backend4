package com.wanli.common;

/**
 * 系统常量
 */
public class Constants {
    
    /**
     * JWT相关常量
     */
    public static class JWT {
        public static final String SECRET_KEY = "wanli-backend-jwt-secret-key-2024";
        public static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000L; // 24小时
        public static final String TOKEN_PREFIX = "Bearer ";
        public static final String HEADER_STRING = "Authorization";
    }
    
    /**
     * 用户相关常量
     */
    public static class User {
        public static final String DEFAULT_PASSWORD = "123456";
        public static final int USERNAME_MIN_LENGTH = 3;
        public static final int USERNAME_MAX_LENGTH = 20;
        public static final int PASSWORD_MIN_LENGTH = 6;
        public static final int PASSWORD_MAX_LENGTH = 20;
    }
    
    /**
     * 系统相关常量
     */
    public static class System {
        public static final String DEFAULT_TIMEZONE = "Asia/Shanghai";
        public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    }
}