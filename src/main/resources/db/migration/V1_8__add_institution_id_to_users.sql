-- V1.8: 为用户表添加机构关联字段
-- 任务: 添加用户与机构的关联关系
-- 描述: 在用户表中添加institution_id字段，建立用户与机构的多对一关系

-- 1. 添加institution_id字段
ALTER TABLE public.users ADD COLUMN institution_id UUID;

-- 2. 添加外键约束
ALTER TABLE public.users 
ADD CONSTRAINT fk_users_institution_id 
FOREIGN KEY (institution_id) 
REFERENCES public.institutions(id) 
ON DELETE SET NULL;

-- 3. 添加索引以优化查询性能
CREATE INDEX IF NOT EXISTS idx_users_institution_id ON public.users(institution_id) WHERE deleted_at IS NULL;

-- 4. 添加字段注释
COMMENT ON COLUMN public.users.institution_id IS 'Reference to the institution this user belongs to';