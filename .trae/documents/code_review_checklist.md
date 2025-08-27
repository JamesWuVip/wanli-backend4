# 代码审查检查清单

## 1. 文档概述

### 1.1 文档目的

本文档提供了Wanli Academy项目代码审查的标准检查清单，确保代码质量、安全性、性能和可维护性符合项目标准。

### 1.2 适用范围

* 所有后端Java代码

* API接口实现

* 数据库操作代码

* 配置文件

* 测试代码

### 1.3 版本信息

* 文档版本：v1.0.0

* Spring Boot版本：3.5.x

* 创建日期：2025-01-16

## 2. 代码审查流程

### 2.1 审查时机

* [ ] 功能开发完成后

* [ ] 提交Pull Request前

* [ ] 合并到主分支前

* [ ] 重要功能上线前

### 2.2 审查角色

* **代码作者**: 自我审查，确保基本质量

* **同级开发者**: 交叉审查，发现潜在问题

* **技术负责人**: 架构和设计审查

* **QA工程师**: 测试覆盖度审查

### 2.3 审查工具

* GitHub Pull Request

* SonarQube代码质量检查

* IDE静态分析工具

* 单元测试覆盖率报告

## 3. 代码质量检查清单

### 3.1 代码结构和组织

#### 包结构

* [ ] 包名遵循项目命名规范 (`com.wanli.academy.*`)

* [ ] 类按功能模块正确分包

* [ ] 避免循环依赖

* [ ] 包结构清晰，职责明确

#### 类设计

* [ ] 类名使用驼峰命名法，语义清晰

* [ ] 单一职责原则，类功能单一明确

* [ ] 类大小合理（建议不超过500行）

* [ ] 正确使用访问修饰符

* [ ] 避免God Class（上帝类）

#### 方法设计

* [ ] 方法名使用驼峰命名法，语义清晰

* [ ] 方法长度合理（建议不超过200行）

* [ ] 参数数量合理（建议不超过5个）

* [ ] 方法职责单一

* [ ] 避免深层嵌套（建议不超过3层）

### 3.2 编码规范

#### 命名规范

* [ ] 变量名使用驼峰命名法

* [ ] 常量名使用大写字母和下划线

* [ ] 布尔变量使用is/has/can等前缀

* [ ] 集合变量使用复数形式

* [ ] 避免使用缩写和拼音

```java
// ✅ 好的命名
private static final int MAX_RETRY_COUNT = 3;
private boolean isUserActive;
private List<User> activeUsers;
private String userName;

// ❌ 不好的命名
private static final int MAX_CNT = 3;
private boolean flag;
private List<User> user;
private String usrNm;
```

#### 代码格式

* [ ] 使用统一的代码格式化规则

* [ ] 正确使用空行分隔逻辑块

* [ ] 合理的缩进和对齐

* [ ] 行长度不超过120字符

* [ ] 删除多余的空行和空格

#### 注释规范

* [ ] 类和接口有完整的JavaDoc注释

* [ ] 公共方法有JavaDoc注释

* [ ] 复杂逻辑有行内注释说明

* [ ] 注释内容准确，与代码同步更新

* [ ] 避免无意义的注释

```java
/**
 * 用户服务类
 * 提供用户注册、登录、信息管理等功能
 * 
 * @author 开发者姓名
 * @since 1.0.0
 */
@Service
public class UserService {
    
    /**
     * 用户注册
     * 
     * @param request 注册请求参数
     * @return 注册成功的用户信息
     * @throws DuplicateUsernameException 用户名已存在
     * @throws DuplicateEmailException 邮箱已被注册
     */
    public UserResponse registerUser(RegisterRequest request) {
        // 实现逻辑
    }
}
```

### 3.3 Spring Boot最佳实践

#### 注解使用

* [ ] 正确使用Spring注解（@Service, @Repository, @Controller等）

* [ ] 避免过度使用@Autowired，优先使用构造器注入

* [ ] 正确使用@Transactional注解

* [ ] 合理使用@Valid和@Validated进行参数验证

* [ ] 正确使用@PreAuthorize进行权限控制

```java
// ✅ 推荐的依赖注入方式
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
}

// ❌ 不推荐的方式
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
}
```

#### 配置管理

* [ ] 配置项使用@ConfigurationProperties

* [ ] 敏感配置使用环境变量

* [ ] 配置文件按环境正确分离

* [ ] 避免硬编码配置值

#### 异常处理

* [ ] 使用项目定义的异常类

* [ ] 正确的异常传播和转换

* [ ] 避免捕获Exception等通用异常

* [ ] 异常信息对用户友好

## 4. 数据库操作检查

### 4.1 Repository层

* [ ] 继承正确的Repository接口

* [ ] 查询方法命名符合Spring Data JPA规范

* [ ] 复杂查询使用@Query注解

* [ ] 避免N+1查询问题

* [ ] 正确使用分页和排序

```java
// ✅ 好的Repository设计
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.status = :status AND u.createdAt >= :startDate")
    Page<User> findActiveUsersCreatedAfter(@Param("status") UserStatus status, 
                                          @Param("startDate") LocalDateTime startDate, 
                                          Pageable pageable);
}
```

### 4.2 实体类设计

* [ ] 正确使用JPA注解

* [ ] 实体关系映射正确

* [ ] 使用BaseEntity统一公共字段

* [ ] 正确实现equals和hashCode

* [ ] 避免双向关联的无限递归

### 4.3 事务管理

* [ ] 服务层方法正确使用@Transactional

* [ ] 事务边界合理，避免过大事务

* [ ] 只读操作使用readOnly=true

* [ ] 正确处理事务回滚条件

## 5. API设计检查

### 5.1 RESTful设计

* [ ] URL设计符合RESTful规范

* [ ] HTTP方法使用正确（GET/POST/PUT/DELETE）

* [ ] 状态码返回正确

* [ ] 资源命名使用名词复数形式

```java
// ✅ 好的API设计
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(Pageable pageable) {
        // 获取用户列表
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        // 获取单个用户
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        // 创建用户
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id, 
                                                              @Valid @RequestBody UpdateUserRequest request) {
        // 更新用户
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        // 删除用户
    }
}
```

### 5.2 请求响应设计

* [ ] 使用统一的响应格式（ApiResponse）

* [ ] 请求参数使用DTO对象

* [ ] 响应数据使用DTO对象

* [ ] 正确使用HTTP状态码

* [ ] 分页参数使用Pageable

### 5.3 参数验证

* [ ] 使用Bean Validation注解

* [ ] 自定义验证器实现复杂验证

* [ ] 验证错误信息友好

* [ ] 避免在Controller中进行业务验证

```java
// ✅ 好的参数验证
public class RegisterRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在8-20字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$", 
             message = "密码必须包含大小写字母和数字")
    private String password;
}
```

## 6. 安全性检查

### 6.1 认证授权

* [ ] 敏感接口正确使用认证

* [ ] 权限控制粒度合适

* [ ] JWT Token正确验证

* [ ] 避免权限绕过漏洞

### 6.2 输入验证

* [ ] 所有用户输入都进行验证

* [ ] 防止SQL注入

* [ ] 防止XSS攻击

* [ ] 防止CSRF攻击

* [ ] 文件上传安全检查

### 6.3 敏感信息保护

* [ ] 密码正确加密存储

* [ ] 敏感信息不记录到日志

* [ ] API响应不包含敏感信息

* [ ] 错误信息不泄露系统信息

```java
// ✅ 安全的密码处理
@Service
public class AuthService {
    
    private final PasswordEncoder passwordEncoder;
    
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException();
        }
        
        // 加密新密码
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        
        userRepository.save(user);
        
        // 不记录敏感信息到日志
        log.info("Password changed successfully for user: {}", username);
    }
}
```

## 7. 性能检查

### 7.1 数据库性能

* [ ] 避免N+1查询问题

* [ ] 合理使用索引

* [ ] 大数据量查询使用分页

* [ ] 避免全表扫描

* [ ] 批量操作使用批处理

### 7.2 缓存使用

* [ ] 合理使用缓存注解

* [ ] 缓存键设计合理

* [ ] 缓存过期时间设置合适

* [ ] 避免缓存穿透和雪崩

### 7.3 资源管理

* [ ] 正确关闭资源（文件、连接等）

* [ ] 避免内存泄漏

* [ ] 合理使用线程池

* [ ] 避免阻塞操作

## 8. 测试覆盖检查

### 8.1 单元测试

* [ ] 核心业务逻辑有单元测试

* [ ] 测试覆盖率达到80%以上

* [ ] 测试用例包含正常和异常场景

* [ ] 测试方法命名清晰

* [ ] 使用合适的断言

### 8.2 集成测试

* [ ] API接口有集成测试

* [ ] 数据库操作有测试

* [ ] 测试数据隔离

* [ ] 测试环境配置正确

### 8.3 测试质量

* [ ] 测试用例独立，不相互依赖

* [ ] 测试数据准备充分

* [ ] 异常场景测试完整

* [ ] 边界条件测试覆盖

```java
// ✅ 好的测试用例
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("用户注册成功")
    void registerUser_ValidRequest_Success() {
        // Given
        RegisterRequest request = createValidRegisterRequest();
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(createUser());
        
        // When
        UserResponse response = userService.registerUser(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo(request.getUsername());
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @DisplayName("用户注册失败 - 用户名已存在")
    void registerUser_DuplicateUsername_ThrowsException() {
        // Given
        RegisterRequest request = createValidRegisterRequest();
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.registerUser(request))
            .isInstanceOf(DuplicateUsernameException.class)
            .hasMessageContaining(request.getUsername());
        
        verify(userRepository, never()).save(any(User.class));
    }
}
```

## 9. 配置和部署检查

### 9.1 配置文件

* [ ] 配置文件按环境正确分离

* [ ] 敏感配置使用环境变量

* [ ] 配置项有合理的默认值

* [ ] 配置文件格式正确

### 9.2 日志配置

* [ ] 日志级别配置合理

* [ ] 日志格式统一

* [ ] 敏感信息不记录到日志

* [ ] 日志文件轮转配置

### 9.3 监控配置

* [ ] 健康检查端点配置

* [ ] 指标收集配置

* [ ] 错误监控配置

* [ ] 性能监控配置

## 10. 代码审查检查表

### 10.1 功能性检查

* [ ] 功能实现符合需求

* [ ] 边界条件处理正确

* [ ] 错误处理完整

* [ ] 业务逻辑正确

* [ ] 数据一致性保证

### 10.2 非功能性检查

* [ ] 性能满足要求

* [ ] 安全性符合标准

* [ ] 可维护性良好

* [ ] 可扩展性考虑

* [ ] 可测试性良好

### 10.3 代码质量检查

* [ ] 代码结构清晰

* [ ] 命名规范统一

* [ ] 注释完整准确

* [ ] 无重复代码

* [ ] 无死代码

### 10.4 最终检查

* [ ] 所有测试通过

* [ ] 代码覆盖率达标

* [ ] 静态分析无严重问题

* [ ] 文档更新完整

* [ ] 变更记录清晰

## 11. 审查结果处理

### 11.1 问题分类

* **严重问题**: 功能错误、安全漏洞、性能问题

* **一般问题**: 代码规范、设计问题、可维护性

* **建议改进**: 代码优化、最佳实践建议

### 11.2 处理流程

1. **发现问题**: 在PR中标记问题位置和描述
2. **问题讨论**: 开发者和审查者讨论解决方案
3. **修复问题**: 开发者修复问题并更新代码
4. **重新审查**: 审查者确认问题已解决
5. **合并代码**: 所有问题解决后合并到主分支

### 11.3 审查记录

```markdown
## 代码审查记录

**审查时间**: 2025-01-16
**审查者**: 张三
**代码作者**: 李四
**PR链接**: #123

### 发现问题
1. **严重**: UserService.registerUser方法缺少事务注解
2. **一般**: 变量命名不规范，建议使用驼峰命名
3. **建议**: 可以使用Optional优化空值处理

### 修复状态
- [x] 问题1已修复
- [x] 问题2已修复
- [x] 问题3已优化

### 审查结论
✅ 通过审查，可以合并
```

## 12. 工具和自动化

### 12.1 静态分析工具

* **SonarQube**: 代码质量和安全性分析

* **SpotBugs**: Bug检测

* **PMD**: 代码规范检查

* **Checkstyle**: 代码风格检查

### 12.2 IDE插件

* **SonarLint**: 实时代码质量检查

* **CheckStyle-IDEA**: 代码风格检查

* **SpotBugs**: Bug检测

### 12.3 CI/CD集成

```yaml
# GitHub Actions示例
name: Code Review Checks

on:
  pull_request:
    branches: [ main, develop ]

jobs:
  code-quality:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Run tests
        run: ./mvnw test
      
      - name: Generate test report
        uses: dorny/test-reporter@v1
        if: success() || failure()
        with:
          name: Maven Tests
          path: target/surefire-reports/*.xml
          reporter: java-junit
      
      - name: Run SonarQube analysis
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: ./mvnw sonar:sonar
      
      - name: Check code coverage
        run: |
          coverage=$(./mvnw jacoco:report | grep -o 'Total.*[0-9]\+%' | grep -o '[0-9]\+%' | head -1 | grep -o '[0-9]\+')
          if [ $coverage -lt 80 ]; then
            echo "Code coverage is below 80%: $coverage%"
            exit 1
          fi
```

***

**文档维护**

* 负责人：技术负责人

* 更新频率：随开发规范变更更新

* 审核周期：每季度审核一次

* 培训计划：新成员入职培训必读

