-- V1.12: 修复用户表约束问题
-- 任务: 分步骤添加用户表约束
-- 描述: 先更新数据，再添加约束

-- 1. 确保数据一致性
UPDATE public.users SET role = 'BRANCH_TEACHER' WHERE role = 'TEACHER';
UPDATE public.users SET role = 'STUDENT' WHERE role IS NULL;
UPDATE public.users SET status = 'ACTIVE' WHERE status IS NULL;
UPDATE public.users SET login_attempts = 0 WHERE login_attempts IS NULL;

-- 2. 添加role枚举约束
ALTER TABLE public.users 
ADD CONSTRAINT chk_users_role 
CHECK (role IN ('HQ_TEACHER', 'BRANCH_TEACHER', 'STUDENT', 'ADMIN'));

-- 3. 添加status枚举约束
ALTER TABLE public.users 
ADD CONSTRAINT chk_users_status 
CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED', 'DELETED'));

-- 4. 添加login_attempts范围约束
ALTER TABLE public.users 
ADD CONSTRAINT chk_users_login_attempts 
CHECK (login_attempts >= 0 AND login_attempts <= 10);

-- 5. 创建缺失的索引
CREATE INDEX IF NOT EXISTS idx_users_status ON public.users(status);
CREATE INDEX IF NOT EXISTS idx_users_last_login_at ON public.users(last_login_at);

COMMIT;