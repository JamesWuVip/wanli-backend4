# 异常处理规范文档

## 1. 文档概述

### 1.1 文档目的

本文档定义了Wanli Academy项目中异常处理的标准规范，包括异常分类、处理策略、错误码定义和最佳实践，确保系统异常处理的一致性和用户体验。

### 1.2 适用范围

* 业务异常处理

* 系统异常处理

* API错误响应

* 日志记录规范

* 用户友好错误信息

### 1.3 版本信息

* 文档版本：v1.0.0

* Spring Boot版本：3.5.x

* 创建日期：2025-01-16

## 2. 异常分类体系

### 2.1 异常层次结构

```
Throwable
├── Error (系统错误，不处理)
└── Exception
    ├── RuntimeException
    │   ├── BusinessException (业务异常)
    │   │   ├── UserNotFoundException
    │   │   ├── DuplicateUsernameException
    │   │   ├── InvalidPasswordException
    │   │   └── InsufficientPermissionException
    │   ├── ValidationException (验证异常)
    │   └── SystemException (系统运行时异常)
    └── CheckedException (检查异常)
        ├── DatabaseException
        └── ExternalServiceException
```

### 2.2 异常分类说明

#### 业务异常 (BusinessException)

* **定义**: 业务逻辑相关的异常

* **特点**: 可预期、用户可理解、需要友好提示

* **处理**: 返回具体错误信息给用户

#### 系统异常 (SystemException)

* **定义**: 系统运行时异常

* **特点**: 不可预期、技术性错误

* **处理**: 记录详细日志，返回通用错误信息

#### 验证异常 (ValidationException)

* **定义**: 参数验证失败异常

* **特点**: 输入数据不符合要求

* **处理**: 返回具体验证失败信息

## 3. 异常类定义

### 3.1 基础异常类

```java
/**
 * 基础异常类
 */
public abstract class BaseException extends RuntimeException {
    
    private final String errorCode;
    private final String errorMessage;
    private final Object[] args;
    
    public BaseException(String errorCode, String errorMessage, Object... args) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.args = args;
    }
    
    public BaseException(String errorCode, String errorMessage, Throwable cause, Object... args) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.args = args;
    }
    
    // Getters
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public Object[] getArgs() { return args; }
}
```

### 3.2 业务异常类

```java
/**
 * 业务异常基类
 */
public class BusinessException extends BaseException {
    
    public BusinessException(String errorCode, String errorMessage, Object... args) {
        super(errorCode, errorMessage, args);
    }
    
    public BusinessException(String errorCode, String errorMessage, Throwable cause, Object... args) {
        super(errorCode, errorMessage, cause, args);
    }
}

/**
 * 用户相关异常
 */
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String username) {
        super("USER_NOT_FOUND", "用户不存在: {0}", username);
    }
    
    public UserNotFoundException(Long userId) {
        super("USER_NOT_FOUND", "用户不存在: ID={0}", userId);
    }
}

public class DuplicateUsernameException extends BusinessException {
    public DuplicateUsernameException(String username) {
        super("DUPLICATE_USERNAME", "用户名已存在: {0}", username);
    }
}

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String email) {
        super("DUPLICATE_EMAIL", "邮箱已被注册: {0}", email);
    }
}

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException() {
        super("INVALID_PASSWORD", "密码不正确");
    }
    
    public InvalidPasswordException(String reason) {
        super("INVALID_PASSWORD", "密码不符合要求: {0}", reason);
    }
}

public class InsufficientPermissionException extends BusinessException {
    public InsufficientPermissionException(String operation) {
        super("INSUFFICIENT_PERMISSION", "权限不足，无法执行操作: {0}", operation);
    }
}

/**
 * 课程相关异常
 */
public class CourseNotFoundException extends BusinessException {
    public CourseNotFoundException(Long courseId) {
        super("COURSE_NOT_FOUND", "课程不存在: ID={0}", courseId);
    }
}

public class CourseAccessDeniedException extends BusinessException {
    public CourseAccessDeniedException(Long courseId) {
        super("COURSE_ACCESS_DENIED", "无权访问课程: ID={0}", courseId);
    }
}

/**
 * 课时相关异常
 */
public class LessonNotFoundException extends BusinessException {
    public LessonNotFoundException(Long lessonId) {
        super("LESSON_NOT_FOUND", "课时不存在: ID={0}", lessonId);
    }
}
```

### 3.3 系统异常类

```java
/**
 * 系统异常基类
 */
public class SystemException extends BaseException {
    
    public SystemException(String errorCode, String errorMessage, Object... args) {
        super(errorCode, errorMessage, args);
    }
    
    public SystemException(String errorCode, String errorMessage, Throwable cause, Object... args) {
        super(errorCode, errorMessage, cause, args);
    }
}

/**
 * 数据库异常
 */
public class DatabaseException extends SystemException {
    public DatabaseException(String operation, Throwable cause) {
        super("DATABASE_ERROR", "数据库操作失败: {0}", cause, operation);
    }
}

/**
 * 外部服务异常
 */
public class ExternalServiceException extends SystemException {
    public ExternalServiceException(String serviceName, Throwable cause) {
        super("EXTERNAL_SERVICE_ERROR", "外部服务调用失败: {0}", cause, serviceName);
    }
}

/**
 * 配置异常
 */
public class ConfigurationException extends SystemException {
    public ConfigurationException(String configKey) {
        super("CONFIGURATION_ERROR", "配置错误: {0}", configKey);
    }
}
```

### 3.4 验证异常类

```java
/**
 * 验证异常基类
 */
public class ValidationException extends BaseException {
    
    private final List<FieldError> fieldErrors;
    
    public ValidationException(String errorMessage) {
        super("VALIDATION_ERROR", errorMessage);
        this.fieldErrors = new ArrayList<>();
    }
    
    public ValidationException(List<FieldError> fieldErrors) {
        super("VALIDATION_ERROR", "参数验证失败");
        this.fieldErrors = fieldErrors;
    }
    
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
    
    public static class FieldError {
        private final String field;
        private final String message;
        private final Object rejectedValue;
        
        public FieldError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }
        
        // Getters
        public String getField() { return field; }
        public String getMessage() { return message; }
        public Object getRejectedValue() { return rejectedValue; }
    }
}
```

## 4. 错误码定义

### 4.1 错误码规范

* **格式**: `{模块}_{类型}_{具体错误}`

* **模块**: USER, COURSE, LESSON, AUTH, SYSTEM

* **类型**: NOT\_FOUND, INVALID, DUPLICATE, ACCESS\_DENIED, ERROR

### 4.2 错误码清单

```java
public enum ErrorCode {
    
    // 通用错误码 (1000-1999)
    SUCCESS("0000", "操作成功"),
    SYSTEM_ERROR("1000", "系统内部错误"),
    INVALID_PARAMETER("1001", "参数无效"),
    VALIDATION_ERROR("1002", "参数验证失败"),
    UNAUTHORIZED("1003", "未授权访问"),
    FORBIDDEN("1004", "访问被禁止"),
    NOT_FOUND("1005", "资源不存在"),
    METHOD_NOT_ALLOWED("1006", "请求方法不允许"),
    REQUEST_TIMEOUT("1007", "请求超时"),
    TOO_MANY_REQUESTS("1008", "请求过于频繁"),
    
    // 用户相关错误码 (2000-2999)
    USER_NOT_FOUND("2000", "用户不存在"),
    DUPLICATE_USERNAME("2001", "用户名已存在"),
    DUPLICATE_EMAIL("2002", "邮箱已被注册"),
    INVALID_PASSWORD("2003", "密码不正确"),
    USER_DISABLED("2004", "用户已被禁用"),
    USER_LOCKED("2005", "用户已被锁定"),
    PASSWORD_EXPIRED("2006", "密码已过期"),
    INSUFFICIENT_PERMISSION("2007", "权限不足"),
    
    // 认证相关错误码 (3000-3999)
    INVALID_TOKEN("3000", "Token无效"),
    TOKEN_EXPIRED("3001", "Token已过期"),
    TOKEN_NOT_FOUND("3002", "Token不存在"),
    INVALID_CREDENTIALS("3003", "用户名或密码错误"),
    LOGIN_FAILED("3004", "登录失败"),
    LOGOUT_FAILED("3005", "登出失败"),
    
    // 课程相关错误码 (4000-4999)
    COURSE_NOT_FOUND("4000", "课程不存在"),
    COURSE_ACCESS_DENIED("4001", "无权访问课程"),
    COURSE_ALREADY_EXISTS("4002", "课程已存在"),
    COURSE_NOT_PUBLISHED("4003", "课程未发布"),
    COURSE_FULL("4004", "课程已满员"),
    
    // 课时相关错误码 (5000-5999)
    LESSON_NOT_FOUND("5000", "课时不存在"),
    LESSON_ACCESS_DENIED("5001", "无权访问课时"),
    LESSON_NOT_AVAILABLE("5002", "课时不可用"),
    
    // 数据库相关错误码 (6000-6999)
    DATABASE_ERROR("6000", "数据库操作失败"),
    DATA_INTEGRITY_VIOLATION("6001", "数据完整性约束违反"),
    DUPLICATE_KEY("6002", "数据重复"),
    
    // 外部服务错误码 (7000-7999)
    EXTERNAL_SERVICE_ERROR("7000", "外部服务调用失败"),
    SERVICE_UNAVAILABLE("7001", "服务不可用"),
    
    // 文件相关错误码 (8000-8999)
    FILE_NOT_FOUND("8000", "文件不存在"),
    FILE_UPLOAD_FAILED("8001", "文件上传失败"),
    FILE_SIZE_EXCEEDED("8002", "文件大小超出限制"),
    INVALID_FILE_TYPE("8003", "文件类型不支持");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() { return code; }
    public String getMessage() { return message; }
}
```

## 5. 全局异常处理器

### 5.1 全局异常处理器实现

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @Autowired
    private MessageSource messageSource;
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("Business exception occurred: {}", e.getMessage(), e);
        
        ApiResponse<Void> response = ApiResponse.error(
            e.getErrorCode(),
            formatMessage(e.getErrorMessage(), e.getArgs())
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理验证异常
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(ValidationException e) {
        log.warn("Validation exception occurred: {}", e.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        e.getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getMessage())
        );
        
        ApiResponse<Map<String, String>> response = ApiResponse.error(
            e.getErrorCode(),
            e.getErrorMessage(),
            errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理Bean Validation异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {
        log.warn("Method argument validation failed: {}", e.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ApiResponse<Map<String, String>> response = ApiResponse.error(
            ErrorCode.VALIDATION_ERROR.getCode(),
            "参数验证失败",
            errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(
            ConstraintViolationException e) {
        log.warn("Constraint violation occurred: {}", e.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(fieldName, message);
        });
        
        ApiResponse<Map<String, String>> response = ApiResponse.error(
            ErrorCode.VALIDATION_ERROR.getCode(),
            "参数验证失败",
            errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication failed: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            ErrorCode.UNAUTHORIZED.getCode(),
            "认证失败"
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    /**
     * 处理授权异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            ErrorCode.FORBIDDEN.getCode(),
            "访问被拒绝"
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    
    /**
     * 处理数据库异常
     */
    @ExceptionHandler({DataAccessException.class, DatabaseException.class})
    public ResponseEntity<ApiResponse<Void>> handleDatabaseException(Exception e) {
        log.error("Database exception occurred: {}", e.getMessage(), e);
        
        ApiResponse<Void> response = ApiResponse.error(
            ErrorCode.DATABASE_ERROR.getCode(),
            "数据库操作失败"
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理数据完整性违反异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("Data integrity violation: {}", e.getMessage(), e);
        
        String message = "数据操作失败";
        if (e.getCause() instanceof ConstraintViolationException) {
            message = "数据重复或违反约束条件";
        }
        
        ApiResponse<Void> response = ApiResponse.error(
            ErrorCode.DATA_INTEGRITY_VIOLATION.getCode(),
            message
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    /**
     * 处理HTTP请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("Method not supported: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            ErrorCode.METHOD_NOT_ALLOWED.getCode(),
            "请求方法不支持: " + e.getMethod()
        );
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }
    
    /**
     * 处理HTTP媒体类型不支持异常
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        log.warn("Media type not supported: {}", e.getMessage());
        
        ApiResponse<Void> response = ApiResponse.error(
            ErrorCode.INVALID_PARAMETER.getCode(),
            "不支持的媒体类型: " + e.getContentType()
        );
        
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }
    
    /**
     * 处理系统异常
     */
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleSystemException(SystemException e) {
        log.error("System exception occurred: {}", e.getMessage(), e);
        
        ApiResponse<Void> response = ApiResponse.error(
            e.getErrorCode(),
            "系统内部错误"
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
        log.error("Unexpected exception occurred: {}", e.getMessage(), e);
        
        ApiResponse<Void> response = ApiResponse.error(
            ErrorCode.SYSTEM_ERROR.getCode(),
            "系统内部错误"
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 格式化错误消息
     */
    private String formatMessage(String message, Object[] args) {
        if (args == null || args.length == 0) {
            return message;
        }
        return MessageFormat.format(message, args);
    }
}
```

### 5.2 统一响应格式

```java
/**
 * 统一API响应格式
 */
public class ApiResponse<T> {
    
    private boolean success;
    private String code;
    private String message;
    private T data;
    private Long timestamp;
    
    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    // 成功响应
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, ErrorCode.SUCCESS.getCode(), "操作成功", data);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, ErrorCode.SUCCESS.getCode(), message, data);
    }
    
    // 失败响应
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
    
    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }
    
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public Long getTimestamp() { return timestamp; }
}
```

## 6. 异常处理最佳实践

### 6.1 Service层异常处理

```java
@Service
@Transactional
@Slf4j
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 用户注册
     */
    public UserResponse registerUser(RegisterRequest request) {
        try {
            // 验证用户名是否已存在
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateUsernameException(request.getUsername());
            }
            
            // 验证邮箱是否已被注册
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEmailException(request.getEmail());
            }
            
            // 创建用户
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(UserRole.STUDENT);
            user.setStatus(UserStatus.ACTIVE);
            
            User savedUser = userRepository.save(user);
            log.info("User registered successfully: {}", savedUser.getUsername());
            
            return UserResponse.from(savedUser);
            
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (DataAccessException e) {
            // 数据库异常转换为系统异常
            log.error("Database error during user registration: {}", e.getMessage(), e);
            throw new DatabaseException("用户注册", e);
        } catch (Exception e) {
            // 其他未知异常
            log.error("Unexpected error during user registration: {}", e.getMessage(), e);
            throw new SystemException("SYSTEM_ERROR", "用户注册失败", e);
        }
    }
    
    /**
     * 用户登录
     */
    public AuthResponse loginUser(LoginRequest request) {
        try {
            // 查找用户
            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException(request.getUsername()));
            
            // 检查用户状态
            if (user.getStatus() == UserStatus.DISABLED) {
                throw new BusinessException("USER_DISABLED", "用户已被禁用");
            }
            
            if (user.getStatus() == UserStatus.LOCKED) {
                throw new BusinessException("USER_LOCKED", "用户已被锁定");
            }
            
            // 验证密码
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new InvalidPasswordException();
            }
            
            // 生成Token
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
            
            log.info("User logged in successfully: {}", user.getUsername());
            
            return new AuthResponse(accessToken, refreshToken);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during user login: {}", e.getMessage(), e);
            throw new SystemException("LOGIN_ERROR", "登录失败", e);
        }
    }
    
    /**
     * 获取用户信息
     */
    public UserResponse getUserById(Long userId) {
        return userRepository.findById(userId)
            .map(UserResponse::from)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
    
    /**
     * 更新用户信息
     */
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
            
            // 检查邮箱是否被其他用户使用
            if (!user.getEmail().equals(request.getEmail()) && 
                userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEmailException(request.getEmail());
            }
            
            // 更新用户信息
            user.setEmail(request.getEmail());
            user.setUpdatedAt(LocalDateTime.now());
            
            User updatedUser = userRepository.save(user);
            log.info("User updated successfully: {}", updatedUser.getUsername());
            
            return UserResponse.from(updatedUser);
            
        } catch (BusinessException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error during user update: {}", e.getMessage(), e);
            throw new DatabaseException("用户更新", e);
        } catch (Exception e) {
            log.error("Unexpected error during user update: {}", e.getMessage(), e);
            throw new SystemException("SYSTEM_ERROR", "用户更新失败", e);
        }
    }
}
```

### 6.2 Controller层异常处理

```java
@RestController
@RequestMapping("/api/users")
@Validated
@Slf4j
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        
        UserResponse user = userService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success("注册成功", user));
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        
        AuthResponse auth = userService.loginUser(request);
        return ResponseEntity.ok(ApiResponse.success("登录成功", auth));
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        
        String username = authentication.getName();
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        UserResponse user = userService.updateUserByUsername(username, request);
        return ResponseEntity.ok(ApiResponse.success("更新成功", user));
    }
}
```

## 7. 日志记录规范

### 7.1 日志级别使用规范

* **ERROR**: 系统错误、异常情况

* **WARN**: 业务异常、警告信息

* **INFO**: 重要业务操作、状态变更

* **DEBUG**: 调试信息、详细执行流程

### 7.2 异常日志记录示例

```java
@Component
@Slf4j
public class ExceptionLogger {
    
    /**
     * 记录业务异常
     */
    public void logBusinessException(BusinessException e, String operation) {
        log.warn("Business exception in {}: [{}] {}", 
            operation, e.getErrorCode(), e.getMessage());
    }
    
    /**
     * 记录系统异常
     */
    public void logSystemException(SystemException e, String operation) {
        log.error("System exception in {}: [{}] {}", 
            operation, e.getErrorCode(), e.getMessage(), e);
    }
    
    /**
     * 记录验证异常
     */
    public void logValidationException(ValidationException e, String operation) {
        log.warn("Validation exception in {}: {}", operation, e.getMessage());
        e.getFieldErrors().forEach(error -> 
            log.warn("Field validation error - {}: {}", error.getField(), error.getMessage())
        );
    }
    
    /**
     * 记录未知异常
     */
    public void logUnknownException(Exception e, String operation) {
        log.error("Unknown exception in {}: {}", operation, e.getMessage(), e);
    }
}
```

### 7.3 结构化日志配置

```xml
<!-- logback-spring.xml -->
<configuration>
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>/var/log/wanli/application.log</file>
            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <timestamp/>
                    <logLevel/>
                    <loggerName/>
                    <message/>
                    <mdc/>
                    <stackTrace/>
                    <pattern>
                        <pattern>
                            {
                                "traceId": "%X{traceId:-}",
                                "spanId": "%X{spanId:-}",
                                "service": "wanli-academy",
                                "environment": "${ENVIRONMENT:-unknown}"
                            }
                        </pattern>
                    </pattern>
                </providers>
            </encoder>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>/var/log/wanli/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
                <maxFileSize>100MB</maxFileSize>
                <maxHistory>30</maxHistory>
                <totalSizeCap>3GB</totalSizeCap>
            </rollingPolicy>
        </appender>
        
        <root level="INFO">
            <appender-ref ref="FILE"/>
        </root>
    </springProfile>
</configuration>
```

## 8. 监控和告警

### 8.1 异常监控指标

```java
@Component
public class ExceptionMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter businessExceptionCounter;
    private final Counter systemExceptionCounter;
    private final Counter validationExceptionCounter;
    
    public ExceptionMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.businessExceptionCounter = Counter.builder("exception.business")
            .description("Business exceptions count")
            .register(meterRegistry);
        this.systemExceptionCounter = Counter.builder("exception.system")
            .description("System exceptions count")
            .register(meterRegistry);
        this.validationExceptionCounter = Counter.builder("exception.validation")
            .description("Validation exceptions count")
            .register(meterRegistry);
    }
    
    public void recordBusinessException(String errorCode) {
        businessExceptionCounter.increment(
            Tags.of("error_code", errorCode)
        );
    }
    
    public void recordSystemException(String errorCode) {
        systemExceptionCounter.increment(
            Tags.of("error_code", errorCode)
        );
    }
    
    public void recordValidationException() {
        validationExceptionCounter.increment();
    }
}
```

### 8.2 告警规则配置

```yaml
# prometheus alerts
groups:
  - name: wanli-academy-exceptions
    rules:
      - alert: HighBusinessExceptionRate
        expr: rate(exception_business_total[5m]) > 0.1
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High business exception rate detected"
          description: "Business exception rate is {{ $value }} per second"
      
      - alert: SystemExceptionOccurred
        expr: increase(exception_system_total[1m]) > 0
        for: 0m
        labels:
          severity: critical
        annotations:
          summary: "System exception occurred"
          description: "{{ $value }} system exceptions in the last minute"
      
      - alert: HighValidationExceptionRate
        expr: rate(exception_validation_total[5m]) > 0.5
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High validation exception rate"
          description: "Validation exception rate is {{ $value }} per second"
```

## 9. 测试策略

### 9.1 异常处理测试

```java
@ExtendWith(MockitoExtension.class)
class UserServiceExceptionTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("用户注册 - 用户名已存在异常")
    void registerUser_DuplicateUsername_ThrowsException() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);
        
        // When & Then
        DuplicateUsernameException exception = assertThrows(
            DuplicateUsernameException.class,
            () -> userService.registerUser(request)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo("DUPLICATE_USERNAME");
        assertThat(exception.getMessage()).contains("existinguser");
    }
    
    @Test
    @DisplayName("用户登录 - 用户不存在异常")
    void loginUser_UserNotFound_ThrowsException() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistentuser");
        request.setPassword("password123");
        
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
        
        // When & Then
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> userService.loginUser(request)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(exception.getMessage()).contains("nonexistentuser");
    }
    
    @Test
    @DisplayName("数据库异常处理")
    void registerUser_DatabaseException_ThrowsSystemException() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenThrow(new DataAccessException("DB Error") {});
        
        // When & Then
        DatabaseException exception = assertThrows(
            DatabaseException.class,
            () -> userService.registerUser(request)
        );
        
        assertThat(exception.getErrorCode()).isEqualTo("DATABASE_ERROR");
    }
}
```

### 9.2 全局异常处理器测试

```java
@SpringBootTest
@AutoConfigureTestDatabase
class GlobalExceptionHandlerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("业务异常处理测试")
    void handleBusinessException() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setEmail("existing@example.com");
        request.setPassword("password123");
        
        // 先注册一个用户
        restTemplate.postForEntity("/api/auth/register", request, ApiResponse.class);
        
        // When - 尝试注册相同用户名
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
            "/api/auth/register", request, ApiResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getCode()).isEqualTo("DUPLICATE_USERNAME");
    }
    
    @Test
    @DisplayName("参数验证异常处理测试")
    void handleValidationException() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername(""); // 空用户名
        request.setEmail("invalid-email"); // 无效邮箱
        request.setPassword("123"); // 密码太短
        
        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
            "/api/auth/register", request, ApiResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getCode()).isEqualTo("VALIDATION_ERROR");
    }
}
```

## 10. 部署和运维

### 10.1 生产环境配置

```yaml
# application-prod.yml
logging:
  level:
    com.wanli.exception: INFO
    org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId:-},%X{spanId:-}] %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId:-},%X{spanId:-}] %logger{36} - %msg%n"

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    tags:
      application: wanli-academy
      environment: production
```

### 10.2 异常处理运维检查清单

* [ ] 异常日志正常记录

* [ ] 监控指标正常上报

* [ ] 告警规则正确配置

* [ ] 错误响应格式统一

* [ ] 敏感信息不泄露

* [ ] 异常堆栈不暴露给用户

* [ ] 数据库连接异常恢复机制

* [ ] 外部服务异常降级策略

***

**文档维护**

* 负责人：开发团队

* 更新频率：随异常处理策略变更更新

* 审核周期：每月审核一次

