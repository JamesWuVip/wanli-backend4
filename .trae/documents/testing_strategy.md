# 测试策略文档

## 1. 文档概述

### 1.1 文档目的

本文档定义了Wanli Academy项目的测试策略、测试方法、测试工具和质量保证流程，确保软件质量和系统稳定性。

### 1.2 适用范围

* 单元测试

* 集成测试

* API测试

* 性能测试

* 安全测试

### 1.3 版本信息

* 文档版本：v1.0.0

* Spring Boot版本：3.5.x

* JUnit版本：5.x

* 创建日期：2025-01-16

## 2. 测试策略概述

### 2.1 测试金字塔

```
        /\           UI Tests (E2E)
       /  \          - 少量，关键用户流程
      /____\         - 自动化程度：中等
     /      \        
    /        \       Integration Tests
   /          \      - 适量，API和服务集成
  /____________\     - 自动化程度：高
 /              \    
/________________\   Unit Tests
                     - 大量，业务逻辑覆盖
                     - 自动化程度：高
```

### 2.2 测试原则

* **快速反馈**: 测试应该快速执行并提供即时反馈

* **可靠性**: 测试结果应该稳定可重复

* **可维护性**: 测试代码应该易于理解和维护

* **全面覆盖**: 关键业务逻辑应该有充分的测试覆盖

* **自动化优先**: 优先实现自动化测试

### 2.3 质量目标

* 代码覆盖率：≥ 80%

* 单元测试通过率：100%

* 集成测试通过率：100%

* 关键API响应时间：< 200ms

* 系统可用性：≥ 99.9%

## 3. 单元测试策略

### 3.1 测试范围

* Service层业务逻辑

* Repository层数据访问

* 工具类和帮助方法

* 异常处理逻辑

* 数据验证逻辑

### 3.2 测试框架和工具

```xml
<!-- pom.xml 测试依赖 -->
<dependencies>
    <!-- Spring Boot Test Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Testcontainers for Database Testing -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>mysql</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito for Mocking -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- AssertJ for Fluent Assertions -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 3.3 Service层测试示例

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("用户注册 - 成功场景")
    void registerUser_Success() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");
        
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        UserResponse response = userService.registerUser(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    @DisplayName("用户注册 - 用户名已存在")
    void registerUser_UsernameExists() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.registerUser(request))
            .isInstanceOf(BusinessException.class)
            .hasMessage("用户名已存在");
        
        verify(userRepository).existsByUsername("existinguser");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    @DisplayName("用户登录 - 成功场景")
    void loginUser_Success() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encoded_password");
        user.setStatus(UserStatus.ACTIVE);
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        
        // When
        AuthResponse response = userService.loginUser(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull();
        assertThat(response.getRefreshToken()).isNotNull();
        
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encoded_password");
    }
}
```

### 3.4 Repository层测试示例

```java
@DataJpaTest
@Testcontainers
class UserRepositoryTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
    
    @Test
    @DisplayName("根据用户名查找用户")
    void findByUsername_Success() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encoded_password");
        user.setRole(UserRole.STUDENT);
        user.setStatus(UserStatus.ACTIVE);
        
        entityManager.persistAndFlush(user);
        
        // When
        Optional<User> found = userRepository.findByUsername("testuser");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }
    
    @Test
    @DisplayName("检查用户名是否存在")
    void existsByUsername_True() {
        // Given
        User user = new User();
        user.setUsername("existinguser");
        user.setEmail("existing@example.com");
        user.setPassword("encoded_password");
        user.setRole(UserRole.STUDENT);
        user.setStatus(UserStatus.ACTIVE);
        
        entityManager.persistAndFlush(user);
        
        // When
        boolean exists = userRepository.existsByUsername("existinguser");
        
        // Then
        assertThat(exists).isTrue();
    }
    
    @Test
    @DisplayName("根据角色查找活跃用户")
    void findByRoleAndStatus_Success() {
        // Given
        User student1 = createUser("student1", "student1@example.com", UserRole.STUDENT, UserStatus.ACTIVE);
        User student2 = createUser("student2", "student2@example.com", UserRole.STUDENT, UserStatus.INACTIVE);
        User teacher = createUser("teacher", "teacher@example.com", UserRole.TEACHER, UserStatus.ACTIVE);
        
        entityManager.persistAndFlush(student1);
        entityManager.persistAndFlush(student2);
        entityManager.persistAndFlush(teacher);
        
        // When
        List<User> activeStudents = userRepository.findByRoleAndStatus(UserRole.STUDENT, UserStatus.ACTIVE);
        
        // Then
        assertThat(activeStudents).hasSize(1);
        assertThat(activeStudents.get(0).getUsername()).isEqualTo("student1");
    }
    
    private User createUser(String username, String email, UserRole role, UserStatus status) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("encoded_password");
        user.setRole(role);
        user.setStatus(status);
        return user;
    }
}
```

## 4. 集成测试策略

### 4.1 测试范围

* API端点集成测试

* 数据库集成测试

* 外部服务集成测试

* 安全认证集成测试

### 4.2 API集成测试示例

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
class UserControllerIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
    
    @Test
    @Order(1)
    @DisplayName("用户注册 - 集成测试")
    void registerUser_IntegrationTest() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("integrationuser");
        request.setEmail("integration@example.com");
        request.setPassword("password123");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);
        
        // When
        ResponseEntity<ApiResponse<UserResponse>> response = restTemplate.exchange(
            "/api/auth/register",
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<ApiResponse<UserResponse>>() {}
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData().getUsername()).isEqualTo("integrationuser");
        
        // 验证数据库中的数据
        Optional<User> savedUser = userRepository.findByUsername("integrationuser");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getEmail()).isEqualTo("integration@example.com");
    }
    
    @Test
    @Order(2)
    @DisplayName("用户登录 - 集成测试")
    void loginUser_IntegrationTest() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("integrationuser");
        request.setPassword("password123");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);
        
        // When
        ResponseEntity<ApiResponse<AuthResponse>> response = restTemplate.exchange(
            "/api/auth/login",
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<ApiResponse<AuthResponse>>() {}
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData().getAccessToken()).isNotNull();
        assertThat(response.getBody().getData().getRefreshToken()).isNotNull();
    }
    
    @Test
    @Order(3)
    @DisplayName("获取用户信息 - 需要认证")
    void getUserInfo_WithAuthentication() {
        // Given - 先登录获取Token
        String accessToken = loginAndGetToken("integrationuser", "password123");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        // When
        ResponseEntity<ApiResponse<UserResponse>> response = restTemplate.exchange(
            "/api/users/me",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<ApiResponse<UserResponse>>() {}
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getData().getUsername()).isEqualTo("integrationuser");
    }
    
    @Test
    @DisplayName("获取用户信息 - 无认证应返回401")
    void getUserInfo_WithoutAuthentication() {
        // When
        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
            "/api/users/me",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<ApiResponse<Object>>() {}
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    
    private String loginAndGetToken(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<ApiResponse<AuthResponse>> response = restTemplate.exchange(
            "/api/auth/login",
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<ApiResponse<AuthResponse>>() {}
        );
        
        return response.getBody().getData().getAccessToken();
    }
}
```

## 5. 性能测试策略

### 5.1 性能测试工具

* **JMeter**: API性能测试

* **Spring Boot Actuator**: 应用监控

* **Micrometer**: 指标收集

### 5.2 性能测试场景

```java
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class PerformanceTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    @DisplayName("用户注册性能测试")
    void registerUser_PerformanceTest() {
        // Given
        int numberOfUsers = 100;
        List<RegisterRequest> requests = new ArrayList<>();
        
        for (int i = 0; i < numberOfUsers; i++) {
            RegisterRequest request = new RegisterRequest();
            request.setUsername("perfuser" + i);
            request.setEmail("perfuser" + i + "@example.com");
            request.setPassword("password123");
            requests.add(request);
        }
        
        // When
        long startTime = System.currentTimeMillis();
        
        requests.parallelStream().forEach(request -> {
            try {
                userService.registerUser(request);
            } catch (Exception e) {
                // 记录异常但不中断测试
                System.err.println("Registration failed for: " + request.getUsername());
            }
        });
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Then
        System.out.println("Registered " + numberOfUsers + " users in " + duration + "ms");
        System.out.println("Average time per registration: " + (duration / numberOfUsers) + "ms");
        
        // 断言平均响应时间小于100ms
        assertThat(duration / numberOfUsers).isLessThan(100);
    }
}
```

### 5.3 JMeter测试计划

```xml
<!-- user-registration-test.jmx 示例配置 -->
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2">
  <hashTree>
    <TestPlan guiclass="TestPlanGui" testclass="TestPlan" testname="User Registration Performance Test">
      <elementProp name="TestPlan.arguments" elementType="Arguments" guiclass="ArgumentsPanel">
        <collectionProp name="Arguments.arguments"/>
      </elementProp>
      <stringProp name="TestPlan.user_define_classpath"></stringProp>
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
    </TestPlan>
    <hashTree>
      <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="User Registration">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">10</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">50</stringProp>
        <stringProp name="ThreadGroup.ramp_time">30</stringProp>
      </ThreadGroup>
    </hashTree>
  </hashTree>
</jmeterTestPlan>
```

## 6. 安全测试策略

### 6.1 安全测试范围

* JWT Token安全性

* API权限控制

* 输入验证

* SQL注入防护

* XSS防护

### 6.2 安全测试示例

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("SQL注入防护测试")
    void sqlInjectionProtection() {
        // Given
        LoginRequest maliciousRequest = new LoginRequest();
        maliciousRequest.setUsername("admin'; DROP TABLE users; --");
        maliciousRequest.setPassword("password");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> entity = new HttpEntity<>(maliciousRequest, headers);
        
        // When
        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
            "/api/auth/login",
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<ApiResponse<Object>>() {}
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        // 验证数据库表仍然存在
        // 这里可以添加数据库连接测试
    }
    
    @Test
    @DisplayName("XSS防护测试")
    void xssProtection() {
        // Given
        RegisterRequest xssRequest = new RegisterRequest();
        xssRequest.setUsername("<script>alert('xss')</script>");
        xssRequest.setEmail("test@example.com");
        xssRequest.setPassword("password123");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(xssRequest, headers);
        
        // When
        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
            "/api/auth/register",
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<ApiResponse<Object>>() {}
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    @Test
    @DisplayName("JWT Token篡改测试")
    void jwtTokenTampering() {
        // Given
        String tamperedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiJ9.invalid_signature";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tamperedToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        // When
        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
            "/api/users/me",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<ApiResponse<Object>>() {}
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
```

## 7. 测试数据管理

### 7.1 测试数据策略

* **隔离性**: 每个测试使用独立的数据

* **可重复性**: 测试数据应该可重复生成

* **清理性**: 测试后自动清理数据

### 7.2 测试数据工厂

```java
@Component
public class TestDataFactory {
    
    public static User createTestUser(String username, String email, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("$2a$10$encoded_password");
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
    
    public static Course createTestCourse(String title, String description, User teacher) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setTeacher(teacher);
        course.setGradeLevel(GradeLevel.GRADE_1);
        course.setSubject(Subject.MATH);
        course.setStatus(CourseStatus.PUBLISHED);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        return course;
    }
    
    public static Lesson createTestLesson(String title, String content, Course course) {
        Lesson lesson = new Lesson();
        lesson.setTitle(title);
        lesson.setContent(content);
        lesson.setCourse(course);
        lesson.setOrderIndex(1);
        lesson.setStatus(LessonStatus.PUBLISHED);
        lesson.setCreatedAt(LocalDateTime.now());
        lesson.setUpdatedAt(LocalDateTime.now());
        return lesson;
    }
}
```

### 7.3 数据库测试配置

```java
@TestConfiguration
public class TestDatabaseConfig {
    
    @Bean
    @Primary
    @Profile("test")
    public DataSource testDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:schema-test.sql")
            .addScript("classpath:data-test.sql")
            .build();
    }
    
    @EventListener
    public void handleTestExecutionStarted(TestExecutionStartedEvent event) {
        // 测试开始前的数据准备
    }
    
    @EventListener
    public void handleTestExecutionFinished(TestExecutionFinishedEvent event) {
        // 测试结束后的数据清理
    }
}
```

## 8. 持续集成测试

### 8.1 GitHub Actions配置

```yaml
# .github/workflows/test.yml
name: Test Suite

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: root
          MYSQL_DATABASE: wanli_test
        ports:
          - 3306:3306
        options: --health-cmd="mysqladmin ping" --health-interval=10s --health-timeout=5s --health-retries=3
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2
    
    - name: Run tests
      run: mvn clean test
      env:
        SPRING_PROFILES_ACTIVE: test
        SPRING_DATASOURCE_URL: jdbc:mysql://localhost:3306/wanli_test
        SPRING_DATASOURCE_USERNAME: root
        SPRING_DATASOURCE_PASSWORD: root
    
    - name: Generate test report
      run: mvn jacoco:report
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: ./target/site/jacoco/jacoco.xml
        flags: unittests
        name: codecov-umbrella
```

### 8.2 Maven测试配置

```xml
<!-- pom.xml 测试插件配置 -->
<build>
    <plugins>
        <!-- Surefire Plugin for Unit Tests -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0-M9</version>
            <configuration>
                <includes>
                    <include>**/*Test.java</include>
                    <include>**/*Tests.java</include>
                </includes>
                <excludes>
                    <exclude>**/*IntegrationTest.java</exclude>
                </excludes>
            </configuration>
        </plugin>
        
        <!-- Failsafe Plugin for Integration Tests -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-failsafe-plugin</artifactId>
            <version>3.0.0-M9</version>
            <configuration>
                <includes>
                    <include>**/*IntegrationTest.java</include>
                    <include>**/*IT.java</include>
                </includes>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>integration-test</goal>
                        <goal>verify</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        
        <!-- JaCoCo Plugin for Code Coverage -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.8</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                <execution>
                    <id>check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>BUNDLE</element>
                                <limits>
                                    <limit>
                                        <counter>INSTRUCTION</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.80</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## 9. 测试报告和指标

### 9.1 测试报告模板

```markdown
# 测试执行报告

## 测试概览
- 测试执行日期: [日期]
- 测试环境: [环境]
- 测试版本: [版本号]
- 执行人员: [姓名]

## 测试结果统计
| 测试类型 | 总数 | 通过 | 失败 | 跳过 | 通过率 |
|---------|------|------|------|------|--------|
| 单元测试 | 150  | 148  | 2    | 0    | 98.7%  |
| 集成测试 | 45   | 43   | 2    | 0    | 95.6%  |
| 性能测试 | 10   | 9    | 1    | 0    | 90.0%  |

## 代码覆盖率
- 总体覆盖率: 85.2%
- 行覆盖率: 87.1%
- 分支覆盖率: 82.3%
- 方法覆盖率: 89.5%

## 失败测试分析
### 单元测试失败
1. UserServiceTest.testPasswordValidation - 密码验证逻辑错误
2. CourseRepositoryTest.testFindByStatus - 数据库查询异常

### 集成测试失败
1. AuthControllerIntegrationTest.testTokenRefresh - Token刷新机制问题
2. UserControllerIntegrationTest.testUserUpdate - 权限验证失败

## 性能测试结果
- 用户注册平均响应时间: 95ms (目标: <100ms) ✅
- 用户登录平均响应时间: 120ms (目标: <200ms) ✅
- 课程查询平均响应时间: 250ms (目标: <200ms) ❌

## 改进建议
1. 优化课程查询SQL，添加适当索引
2. 修复Token刷新机制的并发问题
3. 完善密码验证的边界条件测试
```

### 9.2 自动化报告生成

```java
@Component
public class TestReportGenerator {
    
    public void generateTestReport(TestResults results) {
        StringBuilder report = new StringBuilder();
        
        report.append("# 自动化测试报告\n\n");
        report.append("## 执行时间: ").append(LocalDateTime.now()).append("\n\n");
        
        // 测试统计
        report.append("## 测试统计\n");
        report.append("- 总测试数: ").append(results.getTotalTests()).append("\n");
        report.append("- 通过数: ").append(results.getPassedTests()).append("\n");
        report.append("- 失败数: ").append(results.getFailedTests()).append("\n");
        report.append("- 通过率: ").append(results.getPassRate()).append("%\n\n");
        
        // 覆盖率信息
        report.append("## 代码覆盖率\n");
        report.append("- 行覆盖率: ").append(results.getLineCoverage()).append("%\n");
        report.append("- 分支覆盖率: ").append(results.getBranchCoverage()).append("%\n\n");
        
        // 失败测试详情
        if (!results.getFailedTests().isEmpty()) {
            report.append("## 失败测试\n");
            results.getFailedTestDetails().forEach(failure -> {
                report.append("- ").append(failure.getTestName())
                      .append(": ").append(failure.getErrorMessage()).append("\n");
            });
        }
        
        // 保存报告
        saveReportToFile(report.toString());
        
        // 发送通知（如果配置了）
        if (shouldSendNotification(results)) {
            sendNotification(report.toString());
        }
    }
    
    private void saveReportToFile(String report) {
        try {
            Path reportPath = Paths.get("target/test-reports/test-report-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".md");
            Files.createDirectories(reportPath.getParent());
            Files.write(reportPath, report.getBytes());
        } catch (IOException e) {
            log.error("Failed to save test report", e);
        }
    }
}
```

## 10. 最佳实践和规范

### 10.1 测试命名规范

* 测试类命名: `{被测试类名}Test`

* 测试方法命名: `{方法名}_{场景}_{期望结果}`

* 显示名称: 使用`@DisplayName`提供中文描述

### 10.2 测试结构规范

```java
@Test
@DisplayName("用户注册 - 成功场景")
void registerUser_ValidInput_Success() {
    // Given (准备测试数据)
    RegisterRequest request = new RegisterRequest();
    // ... 设置测试数据
    
    // When (执行被测试方法)
    UserResponse response = userService.registerUser(request);
    
    // Then (验证结果)
    assertThat(response).isNotNull();
    assertThat(response.getUsername()).isEqualTo("testuser");
    // ... 其他断言
}
```

### 10.3 测试维护规范

* 定期审查和更新测试用例

* 删除过时和重复的测试

* 保持测试代码的简洁和可读性

* 及时修复失败的测试

* 监控测试执行时间，优化慢测试

***

**文档维护**

* 负责人：QA团队

* 更新频率：随测试策略变更更新

* 审核周期：每月审核一次

