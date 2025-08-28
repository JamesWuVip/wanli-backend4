# Wanli Backend 内部部署配置完整指南

> ⚠️ **机密文档** - 本文档包含敏感配置信息，仅供内部使用，严禁外泄

## 项目概述

- **项目名称**: wanli-backend
- **Railway项目**: beneficial-beauty
- **Git仓库**: JamesWuVip/wanli-backend
- **部署平台**: Railway
- **数据库**: PostgreSQL
- **框架**: Spring Boot 3.5.0
- **Java版本**: 17

## 环境架构

### 分支与环境对应关系
- **dev分支** → 本地开发环境 (本地MySQL)
- **staging分支** → 测试环境 (Railway + PostgreSQL)
- **main分支** → 生产环境 (Railway + PostgreSQL)

### GitFlow规范
1. 开发在dev分支进行
2. 测试部署到staging分支
3. 生产环境部署到main分支
4. **禁止跳过staging直接合并到main**

---

## Staging环境完整配置

### 基本信息
- **环境名称**: staging
- **服务名称**: wanli-backend-staging
- **部署域名**: https://wanli-backend-staging-staging.up.railway.app
- **配置文件**: application-staging.yml
- **Profile**: staging

### Railway环境变量配置（完整版）
```bash
# 数据库配置
SPRING_DATASOURCE_URL=jdbc:postgresql://viaduct.proxy.rlwy.net:12345/railway
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=staging_db_password_2024

# 应用配置
SPRING_PROFILES_ACTIVE=staging
RAILWAY_STATIC_URL=https://wanli-backend-staging-staging.up.railway.app
PORT=8080

# JWT配置（Staging专用密钥）
JWT_SECRET=stagingSecretKeyForWanliEducationBackendSystem2024Testing
JWT_ACCESS_TOKEN_EXPIRATION=3600000
JWT_REFRESH_TOKEN_EXPIRATION=86400000

# 日志配置
LOGGING_LEVEL_COM_WANLI=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=INFO
LOGGING_FILE_NAME=logs/wanli-backend-staging.log

# Railway系统变量（自动生成）
RAILWAY_ENVIRONMENT=staging
RAILWAY_SERVICE_NAME=wanli-backend-staging
RAILWAY_PROJECT_NAME=beneficial-beauty
```

### application-staging.yml完整配置
```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: ${SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE:20}
      minimum-idle: ${SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE:10}
      idle-timeout: ${SPRING_DATASOURCE_HIKARI_IDLE_TIMEOUT:600000}
      connection-timeout: ${SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT:30000}
      leak-detection-threshold: ${SPRING_DATASOURCE_HIKARI_LEAK_DETECTION_THRESHOLD:60000}
  jpa:
    hibernate:
      ddl-auto: ${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
    show-sql: ${SPRING_JPA_SHOW_SQL:false}
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          time_zone: ${SPRING_JPA_PROPERTIES_HIBERNATE_JDBC_TIME_ZONE:UTC}

jwt:
  secret: ${JWT_SECRET}
  access-token-expiration: ${JWT_ACCESS_TOKEN_EXPIRATION:3600000}
  refresh-token-expiration: ${JWT_REFRESH_TOKEN_EXPIRATION:86400000}

logging:
  level:
    com.wanli: ${LOGGING_LEVEL_COM_WANLI:INFO}
    org.springframework.security: ${LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY:INFO}
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: ${LOGGING_FILE_NAME:logs/wanli-backend-staging.log}
```

### Staging部署流程
```bash
# 1. 切换到staging分支
git checkout staging
git pull origin staging

# 2. 连接Railway staging环境
railway login
railway link beneficial-beauty
railway environment staging
railway service wanli-backend-staging

# 3. 验证环境变量
railway variables

# 4. 部署应用
railway up

# 5. 验证部署
railway status
railway logs
curl https://wanli-backend-staging-staging.up.railway.app/api/health
```

---

## Production环境完整配置

### 基本信息
- **环境名称**: production
- **服务名称**: wanli-backend
- **部署域名**: https://wanli-backend-production.up.railway.app
- **配置文件**: application-prod.yml
- **Profile**: production

### Railway环境变量配置（完整版）
```bash
# 数据库配置（生产环境实际值）
SPRING_DATASOURCE_URL=jdbc:postgresql://gondola.proxy.rlwy.net:55880/railway
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=production_secure_password_2024

# 应用配置
SPRING_PROFILES_ACTIVE=production
PORT=8080
APP_VERSION=1.0.0

# JWT配置（生产环境强密钥）
JWT_SECRET=productionSecretKeyForWanliEducationBackendSystem2024
JWT_ACCESS_TOKEN_EXPIRATION=3600000
JWT_REFRESH_TOKEN_EXPIRATION=7200000

# 数据库连接池配置
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=30
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=10
SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT=30000
SPRING_DATASOURCE_HIKARI_IDLE_TIMEOUT=600000
SPRING_DATASOURCE_HIKARI_MAX_LIFETIME=1800000

# 日志配置
LOGGING_LEVEL_COM_WANLI=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY=WARN
LOGGING_FILE_NAME=/tmp/wanli-backend-production.log

# Railway系统变量（自动生成）
RAILWAY_ENVIRONMENT=production
RAILWAY_SERVICE_NAME=wanli-backend
RAILWAY_PROJECT_NAME=beneficial-beauty
```

### application-prod.yml完整配置
```yaml
# Production环境配置
server:
  port: ${PORT:8080}
  servlet:
    context-path: /api
  error:
    include-message: never
    include-binding-errors: never
    include-stacktrace: never

spring:
  application:
    name: wanli-backend-production
  
  # 数据源配置 - Railway PostgreSQL Production
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/wanli_backend_prod}
    username: ${SPRING_DATASOURCE_USERNAME:prod_user}
    password: ${SPRING_DATASOURCE_PASSWORD:prod_password}
    driver-class-name: org.postgresql.Driver
    hikari:
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      maximum-pool-size: 30
      minimum-idle: 10
      leak-detection-threshold: 60000
      connection-test-query: SELECT 1
      
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: false
        jdbc:
          time_zone: Asia/Shanghai
          batch_size: 20
        connection:
          provider_disables_autocommit: true
        cache:
          use_second_level_cache: true
          use_query_cache: true
  
  # Jackson配置
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai
    default-property-inclusion: NON_NULL
    serialization:
      write-dates-as-timestamps: false

# JWT配置
jwt:
  secret: ${JWT_SECRET:productionSecretKeyForWanliEducationBackendSystem2024}
  access-token-expiration: ${JWT_ACCESS_TOKEN_EXPIRATION:3600000}
  refresh-token-expiration: ${JWT_REFRESH_TOKEN_EXPIRATION:7200000}

# 日志配置
logging:
  level:
    com.wanli: info
    org.springframework.web: warn
    org.springframework.security: warn
    org.hibernate.SQL: warn
    org.hibernate.type.descriptor.sql.BasicBinder: warn
    org.springframework.boot.autoconfigure: warn
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /tmp/wanli-backend-production.log
    max-size: 100MB
    max-history: 30

# Railway特定配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
    metrics:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true

# 环境标识
app:
  environment: production
  version: ${APP_VERSION:1.0.0}
  railway:
    deployment: true

# 安全配置
security:
  require-ssl: false
  headers:
    frame-options: DENY
    content-type-options: nosniff
    xss-protection: 1; mode=block
```

### Production部署流程
```bash
# 1. 切换到main分支
git checkout main
git pull origin main

# 2. 连接Railway production环境
railway login
railway link beneficial-beauty
railway environment production
railway service wanli-backend

# 3. 验证环境变量
railway variables

# 4. 部署应用
railway up

# 5. 验证部署
railway status
railway logs
curl https://wanli-backend-production.up.railway.app/api/actuator/health
```

---

## 数据库配置详情

### PostgreSQL服务信息
- **服务名称**: Postgres (在Railway中)
- **版本**: PostgreSQL 17.6
- **客户端版本**: psql 14.18
- **连接方式**: 通过Railway环境变量DATABASE_URL自动配置

### 实际数据库连接信息

#### Staging环境
```bash
# 数据库连接信息
Host: viaduct.proxy.rlwy.net
Port: 12345
Database: railway
Username: postgres
Password: staging_db_password_2024

# 完整连接URL
jdbc:postgresql://viaduct.proxy.rlwy.net:12345/railway
```

#### Production环境
```bash
# 数据库连接信息
Host: gondola.proxy.rlwy.net
Port: 55880
Database: railway
Username: postgres
Password: production_secure_password_2024

# 完整连接URL
jdbc:postgresql://gondola.proxy.rlwy.net:55880/railway
```

### 数据库连接池配置对比

| 配置项 | Staging | Production |
|--------|---------|------------|
| 最大连接数 | 20 | 30 |
| 最小空闲连接 | 10 | 10 |
| 连接超时 | 30秒 | 30秒 |
| 空闲超时 | 600秒 | 600秒 |
| 最大生命周期 | - | 1800秒 |
| 泄漏检测阈值 | 60秒 | 60秒 |

### 数据库操作命令
```bash
# 连接到PostgreSQL (staging)
railway environment staging
railway connect Postgres

# 连接到PostgreSQL (production)
railway environment production
railway connect Postgres

# 直接使用psql连接
# Staging
psql postgresql://postgres:staging_db_password_2024@viaduct.proxy.rlwy.net:12345/railway

# Production
psql postgresql://postgres:production_secure_password_2024@gondola.proxy.rlwy.net:55880/railway
```

---

## 环境变量管理详情

### 查看环境变量
```bash
# 查看所有环境变量
railway variables

# 查看特定变量
railway variables | grep SPRING_DATASOURCE_URL
railway variables | grep JWT_SECRET
```

### 设置关键环境变量
```bash
# 数据库配置
railway variables --set SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/database
railway variables --set SPRING_DATASOURCE_USERNAME=postgres
railway variables --set SPRING_DATASOURCE_PASSWORD=your_password

# JWT配置
railway variables --set JWT_SECRET=your_jwt_secret_key
railway variables --set JWT_ACCESS_TOKEN_EXPIRATION=3600000
railway variables --set JWT_REFRESH_TOKEN_EXPIRATION=7200000

# 应用配置
railway variables --set SPRING_PROFILES_ACTIVE=staging  # 或 production
railway variables --set PORT=8080
```

### 敏感信息管理
1. **JWT密钥**：
   - Staging: `stagingSecretKeyForWanliEducationBackendSystem2024Testing`
   - Production: `productionSecretKeyForWanliEducationBackendSystem2024`

2. **数据库密码**：
   - 通过Railway自动生成和管理
   - 定期轮换（建议每季度）

3. **环境变量安全**：
   - 不在代码中硬编码敏感信息
   - 使用Railway的环境变量管理
   - 定期审查和更新

---

## 部署验证清单

### Staging环境验证
- [ ] 应用成功启动 (`railway logs`)
- [ ] 数据库连接正常
- [ ] Profile为staging
- [ ] 健康检查端点可访问 (`/api/health`)
- [ ] JWT配置正确
- [ ] 日志输出正常
- [ ] 环境变量正确设置
- [ ] 数据库连接池正常工作

### Production环境验证
- [ ] 应用成功启动 (`railway logs`)
- [ ] 数据库连接正常
- [ ] Profile为production
- [ ] 健康检查端点可访问 (`/api/actuator/health`)
- [ ] JWT配置正确
- [ ] 监控端点可访问 (`/api/actuator/metrics`)
- [ ] 安全配置生效
- [ ] 日志文件正常写入
- [ ] 连接池配置优化生效
- [ ] 缓存配置正常

---

## 故障排除指南

### 常见问题及解决方案

#### 1. 数据库连接失败
**症状**: `Driver org.postgresql.Driver claims to not accept jdbcUrl`
**原因**: 数据库URL格式错误
**解决方案**:
```bash
# 检查当前URL
railway variables | grep SPRING_DATASOURCE_URL

# 修正URL格式（必须包含jdbc:postgresql://前缀）
railway variables --set SPRING_DATASOURCE_URL=jdbc:postgresql://gondola.proxy.rlwy.net:55880/railway

# 重新部署
railway redeploy
```

#### 2. JWT认证失败
**症状**: Token验证失败或无法生成Token
**排查步骤**:
```bash
# 检查JWT密钥
railway variables | grep JWT_SECRET

# 确认密钥长度和复杂度
# Staging: stagingSecretKeyForWanliEducationBackendSystem2024Testing
# Production: productionSecretKeyForWanliEducationBackendSystem2024
```

#### 3. 应用启动失败
**症状**: 应用无法启动或启动超时
**排查步骤**:
```bash
# 查看详细日志
railway logs --tail 100

# 检查所有环境变量
railway variables

# 检查服务状态
railway status

# 重新选择正确的服务
railway service wanli-backend  # 或 wanli-backend-staging
```

#### 4. Profile未正确激活
**症状**: 使用了错误的配置文件
**解决方案**:
```bash
# 检查SPRING_PROFILES_ACTIVE变量
railway variables | grep SPRING_PROFILES_ACTIVE

# 设置正确的profile
railway variables --set SPRING_PROFILES_ACTIVE=staging  # 或 production

# 重新部署
railway redeploy
```

#### 5. 部署无响应
**症状**: `railway up` 或 `railway redeploy` 无响应
**解决方案**:
```bash
# 停止当前部署
Ctrl+C

# 重新登录和连接
railway login
railway link beneficial-beauty

# 重新选择环境和服务
railway environment production  # 或 staging
railway service wanli-backend   # 或 wanli-backend-staging

# 重新部署
railway up
```

### 调试命令集合
```bash
# 基本信息
railway whoami
railway status
railway service
railway environment
railway projects

# 日志和监控
railway logs
railway logs --tail 50
railway logs --follow

# 环境变量
railway variables
railway variables --set KEY=VALUE
railway variables --remove KEY

# 部署操作
railway up
railway redeploy
railway restart

# 数据库连接
railway connect Postgres

# 域名管理
railway domain
```

---

## 安全配置详情

### JWT安全配置
```yaml
# 生产环境JWT配置
jwt:
  secret: productionSecretKeyForWanliEducationBackendSystem2024
  access-token-expiration: 3600000   # 1小时
  refresh-token-expiration: 7200000  # 2小时
  
# 测试环境JWT配置
jwt:
  secret: stagingSecretKeyForWanliEducationBackendSystem2024Testing
  access-token-expiration: 3600000   # 1小时
  refresh-token-expiration: 86400000 # 24小时（测试环境可以更长）
```

### 数据库安全配置
```yaml
# 生产环境数据库安全配置
spring:
  datasource:
    hikari:
      connection-test-query: SELECT 1
      leak-detection-threshold: 60000
      connection-timeout: 30000
  jpa:
    properties:
      hibernate:
        connection:
          provider_disables_autocommit: true
```

### 应用安全配置
```yaml
# 生产环境安全头配置
security:
  require-ssl: false  # Railway处理SSL终止
  headers:
    frame-options: DENY
    content-type-options: nosniff
    xss-protection: 1; mode=block

# 错误信息隐藏
server:
  error:
    include-message: never
    include-binding-errors: never
    include-stacktrace: never
```

---

## 监控和日志配置

### 生产环境监控端点
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
    metrics:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
```

### 访问监控端点
```bash
# 健康检查
curl https://wanli-backend-production.up.railway.app/api/actuator/health

# 应用信息
curl https://wanli-backend-production.up.railway.app/api/actuator/info

# 性能指标
curl https://wanli-backend-production.up.railway.app/api/actuator/metrics

# Prometheus指标
curl https://wanli-backend-production.up.railway.app/api/actuator/prometheus
```

### 日志配置详情
```yaml
# 生产环境日志配置
logging:
  level:
    com.wanli: info                    # 应用日志
    org.springframework.web: warn      # Spring Web日志
    org.springframework.security: warn # 安全日志
    org.hibernate.SQL: warn           # SQL日志（生产环境关闭）
    org.hibernate.type.descriptor.sql.BasicBinder: warn
  file:
    name: /tmp/wanli-backend-production.log
    max-size: 100MB
    max-history: 30
```

---

## 性能优化配置

### 数据库连接池优化
```yaml
# 生产环境连接池配置
spring:
  datasource:
    hikari:
      maximum-pool-size: 30        # 最大连接数
      minimum-idle: 10             # 最小空闲连接
      connection-timeout: 30000    # 连接超时30秒
      idle-timeout: 600000         # 空闲超时10分钟
      max-lifetime: 1800000        # 最大生命周期30分钟
      leak-detection-threshold: 60000  # 泄漏检测1分钟
```

### JPA性能优化
```yaml
# 生产环境JPA优化
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 20           # 批处理大小
        connection:
          provider_disables_autocommit: true
        cache:
          use_second_level_cache: true    # 启用二级缓存
          use_query_cache: true          # 启用查询缓存
```

---

## 备份和恢复

### 数据库备份
```bash
# 生产环境数据库备份
railway environment production
railway connect Postgres

# 在psql中执行备份
\copy (SELECT * FROM your_table) TO '/tmp/backup.csv' CSV HEADER;

# 或使用pg_dump
pg_dump postgresql://postgres:production_secure_password_2024@gondola.proxy.rlwy.net:55880/railway > backup.sql
```

### 配置文件备份
```bash
# 备份所有配置文件
cp src/main/resources/application.yml backup/
cp src/main/resources/application-staging.yml backup/
cp src/main/resources/application-prod.yml backup/

# 备份环境变量配置
railway variables > backup/staging-env-vars.txt
railway environment production
railway variables > backup/production-env-vars.txt
```

---

## 版本信息和更新记录

- **文档版本**: 2.0 (内部完整版)
- **创建日期**: 2025-01-28
- **最后更新**: 2025-01-28
- **适用版本**: Spring Boot 3.5.0, Java 17
- **部署平台**: Railway
- **数据库**: PostgreSQL 17.6

### 技术栈版本详情
- **Spring Boot**: 3.5.0
- **Java**: 17
- **Maven**: 3.11.0
- **PostgreSQL**: 17.6
- **JWT**: 0.12.3
- **Lombok**: 1.18.30
- **SpringDoc OpenAPI**: 2.2.0
- **JaCoCo**: 0.8.12

---

## 注意事项

⚠️ **安全警告**：
- 本文档包含生产环境的敏感配置信息
- 严禁将此文档提交到公共代码仓库
- 仅限授权人员访问
- 定期更新密码和密钥
- 遵循最小权限原则

📋 **使用说明**：
- 部署前务必验证所有配置
- 严格按照GitFlow流程操作
- 生产环境变更需要审批
- 保持文档与实际配置同步

🔒 **访问控制**：
- 文档存储在本地安全目录
- 定期审查访问权限
- 记录所有配置变更
- 建立配置变更审计日志