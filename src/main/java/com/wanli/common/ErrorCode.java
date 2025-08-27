package com.wanli.common;

/**
 * 错误码枚举
 * 定义系统中所有的错误码和错误消息
 * 
 * @author wanli
 * @version 1.0.0
 */
public enum ErrorCode {
    
    // 通用错误码 (1000-1999)
    SUCCESS("1000", "操作成功"),
    SYSTEM_ERROR("1001", "系统内部错误"),
    INVALID_PARAMETER("1002", "参数无效"),
    VALIDATION_ERROR("1003", "参数验证失败"),
    RESOURCE_NOT_FOUND("1004", "资源不存在"),
    METHOD_NOT_ALLOWED("1005", "请求方法不支持"),
    MEDIA_TYPE_NOT_SUPPORTED("1006", "媒体类型不支持"),
    
    // 用户相关错误码 (2000-2999)
    USER_NOT_FOUND("2001", "用户不存在"),
    DUPLICATE_USERNAME("2002", "用户名已存在"),
    DUPLICATE_EMAIL("2003", "邮箱已被注册"),
    INVALID_PASSWORD("2004", "密码不正确"),
    USER_LOCKED("2005", "用户账户已被锁定"),
    USER_INACTIVE("2006", "用户账户未激活"),
    USER_DELETED("2007", "用户账户已被删除"),
    PASSWORD_TOO_WEAK("2008", "密码强度不够"),
    
    // 认证授权错误码 (3000-3999)
    UNAUTHORIZED("3001", "未认证或认证已过期"),
    ACCESS_DENIED("3002", "访问被拒绝，权限不足"),
    INVALID_TOKEN("3003", "无效的令牌"),
    TOKEN_EXPIRED("3004", "令牌已过期"),
    LOGIN_FAILED("3005", "登录失败"),
    LOGOUT_FAILED("3006", "登出失败"),
    
    // 课程相关错误码 (4000-4999)
    COURSE_NOT_FOUND("4001", "课程不存在"),
    COURSE_ALREADY_EXISTS("4002", "课程已存在"),
    COURSE_FULL("4003", "课程已满员"),
    COURSE_NOT_AVAILABLE("4004", "课程不可用"),
    
    // 课时相关错误码 (5000-5999)
    LESSON_NOT_FOUND("5001", "课时不存在"),
    LESSON_ALREADY_EXISTS("5002", "课时已存在"),
    LESSON_NOT_AVAILABLE("5003", "课时不可用"),
    
    // 数据库相关错误码 (6000-6999)
    DATABASE_ERROR("6001", "数据库操作失败"),
    DATA_INTEGRITY_VIOLATION("6002", "数据完整性约束违反"),
    DUPLICATE_KEY("6003", "数据重复"),
    
    // 外部服务错误码 (7000-7999)
    EXTERNAL_SERVICE_ERROR("7001", "外部服务调用失败"),
    NETWORK_ERROR("7002", "网络连接错误"),
    TIMEOUT_ERROR("7003", "请求超时"),
    
    // 文件相关错误码 (8000-8999)
    FILE_NOT_FOUND("8001", "文件不存在"),
    FILE_UPLOAD_FAILED("8002", "文件上传失败"),
    FILE_TYPE_NOT_SUPPORTED("8003", "文件类型不支持"),
    FILE_SIZE_EXCEEDED("8004", "文件大小超出限制");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}