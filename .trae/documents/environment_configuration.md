# 环境配置文档

## 1. 文档概述

### 1.1 文档目的

本文档详细描述了Wanli Academy项目在不同环境下的配置要求和部署步骤，确保开发、测试、预发布和生产环境的一致性和稳定性。

### 1.2 适用范围

* 开发环境 (Development)

* 测试环境 (Testing)

* 预发布环境 (Staging)

* 生产环境 (Production)

### 1.3 版本信息

* 文档版本：v1.0.0

* Spring Boot版本：3.5.x

* Java版本：17+

* 创建日期：2025-01-16

## 2. 系统要求

### 2.1 硬件要求

#### 开发环境

* CPU: 2核心以上

* 内存: 8GB以上

* 磁盘: 20GB可用空间

* 网络: 稳定的互联网连接

#### 测试环境

* CPU: 2核心以上

* 内存: 4GB以上

* 磁盘: 10GB可用空间

* 网络: 稳定的内网连接

#### 生产环境

* CPU: 4核心以上

* 内存: 16GB以上

* 磁盘: 100GB可用空间（SSD推荐）

* 网络: 高速稳定连接

### 2.2 软件要求

#### 必需软件

* Java 17+ (OpenJDK或Oracle JDK)

* Maven 3.8+

* MySQL 8.0+

* Git 2.30+

#### 推荐软件

* IntelliJ IDEA 2023+

* Docker 20.10+

* Docker Compose 2.0+

* Postman (API测试)

## 3. 开发环境配置

### 3.1 Java环境配置

```bash
# 检查Java版本
java -version

# 设置JAVA_HOME环境变量
export JAVA_HOME=/path/to/java17
export PATH=$JAVA_HOME/bin:$PATH

# 验证配置
echo $JAVA_HOME
javac -version
```

### 3.2 Maven配置

```bash
# 检查Maven版本
mvn -version

# 配置Maven镜像源（可选）
# 编辑 ~/.m2/settings.xml
```

**settings.xml示例**:

```xml
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <name>Aliyun Central</name>
      <url>https://maven.aliyun.com/repository/central</url>
    </mirror>
  </mirrors>
</settings>
```

### 3.3 MySQL数据库配置

#### 安装MySQL

```bash
# macOS (使用Homebrew)
brew install mysql
brew services start mysql

# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql

# CentOS/RHEL
sudo yum install mysql-server
sudo systemctl start mysqld
```

#### 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE wanli_academy_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE wanli_academy_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'wanli_dev'@'localhost' IDENTIFIED BY 'dev_password_123';
CREATE USER 'wanli_test'@'localhost' IDENTIFIED BY 'test_password_123';

-- 授权
GRANT ALL PRIVILEGES ON wanli_academy_dev.* TO 'wanli_dev'@'localhost';
GRANT ALL PRIVILEGES ON wanli_academy_test.* TO 'wanli_test'@'localhost';
FLUSH PRIVILEGES;
```

### 3.4 项目配置

#### application-dev.yml

```yaml
spring:
  profiles:
    active: dev
  
  datasource:
    url: jdbc:mysql://localhost:3306/wanli_academy_dev?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: wanli_dev
    password: dev_password_123
    driver-class-name: com.mysql.cj.jdbc.Driver
    
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1200000
      pool-name: WanliHikariCP-Dev
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
  
  logging:
    level:
      com.wanli: DEBUG
      org.springframework.security: DEBUG
      org.hibernate.SQL: DEBUG
      org.hibernate.type.descriptor.sql.BasicBinder: TRACE

jwt:
  secret: dev-secret-key-for-jwt-token-generation-minimum-256-bits
  access-token-expiration: 86400000  # 24小时
  refresh-token-expiration: 604800000 # 7天

server:
  port: 8080
  servlet:
    context-path: /api

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

#### application-test.yml

```yaml
spring:
  profiles:
    active: test
  
  datasource:
    url: jdbc:mysql://localhost:3306/wanli_academy_test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: wanli_test
    password: test_password_123
    driver-class-name: com.mysql.cj.jdbc.Driver
    
    hikari:
      maximum-pool-size: 5
      minimum-idle: 2
      idle-timeout: 300000
      connection-timeout: 20000
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
  
  logging:
    level:
      com.wanli: INFO
      org.springframework.security: WARN
      org.hibernate.SQL: WARN

jwt:
  secret: test-secret-key-for-jwt-token-generation-minimum-256-bits
  access-token-expiration: 3600000   # 1小时
  refresh-token-expiration: 86400000  # 24小时

server:
  port: 8081
```

## 4. Docker环境配置

### 4.1 Dockerfile

```dockerfile
# 多阶段构建
FROM maven:3.8.6-openjdk-17-slim AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:17-jre-slim

WORKDIR /app

# 创建非root用户
RUN groupadd -r wanli && useradd -r -g wanli wanli

# 复制jar文件
COPY --from=build /app/target/*.jar app.jar

# 更改文件所有者
RUN chown wanli:wanli app.jar

# 切换到非root用户
USER wanli

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
```

### 4.2 docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: wanli-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root_password_123
      MYSQL_DATABASE: wanli_academy
      MYSQL_USER: wanli_user
      MYSQL_PASSWORD: wanli_password_123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./docker/mysql/init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - wanli-network
    restart: unless-stopped
    command: --default-authentication-plugin=mysql_native_password

  app:
    build: .
    container_name: wanli-app
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/wanli_academy?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: wanli_user
      SPRING_DATASOURCE_PASSWORD: wanli_password_123
      JWT_SECRET: docker-secret-key-for-jwt-token-generation-minimum-256-bits
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    networks:
      - wanli-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

volumes:
  mysql_data:

networks:
  wanli-network:
    driver: bridge
```

### 4.3 application-docker.yml

```yaml
spring:
  profiles:
    active: docker
  
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1200000
      pool-name: WanliHikariCP-Docker
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
  
  logging:
    level:
      com.wanli: INFO
      org.springframework.security: WARN
      org.hibernate.SQL: WARN
    pattern:
      console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

jwt:
  secret: ${JWT_SECRET}
  access-token-expiration: 86400000
  refresh-token-expiration: 604800000

server:
  port: 8080
  servlet:
    context-path: /api

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true
```

## 5. 生产环境配置

### 5.1 系统优化

```bash
# JVM参数优化
JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/wanli/"

# 系统参数优化
echo 'vm.swappiness=10' >> /etc/sysctl.conf
echo 'net.core.somaxconn=65535' >> /etc/sysctl.conf
sysctl -p
```

### 5.2 application-prod.yml

```yaml
spring:
  profiles:
    active: prod
  
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      idle-timeout: 600000
      connection-timeout: 30000
      max-lifetime: 1800000
      pool-name: WanliHikariCP-Prod
      leak-detection-threshold: 60000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
  
  logging:
    level:
      com.wanli: INFO
      org.springframework.security: WARN
      org.hibernate.SQL: WARN
    file:
      name: /var/log/wanli/application.log
    logback:
      rollingpolicy:
        max-file-size: 100MB
        max-history: 30

jwt:
  secret: ${JWT_SECRET}
  access-token-expiration: 86400000
  refresh-token-expiration: 604800000

server:
  port: 8080
  servlet:
    context-path: /api
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain
  http2:
    enabled: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: never
  metrics:
    export:
      prometheus:
        enabled: true
```

## 6. 环境变量配置

### 6.1 开发环境变量

```bash
# .env.dev
SPRING_PROFILES_ACTIVE=dev
DATABASE_URL=jdbc:mysql://localhost:3306/wanli_academy_dev
DATABASE_USERNAME=wanli_dev
DATABASE_PASSWORD=dev_password_123
JWT_SECRET=dev-secret-key-for-jwt-token-generation-minimum-256-bits
LOG_LEVEL=DEBUG
```

### 6.2 生产环境变量

```bash
# .env.prod
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:mysql://prod-mysql:3306/wanli_academy
DATABASE_USERNAME=wanli_prod
DATABASE_PASSWORD=${PROD_DB_PASSWORD}
JWT_SECRET=${PROD_JWT_SECRET}
LOG_LEVEL=INFO
```

## 7. 部署脚本

### 7.1 开发环境启动脚本

```bash
#!/bin/bash
# start-dev.sh

echo "Starting Wanli Academy Development Environment..."

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java 17+"
    exit 1
fi

# 检查MySQL服务
if ! pgrep -x "mysqld" > /dev/null; then
    echo "MySQL is not running. Starting MySQL..."
    brew services start mysql || sudo systemctl start mysql
fi

# 构建项目
echo "Building project..."
mvn clean compile

# 运行应用
echo "Starting application..."
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 7.2 Docker部署脚本

```bash
#!/bin/bash
# deploy-docker.sh

echo "Deploying Wanli Academy with Docker..."

# 停止现有容器
docker-compose down

# 清理旧镜像
docker system prune -f

# 构建并启动
docker-compose up --build -d

# 等待服务启动
echo "Waiting for services to start..."
sleep 30

# 健康检查
echo "Checking application health..."
curl -f http://localhost:8080/api/actuator/health || {
    echo "Health check failed!"
    docker-compose logs app
    exit 1
}

echo "Deployment completed successfully!"
```

## 8. 监控和日志

### 8.1 日志配置

```xml
<!-- logback-spring.xml -->
<configuration>
    <springProfile name="dev">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>
    
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>/var/log/wanli/application.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>/var/log/wanli/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
                <maxFileSize>100MB</maxFileSize>
                <maxHistory>30</maxHistory>
                <totalSizeCap>3GB</totalSizeCap>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="FILE"/>
        </root>
    </springProfile>
</configuration>
```

### 8.2 监控配置

```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'wanli-academy'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/api/actuator/prometheus'
    scrape_interval: 5s
```

## 9. 故障排除

### 9.1 常见问题

#### 数据库连接失败

```bash
# 检查MySQL服务状态
sudo systemctl status mysql

# 检查端口占用
netstat -tlnp | grep 3306

# 测试数据库连接
mysql -h localhost -u wanli_dev -p wanli_academy_dev
```

#### 应用启动失败

```bash
# 检查Java版本
java -version

# 检查端口占用
lsof -i :8080

# 查看应用日志
tail -f /var/log/wanli/application.log
```

#### 内存不足

```bash
# 检查内存使用
free -h

# 检查JVM内存
jps -v | grep wanli

# 调整JVM参数
export JAVA_OPTS="-Xms1g -Xmx2g"
```

### 9.2 性能调优

#### 数据库优化

```sql
-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query_log';
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;

-- 分析表结构
ANALYZE TABLE users;
ANALYZE TABLE courses;
ANALYZE TABLE lessons;

-- 优化索引
SHOW INDEX FROM users;
EXPLAIN SELECT * FROM users WHERE email = 'test@example.com';
```

#### JVM调优

```bash
# G1GC参数
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:G1HeapRegionSize=16m
-XX:G1ReservePercent=25
-XX:InitiatingHeapOccupancyPercent=30

# 内存参数
-Xms4g
-Xmx4g
-XX:MetaspaceSize=256m
-XX:MaxMetaspaceSize=512m

# GC日志
-Xlog:gc*:gc.log:time,tags
```

## 10. 安全配置

### 10.1 网络安全

```bash
# 防火墙配置
sudo ufw allow 22/tcp
sudo ufw allow 8080/tcp
sudo ufw enable

# SSL证书配置（生产环境）
sudo certbot --nginx -d api.wanli-academy.com
```

### 10.2 应用安全

```yaml
# application-prod.yml 安全配置
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: wanli-academy

spring:
  security:
    require-ssl: true
```

***

**文档维护**

* 负责人：DevOps团队

* 更新频率：环境变更时更新

* 审核周期：每季度审核一次

