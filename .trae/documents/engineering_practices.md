# 万里书院项目 - 开发最佳实践指南

## 文档概述

**创建时间**: 2025年1月25日\
**适用范围**: 万里书院后端项目开发团队\
**文档目的**: 提供统一的开发规范和最佳实践，避免常见错误\
**文档状态**: 正式版本

***

## 1. 核心开发原则

### 1.1 设计优先原则

* **先设计后编码**: 任何功能开发前必须先完成设计文档
* **统一技术规范**: 所有开发必须遵循统一的技术标准
* **增量式开发**: 采用小步快跑的增量开发策略
* **质量第一**: 代码质量优于开发速度

### 1.2 一致性原则

* **数据类型一致**: 统一使用UUID主键，OffsetDateTime时间类型
* **命名规范一致**: 遵循统一的命名规范
* **API设计一致**: 统一的请求响应格式
* **错误处理一致**: 统一的异常处理机制

***

## 2. 技术规范标准

### 2.1 Spring Boot 3.5 技术栈

#### 2.1.1 核心依赖版本

```xml
<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.5.0</spring-boot.version>
    <spring-framework.version>6.2.0</spring-framework.version>
    <spring-security.version>6.4.0</spring-security.version>
    <spring-data-jpa.version>3.4.0</spring-data-jpa.version>
    <jakarta-validation.version>3.1.0</jakarta-validation.version>
    <springdoc-openapi.version>2.7.0</springdoc-openapi.version>
    <testcontainers.version>1.20.4</testcontainers.version>
    <mapstruct.version>1.6.3</mapstruct.version>
</properties>
```

#### 2.1.2 必需依赖清单

**核心依赖**:

* `spring-boot-starter-web` - Web应用基础
* `spring-boot-starter-data-jpa` - JPA数据访问
* `spring-boot-starter-security` - 安全框架
* `spring-boot-starter-validation` - 数据验证
* `spring-boot-starter-actuator` - 监控端点

**数据库依赖**:

* `postgresql` - PostgreSQL驱动
* `HikariCP` - 连接池（Spring Boot默认）

**文档和工具**:

* `springdoc-openapi-starter-webmvc-ui` - API文档
* `mapstruct` - DTO映射工具

**测试依赖**:

* `spring-boot-starter-test` - 测试框架
* `spring-security-test` - 安全测试
* `testcontainers` - 集成测试

### 2.2 数据类型规范

#### 2.2.1 主键类型规范

**实体类主键**:

```java
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // 其他字段...
}
```

**API传输层**:

```java
// Request DTO
public record UserCreateRequest(
    @NotBlank String username,
    @Email String email,
    @Size(min = 6) String password
) {}

// Response DTO
public record UserResponse(
    String id,  // UUID转String
    String username,
    String email,
    String role,
    OffsetDateTime createdAt
) {}
```

**Controller层处理**:

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        UUID userId = UUID.fromString(id);
        // 业务逻辑处理...
    }
}
```

#### 2.2.2 时间类型规范

**实体类时间字段**:

```java
@Entity
public class BaseEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
```

**API传输格式**:

```java
// 使用ISO 8601格式
"createdAt": "2025-01-25T10:00:00.000+00:00"
```

### 2.3 枚举类型规范

#### 2.3.1 标准枚举定义

```java
public enum UserRole {
    ROLE_PLATFORM_ADMIN("平台管理员"),
    ROLE_HQ_TEACHER("总部教师"),
    ROLE_FRANCHISE_ADMIN("门店管理员"),
    ROLE_FRANCHISE_TEACHER("门店教师"),
    ROLE_STUDENT("学员");
    
    private final String description;
    
    UserRole(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
```

#### 2.3.2 业务状态枚举

```java
public enum EntityStatus {
    DRAFT("草稿"),
    PUBLISHED("已发布"),
    ARCHIVED("已归档"),
    DELETED("已删除");
    
    private final String description;
    
    EntityStatus(String description) {
        this.description = description;
    }
}

public enum LessonType {
    THEORY("理论课"),
    PRACTICE("实践课"),
    REVIEW("复习课"),
    EXAM("考试课"),
    DISCUSSION("讨论课"),
    LAB("实验课");
    
    private final String description;
}
```

***

## 3. 代码结构规范

### 3.1 项目结构标准

```
src/main/java/com/wanli/
├── WanliBackendApplication.java     # 主启动类
├── config/                          # 配置类
│   ├── SecurityConfig.java          # 安全配置
│   ├── JpaConfig.java              # JPA配置
│   ├── CorsConfig.java             # CORS配置
│   └── OpenApiConfig.java          # API文档配置
├── controller/                      # 控制器层
│   ├── AuthController.java         # 认证控制器
│   ├── UserController.java         # 用户控制器
│   └── CourseController.java       # 课程控制器
├── service/                         # 服务层接口
│   ├── UserService.java            # 用户服务接口
│   └── impl/                       # 服务实现
│       └── UserServiceImpl.java    # 用户服务实现
├── repository/                      # 数据访问层
│   ├── UserRepository.java         # 用户仓库
│   └── CourseRepository.java       # 课程仓库
├── entity/                          # 实体类
│   ├── BaseEntity.java             # 基础实体
│   ├── User.java                   # 用户实体
│   └── Course.java                 # 课程实体
├── dto/                            # 数据传输对象
│   ├── request/                    # 请求DTO
│   │   ├── UserCreateRequest.java  # 用户创建请求
│   │   └── UserUpdateRequest.java  # 用户更新请求
│   └── response/                   # 响应DTO
│       ├── UserResponse.java       # 用户响应
│       └── ApiResponse.java        # 统一响应格式
├── enums/                          # 枚举类
│   ├── UserRole.java              # 用户角色枚举
│   └── EntityStatus.java          # 实体状态枚举
├── exception/                      # 异常处理
│   ├── GlobalExceptionHandler.java # 全局异常处理器
│   └── BusinessException.java     # 业务异常
├── security/                       # 安全相关
│   ├── JwtUtil.java               # JWT工具类
│   └── UserDetailsServiceImpl.java # 用户详情服务
└── util/                          # 工具类
    ├── UUIDConverter.java         # UUID转换工具
    └── DateTimeUtil.java          # 时间工具类
```

### 3.2 命名规范

#### 3.2.1 文件和目录命名

* **包名**: 全小写，使用点分隔 (`com.wanli.service`)
* **类名**: 驼峰命名，首字母大写 (`UserService`)
* **接口名**: 驼峰命名，首字母大写 (`UserRepository`)
* **枚举名**: 驼峰命名，首字母大写 (`UserRole`)

#### 3.2.2 方法和变量命名

* **方法名**: 驼峰命名，首字母小写 (`findUserById`)
* **变量名**: 驼峰命名，首字母小写 (`userId`)
* **常量名**: 全大写，下划线分隔 (`MAX_RETRY_COUNT`)
* **枚举值**: 全大写，下划线分隔 (`ROLE_PLATFORM_ADMIN`)

### 3.3 Repository方法命名规范

#### 3.3.1 JPA标准方法

```java
public interface UserRepository extends JpaRepository<User, UUID> {
    
    // 基础查询
    Optional<User> findById(UUID id);
    List<User> findAll();
    boolean existsById(UUID id);
    
    // 条件查询
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    
    // 软删除相关
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<User> findByIdAndNotDeleted(@Param("id") UUID id);
    
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAllNotDeleted();
    
    // 复合条件查询
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.deletedAt IS NULL")
    Optional<User> findByUsernameAndNotDeleted(@Param("username") String username);
    
    // 存在性检查
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :id AND u.deletedAt IS NULL")
    boolean existsByEmailAndIdNotAndNotDeleted(@Param("email") String email, @Param("id") UUID id);
}
```

***

## 4. API设计规范

### 4.1 RESTful API设计原则

#### 4.1.1 URL设计规范

```
# 资源集合
GET    /api/users          # 获取用户列表
POST   /api/users          # 创建用户

# 单个资源
GET    /api/users/{id}     # 获取指定用户
PUT    /api/users/{id}     # 更新指定用户
DELETE /api/users/{id}     # 删除指定用户

# 嵌套资源
GET    /api/courses/{courseId}/lessons    # 获取课程的课时列表
POST   /api/courses/{courseId}/lessons    # 在课程下创建课时
```

#### 4.1.2 HTTP状态码使用规范

* `200 OK` - 请求成功
* `201 Created` - 资源创建成功
* `204 No Content` - 请求成功但无返回内容
* `400 Bad Request` - 请求参数错误
* `401 Unauthorized` - 未认证
* `403 Forbidden` - 无权限
* `404 Not Found` - 资源不存在
* `409 Conflict` - 资源冲突
* `500 Internal Server Error` - 服务器内部错误

### 4.2 统一响应格式

#### 4.2.1 成功响应格式

```java
// 统一响应包装类
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    String timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "操作成功", data, OffsetDateTime.now().toString());
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, OffsetDateTime.now().toString());
    }
}

// 分页响应格式
public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {}
```

#### 4.2.2 错误响应格式

```java
// 业务错误响应
{
  "success": false,
  "message": "用户名已存在",
  "data": null,
  "timestamp": "2025-01-25T10:00:00.000+00:00"
}

// 验证错误响应
{
  "success": false,
  "message": "参数验证失败",
  "data": {
    "username": "用户名不能为空",
    "email": "邮箱格式不正确"
  },
  "timestamp": "2025-01-25T10:00:00.000+00:00"
}
```

### 4.3 分页和排序规范

#### 4.3.1 分页参数标准

```java
@GetMapping
public ResponseEntity<PageResponse<UserResponse>> getUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size,
    @RequestParam(defaultValue = "createdAt") String sort,
    @RequestParam(defaultValue = "desc") String direction
) {
    // 实现逻辑
}
```

#### 4.3.2 排序参数处理

```java
@Service
public class UserServiceImpl implements UserService {
    
    public PageResponse<UserResponse> getUsers(int page, int size, String sort, String direction) {
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) 
            ? Sort.Direction.ASC 
            : Sort.Direction.DESC;
            
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<User> userPage = userRepository.findAllNotDeleted(pageable);
        
        // 转换为响应DTO
        return convertToPageResponse(userPage);
    }
}
```

***

## 5. 数据验证规范

### 5.1 Bean Validation 3.x使用

#### 5.1.1 常用验证注解

```java
public record UserCreateRequest(
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50字符之间")
    String username,
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    String email,
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100字符之间")
    String password,
    
    @Valid
    UserProfileRequest profile
) {}
```

#### 5.1.2 自定义验证注解

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UUIDValidator.class)
public @interface ValidUUID {
    String message() default "UUID格式不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
public class UUIDValidator implements ConstraintValidator<ValidUUID, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 让@NotNull处理null值
        }
        
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
```

### 5.2 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
            false, "参数验证失败", errors, OffsetDateTime.now().toString()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        ApiResponse<Void> response = new ApiResponse<>(
            false, ex.getMessage(), null, OffsetDateTime.now().toString()
        );
        
        return ResponseEntity.badRequest().body(response);
    }
}
```

***

## 6. 测试规范

### 6.1 测试策略

#### 6.1.1 测试层次

1. **单元测试**: 测试单个方法或类的功能
2. **集成测试**: 测试多个组件的协作
3. **API测试**: 测试完整的HTTP请求响应
4. **端到端测试**: 测试完整的业务流程

#### 6.1.2 测试覆盖率要求

* **整体覆盖率**: > 80%
* **Service层覆盖率**: > 90%
* **Repository层覆盖率**: > 85%
* **Controller层覆盖率**: > 75%

### 6.2 Repository测试规范

```java
@DataJpaTest
@Import(TestDatabaseConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void findByUsernameAndNotDeleted_ShouldReturnUser_WhenUserExists() {
        // Given
        User user = createTestUser("testuser", "test@example.com");
        entityManager.persistAndFlush(user);
        
        // When
        Optional<User> result = userRepository.findByUsernameAndNotDeleted("testuser");
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }
    
    private User createTestUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("hashedPassword");
        user.setRole(UserRole.ROLE_STUDENT);
        return user;
    }
}
```

### 6.3 Service测试规范

```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    void createUser_ShouldReturnUserResponse_WhenValidRequest() {
        // Given
        UserCreateRequest request = new UserCreateRequest(
            "testuser", "test@example.com", "password123"
        );
        
        User savedUser = createTestUser();
        when(userRepository.existsByUsernameAndNotDeleted("testuser")).thenReturn(false);
        when(userRepository.existsByEmailAndNotDeleted("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        UserResponse result = userService.createUser(request);
        
        // Then
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.email()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
    }
}
```

### 6.4 API测试规范

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void createUser_ShouldReturn201_WhenValidRequest() {
        // Given
        UserCreateRequest request = new UserCreateRequest(
            "testuser", "test@example.com", "password123"
        );
        
        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
            "/api/users", request, ApiResponse.class
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().success()).isTrue();
        
        // Verify database
        Optional<User> savedUser = userRepository.findByUsernameAndNotDeleted("testuser");
        assertThat(savedUser).isPresent();
    }
}
```

***

## 7. 安全规范

### 7.1 Spring Security 6.x配置

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 7.2 JWT工具类规范

```java
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
        return createToken(claims, userDetails.getUsername());
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    // 其他JWT相关方法...
}
```

***

## 8. 性能优化规范

### 8.1 数据库优化

#### 8.1.1 索引设计原则

```sql
-- 单列索引
CREATE INDEX idx_users_username ON users(username) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_email ON users(email) WHERE deleted_at IS NULL;

-- 复合索引
CREATE INDEX idx_users_role_created_at ON users(role, created_at) WHERE deleted_at IS NULL;

-- 部分索引（软删除）
CREATE INDEX idx_users_active ON users(id) WHERE deleted_at IS NULL;
```

#### 8.1.2 查询优化

```java
// 使用@Query优化复杂查询
@Query(value = """
    SELECT u FROM User u 
    WHERE u.role = :role 
    AND u.deletedAt IS NULL 
    ORDER BY u.createdAt DESC
    """, 
    countQuery = "SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.deletedAt IS NULL")
Page<User> findByRoleAndNotDeleted(@Param("role") UserRole role, Pageable pageable);

// 使用@EntityGraph避免N+1问题
@EntityGraph(attributePaths = {"courses", "classes"})
@Query("SELECT u FROM User u WHERE u.id = :id AND u.deletedAt IS NULL")
Optional<User> findByIdWithCoursesAndClasses(@Param("id") UUID id);
```

### 8.2 缓存策略

```java
@Service
@CacheConfig(cacheNames = "users")
public class UserServiceImpl implements UserService {
    
    @Cacheable(key = "#id")
    public UserResponse getUserById(UUID id) {
        // 实现逻辑
    }
    
    @CacheEvict(key = "#id")
    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        // 实现逻辑
    }
    
    @CacheEvict(allEntries = true)
    public void clearUserCache() {
        // 清除所有用户缓存
    }
}
```

***

## 9. 部署和运维规范

### 9.1 多环境配置

#### 9.1.1 配置文件结构

```yaml
# application.yml (通用配置)
spring:
  application:
    name: wanli-backend
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
    
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
        
---
# application-dev.yml (开发环境)
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/wanli_backend_dev
    username: ${DB_USERNAME:dev_user}
    password: ${DB_PASSWORD:dev_password}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    
logging:
  level:
    com.wanli: DEBUG
    
---
# application-prod.yml (生产环境)
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    
logging:
  level:
    com.wanli: WARN
```

### 9.2 Docker配置

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/wanli-backend-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DB_HOST=postgres
      - DB_USERNAME=dev_user
      - DB_PASSWORD=dev_password
    depends_on:
      - postgres
      
  postgres:
    image: postgres:15
    environment:
      - POSTGRES_DB=wanli_backend_dev
      - POSTGRES_USER=dev_user
      - POSTGRES_PASSWORD=dev_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      
volumes:
  postgres_data:
```

***

## 10. 质量保证清单

### 10.1 开发前检查

* 设计文档已完成并审核通过
* 技术规范已明确
* 开发环境已配置
* 数据库结构已设计
* API接口已定义

### 10.2 开发过程检查

* 代码符合命名规范
* 使用统一的数据类型（UUID主键）
* 实现了适当的数据验证
* 添加了必要的单元测试
* 代码编译无错误无警告

### 10.3 提交前检查

* 所有测试用例通过
* 代码覆盖率达标
* API文档已更新
* 配置文件已检查
* 安全性检查通过

### 10.4 部署前检查

* 集成测试通过
* 性能测试达标
* 安全扫描通过
* 环境变量配置正确
* 数据库迁移脚本准备

***

## 11. 常见问题和解决方案

### 11.1 编译错误处理

**问题**: UUID与Long类型不匹配

```java
// 错误示例
Optional<User> findById(Long id);  // ❌

// 正确示例
Optional<User> findById(UUID id);  // ✅
```

**问题**: 时间类型不一致

```java
// 错误示例
private LocalDateTime createdAt;  // ❌

// 正确示例
private OffsetDateTime createdAt;  // ✅
```

### 11.2 性能问题处理

**问题**: N+1查询问题

```java
// 问题代码
List<User> users = userRepository.findAll();
for (User user : users) {
    user.getCourses().size(); // 触发N+1查询
}

// 解决方案
@EntityGraph(attributePaths = "courses")
List<User> findAllWithCourses();
```

### 11.3 安全问题处理

**问题**: JWT密钥安全

```yaml
# 错误示例
jwt:
  secret: "mySecret"  # ❌ 硬编码密钥

# 正确示例
jwt:
  secret: ${JWT_SECRET}  # ✅ 环境变量
```

***

## 12. 持续改进机制

### 12.1 代码审查流程

1. **提交前自检**: 开发者自行检查代码质量
2. **同行审查**: 至少一名同事审查代码
3. **自动化检查**: CI/CD流程自动检查
4. **集成测试**: 自动运行完整测试套件

### 12.2 质量监控

1. **代码覆盖率监控**: 定期检查测试覆盖率
2. **性能监控**: 监控关键接口响应时间
3. **错误监控**: 监控生产环境错误日志
4. **安全监控**: 定期进行安全扫描

### 12.3 知识分享

1. **技术分享会**: 定期分享最佳实践
2. **文档更新**: 及时更新开发规范
3. **经验总结**: 记录问题解决方案
4. **培训计划**: 新成员技术培训

***

## 结语

本指南基于万里书院项目的实际开发经验制定，旨在提供一套完整、实用的开发规范。所有开发团队成员都应严格遵守本指南，并在实践中不断完善和改进。

**记住核心原则**:

* 设计优先，质量第一
* 统一规范，避免混乱
* 增量开发，及时验证
* 持续改进，追求卓越

***

**文档维护**: 本指南应随项目发展持续更新\
**最后更新**: 2025年1月25日\
**下次评估**: 2025年4月25日
