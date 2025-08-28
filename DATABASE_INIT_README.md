# 万里教育后台管理系统 - 数据库初始化指南

## 概述

本项目提供了完整的数据库初始化脚本，用于在不同环境中快速搭建万里教育后台管理系统的数据库结构。

## 文件说明

### 1. `database_init_script.sql`
- **用途**: 适用于 **staging** 环境
- **特点**: 包含完整的表结构、索引、约束和基础测试数据
- **测试数据**: 包含系统管理员、测试教师、测试学生等账户
- **密码**: 所有测试账户密码均为对应角色名+123（如admin123、teacher123、student123）

### 2. `database_init_production.sql`
- **用途**: 适用于 **production** 环境
- **特点**: 仅包含表结构、索引、约束，不包含任何测试数据
- **安全性**: 生产环境专用，需要手动添加初始管理员用户

### 3. `database_schema.sql`
- **用途**: 从本地dev环境导出的完整schema
- **特点**: 包含所有表结构定义，用于参考和对比

### 4. `initial_data.sql`
- **用途**: 从本地dev环境导出的初始数据
- **特点**: 包含一个测试用户的完整数据

## 数据库表结构

系统包含以下10张核心表：

1. **franchises** - 加盟商表
2. **users** - 用户表（支持管理员、教师、学生角色）
3. **courses** - 课程表
4. **classes** - 班级表
5. **class_members** - 班级成员关联表
6. **lessons** - 课时表
7. **homeworks** - 作业表
8. **questions** - 题目表
9. **submissions** - 作业提交表
10. **student_answers** - 学生答案表

## 使用方法

### Staging 环境初始化

```bash
# 连接到staging数据库
psql -h <staging_host> -U <username> -d <database_name>

# 执行初始化脚本
\i database_init_script.sql
```

### Production 环境初始化

```bash
# 连接到production数据库
psql -h <production_host> -U <username> -d <database_name>

# 执行初始化脚本
\i database_init_production.sql
```

### Railway 环境部署

如果使用Railway部署，可以通过Railway CLI执行：

```bash
# 连接到Railway数据库
railway connect

# 执行相应的初始化脚本
railway run psql $DATABASE_URL -f database_init_script.sql  # staging
railway run psql $DATABASE_URL -f database_init_production.sql  # production
```

## 测试账户信息（仅staging环境）

| 用户名 | 密码 | 角色 | 邮箱 | 用途 |
|--------|------|------|------|------|
| admin | admin123 | ADMIN | admin@wanli.edu | 系统管理员 |
| teacher | teacher123 | TEACHER | teacher@wanli.edu | 测试教师 |
| student | student123 | STUDENT | student@wanli.edu | 测试学生 |

## 生产环境注意事项

1. **安全性**: 生产环境脚本不包含任何默认用户，需要手动创建管理员账户
2. **密码策略**: 生产环境中的用户密码必须符合安全要求
3. **数据备份**: 执行脚本前请确保已备份现有数据
4. **权限控制**: 确保数据库用户具有创建表、索引和约束的权限

## 手动创建生产环境管理员用户

```sql
-- 创建生产环境管理员用户
-- 请将密码替换为安全的BCrypt加密密码
INSERT INTO public.users (
    id, username, email, role, full_name, 
    password_hash, login_attempts, status, 
    created_at, updated_at
) VALUES (
    gen_random_uuid(), 
    'your_admin_username',  -- 替换为实际管理员用户名
    'your_admin@company.com',  -- 替换为实际管理员邮箱
    'ADMIN', 
    '系统管理员',
    '$2a$10$your_bcrypt_password_hash',  -- 替换为实际BCrypt密码哈希
    0, 
    'ACTIVE', 
    CURRENT_TIMESTAMP, 
    CURRENT_TIMESTAMP
);
```

## 验证初始化结果

执行以下SQL验证数据库初始化是否成功：

```sql
-- 检查所有表是否创建成功
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;

-- 检查用户表结构
\d users

-- 检查是否有测试数据（仅staging环境）
SELECT username, role, status FROM users;

-- 检查外键约束
SELECT 
    tc.table_name, 
    tc.constraint_name, 
    tc.constraint_type
FROM information_schema.table_constraints tc
WHERE tc.table_schema = 'public' 
    AND tc.constraint_type = 'FOREIGN KEY'
ORDER BY tc.table_name;
```

## 故障排除

### 常见问题

1. **权限不足**: 确保数据库用户具有CREATE权限
2. **表已存在**: 脚本使用`IF NOT EXISTS`，可以安全重复执行
3. **外键约束错误**: 确保按照脚本顺序执行，先创建表再创建约束
4. **UUID扩展**: 确保PostgreSQL支持uuid-ossp扩展

### 清理数据库（谨慎操作）

如需重新初始化，可以使用以下命令清理数据库：

```sql
-- 警告：此操作将删除所有数据！
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
```

## 联系信息

如有问题，请联系开发团队或查看项目文档。