-- V1.11: 修复用户表结构与User实体类的匹配问题
-- 任务: 确保users表结构与User实体类完全匹配
-- 描述: 添加缺失字段，修复字段类型不匹配问题

-- 1. 添加缺失的institution_id字段（如果不存在）
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'users' AND column_name = 'institution_id') THEN
        ALTER TABLE public.users ADD COLUMN institution_id UUID;
        
        -- 添加外键约束
        ALTER TABLE public.users 
        ADD CONSTRAINT fk_users_institution_id 
        FOREIGN KEY (institution_id) 
        REFERENCES franchises(id) 
        ON DELETE SET NULL;
        
        -- 添加索引
        CREATE INDEX idx_users_institution_id ON public.users(institution_id);
    END IF;
END $$;

-- 2. 确保role字段存在且有正确的默认值
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'users' AND column_name = 'role') THEN
        ALTER TABLE public.users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'STUDENT';
    END IF;
END $$;

-- 3. 确保所有时间字段使用正确的类型 (timestamp with time zone)
DO $$ 
BEGIN
    -- 检查并修复created_at字段类型
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'users' AND column_name = 'created_at' 
               AND data_type != 'timestamp with time zone') THEN
        ALTER TABLE public.users ALTER COLUMN created_at TYPE timestamp with time zone;
    END IF;
    
    -- 检查并修复updated_at字段类型
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'users' AND column_name = 'updated_at' 
               AND data_type != 'timestamp with time zone') THEN
        ALTER TABLE public.users ALTER COLUMN updated_at TYPE timestamp with time zone;
    END IF;
    
    -- 检查并修复deleted_at字段类型
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'users' AND column_name = 'deleted_at' 
               AND data_type != 'timestamp with time zone') THEN
        ALTER TABLE public.users ALTER COLUMN deleted_at TYPE timestamp with time zone;
    END IF;
END $$;

-- 4. 确保login_attempts字段有正确的默认值
DO $$ 
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'users' AND column_name = 'login_attempts' 
               AND column_default IS NULL) THEN
        ALTER TABLE public.users ALTER COLUMN login_attempts SET DEFAULT 0;
    END IF;
END $$;

-- 5. 确保status字段有正确的默认值
DO $$ 
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'users' AND column_name = 'status' 
               AND column_default != '''ACTIVE''::character varying') THEN
        ALTER TABLE public.users ALTER COLUMN status SET DEFAULT 'ACTIVE';
    END IF;
END $$;

-- 6. 移除不需要的nickname字段（如果存在）
DO $$ 
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'users' AND column_name = 'nickname') THEN
        ALTER TABLE public.users DROP COLUMN nickname;
    END IF;
END $$;

-- 7. 添加检查约束确保数据完整性
DO $$ 
BEGIN
    -- 添加role枚举约束
    IF NOT EXISTS (SELECT 1 FROM information_schema.check_constraints 
                   WHERE constraint_name = 'chk_users_role') THEN
        ALTER TABLE public.users 
        ADD CONSTRAINT chk_users_role 
        CHECK (role IN ('HQ_TEACHER', 'BRANCH_TEACHER', 'STUDENT', 'ADMIN'));
    END IF;
    
    -- 添加status枚举约束
    IF NOT EXISTS (SELECT 1 FROM information_schema.check_constraints 
                   WHERE constraint_name = 'chk_users_status') THEN
        ALTER TABLE public.users 
        ADD CONSTRAINT chk_users_status 
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED', 'DELETED'));
    END IF;
    
    -- 添加login_attempts范围约束
    IF NOT EXISTS (SELECT 1 FROM information_schema.check_constraints 
                   WHERE constraint_name = 'chk_users_login_attempts') THEN
        ALTER TABLE public.users 
        ADD CONSTRAINT chk_users_login_attempts 
        CHECK (login_attempts >= 0 AND login_attempts <= 10);
    END IF;
END $$;

-- 8. 确保所有必要的索引存在
DO $$ 
BEGIN
    -- 检查并创建status索引
    IF NOT EXISTS (SELECT 1 FROM pg_indexes 
                   WHERE tablename = 'users' AND indexname = 'idx_users_status') THEN
        CREATE INDEX idx_users_status ON public.users(status);
    END IF;
    
    -- 检查并创建last_login_at索引
    IF NOT EXISTS (SELECT 1 FROM pg_indexes 
                   WHERE tablename = 'users' AND indexname = 'idx_users_last_login_at') THEN
        CREATE INDEX idx_users_last_login_at ON public.users(last_login_at);
    END IF;
END $$;

-- 9. 更新现有数据确保一致性
DO $$ 
BEGIN
    -- 将旧的TEACHER角色更新为BRANCH_TEACHER
    UPDATE public.users SET role = 'BRANCH_TEACHER' WHERE role = 'TEACHER';
    
    -- 确保所有用户都有有效的role
    UPDATE public.users SET role = 'STUDENT' WHERE role IS NULL;
    
    -- 确保所有用户都有有效的status
    UPDATE public.users SET status = 'ACTIVE' WHERE status IS NULL;
    
    -- 确保所有用户都有有效的login_attempts
    UPDATE public.users SET login_attempts = 0 WHERE login_attempts IS NULL;
END $$;

COMMIT;