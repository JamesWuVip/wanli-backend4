-- V1.6: 优化用户表结构以支持JWT认证
-- 任务: T-012 用户认证数据库优化
-- 描述: 简化用户状态字段，移除冗余字段，优化表结构以支持JWT认证

-- 1. 移除冗余字段
ALTER TABLE public.users DROP COLUMN IF EXISTS first_name;
ALTER TABLE public.users DROP COLUMN IF EXISTS last_name;
ALTER TABLE public.users DROP COLUMN IF EXISTS avatar_url;
ALTER TABLE public.users DROP COLUMN IF EXISTS is_active;
ALTER TABLE public.users DROP COLUMN IF EXISTS email_verified;
ALTER TABLE public.users DROP COLUMN IF EXISTS version;
ALTER TABLE public.users DROP COLUMN IF EXISTS nickname;

-- 2. 简化用户状态管理
-- 保留status字段，但简化状态值
-- 只保留: ACTIVE, INACTIVE, LOCKED, DELETED
UPDATE public.users SET status = 'ACTIVE' WHERE status NOT IN ('ACTIVE', 'INACTIVE', 'LOCKED', 'DELETED');

-- 3. 优化字段类型和约束
-- 确保login_attempts有默认值
ALTER TABLE public.users ALTER COLUMN login_attempts SET DEFAULT 0;
ALTER TABLE public.users ALTER COLUMN login_attempts SET NOT NULL;

-- 确保status有默认值
ALTER TABLE public.users ALTER COLUMN status SET DEFAULT 'ACTIVE';
ALTER TABLE public.users ALTER COLUMN status SET NOT NULL;

-- 4. 添加JWT认证相关索引
-- 为JWT认证优化查询性能
CREATE INDEX IF NOT EXISTS idx_users_email_status ON public.users(email, status) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_users_username_status ON public.users(username, status) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_users_status_login_attempts ON public.users(status, login_attempts) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_users_locked_until ON public.users(locked_until) WHERE locked_until IS NOT NULL;

-- 5. 添加用户认证相关的复合索引
-- 用于登录验证的复合索引
CREATE INDEX IF NOT EXISTS idx_users_login_verification ON public.users(email, password_hash, status) WHERE deleted_at IS NULL;

-- 6. 优化现有索引
-- 删除可能重复的索引，重新创建优化的索引
DROP INDEX IF EXISTS idx_users_email;
DROP INDEX IF EXISTS idx_users_username;

-- 重新创建优化的唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email_unique ON public.users(email) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username_unique ON public.users(username) WHERE deleted_at IS NULL;

-- 7. 添加表注释
COMMENT ON TABLE public.users IS 'Users table optimized for JWT authentication - V1.6: Simplified status fields, removed redundant columns, added JWT-specific indexes';

-- 8. 添加字段注释
COMMENT ON COLUMN public.users.status IS 'User status: ACTIVE, INACTIVE, LOCKED, DELETED';
COMMENT ON COLUMN public.users.login_attempts IS 'Number of failed login attempts, resets on successful login';
COMMENT ON COLUMN public.users.locked_until IS 'Account lock expiration time, NULL if not locked';
COMMENT ON COLUMN public.users.last_login_at IS 'Timestamp of last successful login';
COMMENT ON COLUMN public.users.password_hash IS 'BCrypt hashed password for authentication';