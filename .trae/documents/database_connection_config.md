# 数据库配置参考文档

## 概述

本文档记录了万里书院后端项目在不同环境下的正确数据库配置信息，包括连接参数、用户凭据等关键信息。当遇到数据库连接问题时，请参考此文档恢复正确配置。

## 环境配置总览

| 环境    | 部署平台    | 数据库类型      | 连接方式              | 配置文件                    |
| :---- | :------ | :--------- | :---------------- | :---------------------- |
| 开发环境  | 本地      | PostgreSQL | 本地连接              | application-dev.yml     |
| 测试环境  | Railway | PostgreSQL | DATABASE\_URL环境变量 | application-test.yml    |
| 预发布环境 | 本地      | PostgreSQL | 本地连接/环境变量         | application-staging.yml |
| 生产环境  | Railway | PostgreSQL | DATABASE\_URL环境变量 | application-prod.yml    |

## 详细配置信息

### 1. 开发环境 (Development)

**配置文件**: `src/main/resources/application-dev.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/wanli_backend_dev
    username: dev_user
    password: dev_password
    driver-class-name: org.postgresql.Driver
```

**连接信息**:

* **主机**: localhost

* **端口**: 5432

* **数据库名**: wanli\_backend\_dev

* **用户名**: dev\_user

* **密码**: dev\_password

* **JDBC URL**: `jdbc:postgresql://localhost:5432/wanli_backend_dev`

**psql 连接命令**:

```bash
psql -h localhost -U dev_user -d wanli_backend_dev
```

### 2. 测试环境 (Test) - Railway 部署

**配置文件**: `src/main/resources/application-test.yml`

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

**连接信息**:

* **平台**: Railway

* **主机**: Railway 提供的数据库主机

* **端口**: Railway 提供的端口

* **数据库名**: Railway 自动生成

* **用户名**: Railway 自动生成

* **密码**: Railway 自动生成

* **JDBC URL**: Railway 提供的 `DATABASE_URL` 环境变量

**环境变量**:

* `DATABASE_URL`: Railway 自动提供的完整数据库连接URL

**Railway 连接命令**:

```bash
# 通过 Railway CLI 连接
railway connect
railway run psql $DATABASE_URL
```

### 3. 预发布环境 (Staging)

**配置文件**: `src/main/resources/application-staging.yml`

```yaml
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/wanli_backend_staging}
    username: ${DATABASE_USERNAME:staging_user}
    password: ${DATABASE_PASSWORD:staging_password}
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
  hikari:
    maximum-pool-size: 25
    minimum-idle: 10
    connection-timeout: 30000
    idle-timeout: 600000
    max-lifetime: 1800000
    leak-detection-threshold: 60000
```

**连接信息**:

* **主机**: localhost (本地) / Railway PostgreSQL Host (部署时)

* **端口**: 5432 (本地) / Railway 动态端口 (部署时)

* **数据库名**: wanli\_backend\_staging (本地) / railway (Railway)

* **用户名**: staging\_user (本地) / postgres (Railway)

* **密码**: staging\_password (本地) / Railway 自动生成 (Railway)

* **JDBC URL**: `jdbc:postgresql://localhost:5432/wanli_backend_staging` (本地) / `${DATABASE_URL}` (Railway)

**环境变量**:

* `DATABASE_URL`: 完整的数据库连接URL

* `DATABASE_USERNAME`: 数据库用户名

* `DATABASE_PASSWORD`: 数据库密码

* `SPRING_PROFILES_ACTIVE`: staging

**psql 连接命令**:

```bash
# 本地连接
psql -h localhost -U staging_user -d wanli_backend_staging

# Railway 连接
railway environment staging
railway connect
railway run psql $DATABASE_URL
```

**HikariCP 连接池参数 (预发布)**:

* **最大连接数**: 25
* **最小空闲连接**: 10
* **连接超时**: 30000ms
* **空闲超时**: 600000ms
* **最大生命周期**: 1800000ms
* **泄漏检测阈值**: 60000ms

### 4. 生产环境 (Production) - Railway 部署

**配置文件**: `src/main/resources/application-prod.yml`

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
```

**连接信息**:

* **平台**: Railway

* **主机**: Railway 提供的数据库主机

* **端口**: Railway 提供的端口

* **数据库名**: Railway 自动生成

* **用户名**: Railway 自动生成

* **密码**: Railway 自动生成

* **JDBC URL**: Railway 提供的 `DATABASE_URL` 环境变量

**环境变量**:

* `DATABASE_URL`: Railway 自动提供的完整数据库连接URL

**Railway 连接命令**:

```bash
# 通过 Railway CLI 连接
railway connect
railway run psql $DATABASE_URL
```

## 连接池配置

### HikariCP 配置参数

| 参数                       | 开发环境      | 测试环境      | 预发布环境     | 生产环境      |
| :----------------------- | :-------- | :-------- | :-------- | :-------- |
| maximum-pool-size        | 10        | 8         | 10        | 20        |
| minimum-idle             | 5         | 3         | 5         | 10        |
| connection-timeout       | 20000ms   | 20000ms   | 30000ms   | 20000ms   |
| idle-timeout             | 300000ms  | 300000ms  | 600000ms  | 300000ms  |
| max-lifetime             | 1800000ms | 1800000ms | 1800000ms | 1800000ms |
| leak-detection-threshold | -         | -         | 60000ms   | 60000ms   |

## 数据库用户权限

### 开发环境用户权限

```sql
-- 开发环境用户 dev_user 需要的权限
GRANT ALL PRIVILEGES ON DATABASE wanli_backend_dev TO dev_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO dev_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO dev_user;
```

### 测试环境用户权限

```sql
-- 测试环境用户 test_user 需要的权限
GRANT ALL PRIVILEGES ON DATABASE wanli_backend_test TO test_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO test_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO test_user;
```

## 常见问题与解决方案

### 1. 连接被拒绝 (Connection refused)

**问题**: `Connection to localhost:5432 refused`

**解决方案**:

1. 检查 PostgreSQL 服务是否启动

   ```bash
   brew services start postgresql
   # 或
   pg_ctl -D /usr/local/var/postgres start
   ```
2. 检查端口是否正确 (默认 5432)
3. 检查防火墙设置

### 2. 认证失败 (Authentication failed)

**问题**: `FATAL: password authentication failed for user "xxx"`

**解决方案**:

1. 确认用户名和密码正确
2. 检查用户是否存在

   ```sql
   SELECT usename FROM pg_user WHERE usename = 'dev_user';
   ```
3. 重置用户密码

   ```sql
   ALTER USER dev_user PASSWORD 'dev_password';
   ```

### 3. 数据库不存在 (Database does not exist)

**问题**: `FATAL: database "wanli_backend_dev" does not exist`

**解决方案**:

1. 创建数据库

   ```sql
   CREATE DATABASE wanli_backend_dev OWNER dev_user;
   ```
2. 检查数据库名称拼写

### 4. 权限不足 (Permission denied)

**问题**: `permission denied for table xxx`

**解决方案**:

1. 授予表权限

   ```sql
   GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO dev_user;
   ```
2. 授予序列权限

   ```sql
   GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO dev_user;
   ```

### 5. 字段不存在 (Column does not exist)

**问题**: `column "created_by" does not exist`

**解决方案**:

1. 检查数据库迁移是否执行
2. 手动添加缺失字段

   ```sql
   ALTER TABLE users ADD COLUMN created_by VARCHAR(255);
   ALTER TABLE users ADD COLUMN updated_by VARCHAR(255);
   ```

## 数据库迁移检查

### 检查表结构

```sql
-- 检查 users 表结构
\d users

-- 检查所有表
\dt

-- 检查特定字段是否存在
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'users' AND table_schema = 'public';
```

### 检查用户权限

```sql
-- 检查当前用户权限
SELECT grantee, table_name, privilege_type 
FROM information_schema.role_table_grants 
WHERE table_schema = 'public' AND grantee IN ('dev_user', 'test_user', 'staging_user', 'prod_user')
ORDER BY table_name, grantee;
```

## 快速恢复命令

### 创建开发环境数据库和用户

```bash
# 连接到 PostgreSQL
psql -U postgres

# 创建用户和数据库
CREATE USER dev_user WITH PASSWORD 'dev_password';
CREATE DATABASE wanli_backend_dev OWNER dev_user;
GRANT ALL PRIVILEGES ON DATABASE wanli_backend_dev TO dev_user;

# 退出并测试连接
\q
psql -h localhost -U dev_user -d wanli_backend_dev
```

### 创建测试环境数据库和用户

```bash
# 连接到 PostgreSQL
psql -U postgres

# 创建用户和数据库
CREATE USER test_user WITH PASSWORD 'test_password';
CREATE DATABASE wanli_backend_test OWNER test_user;
GRANT ALL PRIVILEGES ON DATABASE wanli_backend_test TO test_user;

# 退出并测试连接
\q
psql -h localhost -U test_user -d wanli_backend_test
```

## Railway 部署配置

### Railway 环境变量

Railway 会自动提供以下环境变量：

* `DATABASE_URL`: 完整的 PostgreSQL 连接字符串 (主要使用)

* `DATABASE_PRIVATE_URL`: 内部网络连接URL

* `DATABASE_HOST`: 数据库主机地址

* `DATABASE_PORT`: 数据库端口

* `DATABASE_NAME`: 数据库名称 (通常为 railway)

* `DATABASE_USERNAME`: 数据库用户名 (通常为 postgres)

* `DATABASE_PASSWORD`: 数据库密码 (Railway自动生成)

### Railway CLI 常用命令

```bash
# 安装 Railway CLI
npm install -g @railway/cli

# 登录 Railway
railway login

# 连接到项目
railway link

# 查看环境变量
railway variables

# 连接到数据库
railway connect
railway run psql $DATABASE_URL

# 部署应用
railway up
```

### Railway 数据库管理

```bash
# 查看数据库状态
railway status

# 查看日志
railway logs

# 执行数据库迁移
railway run -- java -jar app.jar --spring.profiles.active=prod
```

## 注意事项

1. **密码安全**: 生产环境密码应使用环境变量，不要硬编码在配置文件中
2. **Railway 自动管理**: 测试和生产环境的数据库由 Railway 自动管理，无需手动创建用户和数据库
3. **连接池**: 根据应用负载调整连接池大小
4. **权限最小化**: 生产环境应使用最小权限原则
5. **备份**: Railway 提供自动备份，但建议定期手动备份重要数据
6. **监控**: 配置数据库连接监控和告警
7. **本地开发**: 本地开发环境仍使用本地 PostgreSQL 数据库

## 更新记录

* 2025-01-27: 更新数据库配置信息

  * 纠正数据库类型：本地开发环境使用 PostgreSQL

  * 更新部署平台：测试和生产环境部署在 Railway 平台

  * 添加 Railway 部署相关配置和 CLI 命令

  * 更新环境变量配置方式

* 2025-08-26: 创建初始版本，包含所有环境配置信息

  * 记录了开发、测试、预发布、生产四个环境的完整配置

  * 添加了常见问题解决方案和快速恢复命令

