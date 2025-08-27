# Spring Boot 项目工程实践指南

## 1. 概述

本文档基于实际项目开发过程中遇到的问题和解决方案，总结了Spring Boot项目开发的最佳实践，旨在提高代码质量、减少重复工作，并为团队提供统一的开发标准。

## 2. 核心经验教训

### 2.1 测试框架标准化

**原则：坚持使用Spring Boot官方测试框架**

- **必须使用**：`spring-boot-starter-test`、`spring-security-test`
- **禁止使用**：第三方测试框架（如Testcontainers等）
- **理由**：减少依赖复杂性，保持与Spring Boot生态的一致性

**配置要点：**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 2.2 数据库配置原则

**原则：生产环境数据库一致性**

- **禁止使用**：H2内存数据库（包括测试环境）
- **必须使用**：PostgreSQL（开发、测试、生产环境保持一致）
- **理由**：避免不同数据库间的兼容性问题

**测试配置示例：**
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/wanli_backend_test
    username: postgres
    password: password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

**测试类配置：**
```java
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    // 测试代码
}
```

### 2.3 实体状态管理

**核心问题：EntityExistsException 和 ObjectOptimisticLockingFailure**

**解决方案：**

1. **使用Repository而非EntityManager**
   ```java
   // 错误做法
   entityManager.persistAndFlush(user);
   
   // 正确做法
   userRepository.save(user);
   ```

2. **确保实体为Transient状态**
   ```java
   // 每个测试方法创建新实体
   @Test
   void testMethod() {
       User user = TestDataFactory.createDefaultUser(); // 不设置ID
       User savedUser = userRepository.save(user);
       // 测试逻辑
   }
   ```

3. **正确处理实体生命周期**
   ```java
   @Entity
   public class User {
       @PrePersist
       protected void onCreate() {
           this.createdAt = LocalDateTime.now();
       }
       
       // 构造函数中不要调用onCreate()
       public User() {
           // 让JPA自动调用@PrePersist
       }
   }
   ```

### 2.4 错误分析方法论

**系统化错误分析流程：**

1. **错误分类**：按异常类型分组
2. **根因分析**：定位到具体代码行
3. **单点实验**：先解决单个问题
4. **批量修复**：使用自动化脚本处理同类问题
5. **验证测试**：确保修复有效性

## 3. 开发指导原则

### 3.1 依赖管理

**Maven依赖原则：**
- 最小化外部依赖
- 优先使用Spring Boot Starter
- 避免版本冲突
- 定期清理无用依赖

**推荐依赖结构：**
```xml
<!-- 核心依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- 数据库 -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- 测试 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 3.2 测试类配置标准

**Repository测试：**
```java
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
public class RepositoryTest {
    // 测试代码
}
```

**Service测试：**
```java
@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;
    
    // 测试代码
}
```

**Controller测试：**
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Rollback
public class ControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    // 测试代码
}
```

### 3.3 实体设计规范

**实体类设计要点：**

1. **ID生成策略**
   ```java
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   ```

2. **时间戳管理**
   ```java
   @CreationTimestamp
   @Column(name = "created_at", nullable = false, updatable = false)
   private LocalDateTime createdAt;
   
   @UpdateTimestamp
   @Column(name = "updated_at")
   private LocalDateTime updatedAt;
   ```

3. **乐观锁控制**
   ```java
   @Version
   private Long version;
   ```

4. **Builder模式**
   ```java
   @Entity
   @Builder
   @NoArgsConstructor
   @AllArgsConstructor
   public class User {
       // 字段定义
   }
   ```

## 4. 质量保证措施

### 4.1 测试覆盖率要求

- **单元测试覆盖率**：≥ 80%
- **集成测试覆盖率**：≥ 70%
- **关键业务逻辑**：100%覆盖

### 4.2 代码审查检查点

**必检项目：**
- [ ] 是否使用了H2数据库
- [ ] 是否添加了不必要的第三方测试框架
- [ ] 实体状态管理是否正确
- [ ] 测试方法是否有数据隔离
- [ ] 异常处理是否完整

### 4.3 持续集成配置

**Maven配置：**
```xml
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
    </executions>
</plugin>
```

## 5. 未来开发建议

### 5.1 技术栈演进

- **保持Spring Boot版本更新**：定期升级到LTS版本
- **数据库优化**：考虑连接池配置优化
- **缓存策略**：引入Redis缓存
- **监控体系**：集成Micrometer和Prometheus

### 5.2 开发流程优化

1. **代码提交前检查**
   - 运行完整测试套件
   - 检查代码覆盖率
   - 执行静态代码分析

2. **分支管理策略**
   - 功能分支开发
   - Code Review必须通过
   - 自动化测试通过后合并

3. **文档维护**
   - API文档自动生成
   - 架构决策记录(ADR)
   - 定期更新技术文档

### 5.3 性能优化方向

- **数据库查询优化**：使用JPA Criteria API
- **缓存策略**：合理使用@Cacheable注解
- **异步处理**：使用@Async处理耗时操作
- **分页查询**：大数据量查询必须分页

## 6. 常见问题解决方案

### 6.1 EntityExistsException

**问题**：detached entity passed to persist

**解决方案**：
```java
// 错误
entityManager.persistAndFlush(existingEntity);

// 正确
Repository.save(newEntity);
```

### 6.2 ObjectOptimisticLockingFailure

**问题**：版本控制冲突

**解决方案**：
```java
// 确保实体版本正确
User user = userRepository.findById(id).orElseThrow();
user.setName("new name");
userRepository.save(user); // 自动处理版本更新
```

### 6.3 测试数据隔离问题

**解决方案**：
```java
@Test
@Transactional
@Rollback
void testMethod() {
    // 每个测试方法自动回滚
}
```

## 7. 总结

本工程实践指南基于实际项目经验总结，重点强调了测试框架标准化、数据库配置一致性、实体状态管理等关键问题。遵循这些实践可以显著减少开发过程中的问题，提高代码质量和开发效率。

团队成员应定期回顾和更新这些实践，确保与技术发展保持同步。