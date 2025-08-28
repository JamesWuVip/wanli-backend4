# 数据库部署指南

万里教育后台管理系统数据库初始化和部署指南

## 概述

本指南详细说明如何在Railway的staging和production环境中执行数据库初始化脚本。

## 环境说明

- **Dev环境**: 本地PostgreSQL数据库 `wanli_backend_dev`
- **Staging环境**: Railway PostgreSQL数据库（包含测试数据）
- **Production环境**: Railway PostgreSQL数据库（仅表结构，无测试数据）

## 初始化脚本说明

### 1. database_init_staging.sql
- **用途**: Staging环境数据库初始化
- **内容**: 完整表结构 + 测试数据
- **测试账户**:
  - 管理员: `admin@wanli.com` / `admin123`
  - 教师: `teacher@wanli.com` / `teacher123`
  - 学生: `student@wanli.com` / `student123`

### 2. database_init_production.sql
- **用途**: Production环境数据库初始化
- **内容**: 仅表结构，无测试数据
- **注意**: 需要手动创建管理员用户

## Railway数据库连接

### 1. 获取数据库连接信息

```bash
# 切换到staging环境
railway environment staging
railway variables

# 切换到production环境
railway environment production
railway variables
```

### 2. 连接数据库

```bash
# 方法1: 使用Railway CLI连接
railway connect postgres

# 方法2: 使用psql直接连接
psql "postgresql://username:password@host:port/database"
```

## 部署步骤

### Staging环境部署

1. **切换到staging环境**
   ```bash
   railway environment staging
   ```

2. **连接到staging数据库**
   ```bash
   railway connect postgres
   ```

3. **执行初始化脚本**
   ```sql
   \i database_init_staging.sql
   ```

4. **验证部署**
   ```sql
   -- 检查表是否创建成功
   \dt
   
   -- 检查测试数据
   SELECT COUNT(*) FROM franchises;
   SELECT COUNT(*) FROM users;
   SELECT COUNT(*) FROM courses;
   
   -- 验证测试账户
   SELECT username, email, role FROM users WHERE role = 'ADMIN';
   ```

### Production环境部署

1. **切换到production环境**
   ```bash
   railway environment production
   ```

2. **连接到production数据库**
   ```bash
   railway connect postgres
   ```

3. **执行初始化脚本**
   ```sql
   \i database_init_production.sql
   ```

4. **创建管理员用户**
   ```sql
   -- 首先创建一个加盟商（如果需要）
   INSERT INTO franchises (id, name, status) 
   VALUES (gen_random_uuid(), '总部', 'ACTIVE');
   
   -- 创建管理员用户
   INSERT INTO users (
       id, franchise_id, username, email, role, 
       full_name, password_hash, login_attempts, status
   ) VALUES (
       gen_random_uuid(),
       (SELECT id FROM franchises WHERE name = '总部' LIMIT 1),
       'admin',
       'admin@wanli.com',
       'ADMIN',
       '系统管理员',
       '$2a$10$encrypted_password_hash', -- 需要使用实际的加密密码
       0,
       'ACTIVE'
   );
   ```

5. **验证部署**
   ```sql
   -- 检查表结构
   \dt
   
   -- 确认无测试数据（除了刚创建的管理员）
   SELECT COUNT(*) FROM users;
   SELECT COUNT(*) FROM franchises;
   
   -- 验证管理员账户
   SELECT username, email, role FROM users WHERE role = 'ADMIN';
   ```

## 验证步骤

### 1. 表结构验证

```sql
-- 检查所有表是否存在
\dt

-- 应该看到以下10张表：
-- class_members, classes, courses, franchises, homeworks
-- lessons, questions, student_answers, submissions, users
```

### 2. 约束验证

```sql
-- 检查主键约束
SELECT conname, contype FROM pg_constraint 
WHERE contype = 'p' AND connamespace = 'public'::regnamespace;

-- 检查外键约束
SELECT conname, contype FROM pg_constraint 
WHERE contype = 'f' AND connamespace = 'public'::regnamespace;

-- 检查唯一约束
SELECT conname, contype FROM pg_constraint 
WHERE contype = 'u' AND connamespace = 'public'::regnamespace;
```

### 3. 索引验证

```sql
-- 检查索引
SELECT indexname, tablename FROM pg_indexes 
WHERE schemaname = 'public' ORDER BY tablename, indexname;
```

### 4. 数据验证（仅Staging）

```sql
-- 检查测试数据
SELECT 
    'franchises' as table_name, COUNT(*) as count FROM franchises
UNION ALL
SELECT 'users', COUNT(*) FROM users
UNION ALL
SELECT 'courses', COUNT(*) FROM courses
UNION ALL
SELECT 'classes', COUNT(*) FROM classes
UNION ALL
SELECT 'class_members', COUNT(*) FROM class_members
UNION ALL
SELECT 'lessons', COUNT(*) FROM lessons;
```

## 故障排除

### 1. 连接问题

**问题**: 无法连接到Railway数据库

**解决方案**:
```bash
# 检查Railway登录状态
railway whoami

# 重新登录
railway login

# 检查环境设置
railway environment

# 检查项目设置
railway status
```

### 2. 权限问题

**问题**: 执行SQL时权限不足

**解决方案**:
```sql
-- 检查当前用户权限
SELECT current_user, session_user;

-- 检查数据库权限
\l

-- 如果需要，联系Railway支持或检查数据库用户配置
```

### 3. 表已存在错误

**问题**: 表已存在，无法创建

**解决方案**:
```sql
-- 检查现有表
\dt

-- 如果需要重新初始化，先删除所有表（谨慎操作）
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;

-- 然后重新执行初始化脚本
```

### 4. 外键约束错误

**问题**: 外键约束创建失败

**解决方案**:
```sql
-- 检查表创建顺序，确保被引用的表先创建
-- 如果遇到循环引用，可以先创建表，后添加外键

-- 临时禁用外键检查（不推荐在生产环境使用）
SET session_replication_role = replica;
-- 执行SQL
SET session_replication_role = DEFAULT;
```

## 清理数据库

### 完全重置数据库

```sql
-- 警告：这将删除所有数据！
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;

-- 重新执行初始化脚本
```

### 仅清理数据（保留表结构）

```sql
-- 按依赖顺序删除数据
TRUNCATE TABLE student_answers CASCADE;
TRUNCATE TABLE submissions CASCADE;
TRUNCATE TABLE questions CASCADE;
TRUNCATE TABLE homeworks CASCADE;
TRUNCATE TABLE lessons CASCADE;
TRUNCATE TABLE class_members CASCADE;
TRUNCATE TABLE classes CASCADE;
TRUNCATE TABLE courses CASCADE;
TRUNCATE TABLE users CASCADE;
TRUNCATE TABLE franchises CASCADE;
```

## 备份和恢复

### 创建备份

```bash
# 使用pg_dump创建备份
pg_dump "postgresql://username:password@host:port/database" > backup.sql

# 仅备份数据
pg_dump --data-only "postgresql://username:password@host:port/database" > data_backup.sql

# 仅备份结构
pg_dump --schema-only "postgresql://username:password@host:port/database" > schema_backup.sql
```

### 恢复备份

```bash
# 恢复完整备份
psql "postgresql://username:password@host:port/database" < backup.sql

# 恢复数据
psql "postgresql://username:password@host:port/database" < data_backup.sql
```

## 监控和维护

### 定期检查

```sql
-- 检查数据库大小
SELECT pg_size_pretty(pg_database_size(current_database()));

-- 检查表大小
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- 检查连接数
SELECT count(*) FROM pg_stat_activity;
```

### 性能优化

```sql
-- 更新表统计信息
ANALYZE;

-- 重建索引（如果需要）
REINDEX DATABASE current_database();

-- 清理死元组
VACUUM ANALYZE;
```

## 联系支持

如果遇到无法解决的问题，请联系：
- Railway支持: https://railway.app/help
- 项目维护者: [项目联系方式]

## 更新日志

- 2025-01-17: 初始版本创建
- 包含staging和production环境的完整部署流程
- 添加故障排除和维护指南