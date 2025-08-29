-- V1.9: 修复institutions表id字段类型

-- 删除外键约束
ALTER TABLE institutions DROP CONSTRAINT IF EXISTS fk_institutions_created_by;
ALTER TABLE institutions DROP CONSTRAINT IF EXISTS fk_institutions_updated_by;

-- 删除依赖institutions表的外键约束
ALTER TABLE users DROP CONSTRAINT IF EXISTS fk_users_institution_id;
ALTER TABLE courses DROP CONSTRAINT IF EXISTS fk_courses_institution_id;

-- 修改institutions表的id字段类型为UUID
ALTER TABLE institutions ALTER COLUMN id TYPE UUID USING id::UUID;

-- 修改users表的institution_id字段类型为UUID（如果存在）
ALTER TABLE users ALTER COLUMN institution_id TYPE UUID USING institution_id::UUID;

-- 修改courses表的institution_id字段类型为UUID（如果存在）
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'courses' AND column_name = 'institution_id') THEN
        ALTER TABLE courses ALTER COLUMN institution_id TYPE UUID USING institution_id::UUID;
    END IF;
END $$;

-- 重新添加外键约束
ALTER TABLE institutions 
ADD CONSTRAINT fk_institutions_created_by 
FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE institutions 
ADD CONSTRAINT fk_institutions_updated_by 
FOREIGN KEY (updated_by) REFERENCES users(id);

-- 重新添加users表的institution_id外键约束
ALTER TABLE users 
ADD CONSTRAINT fk_users_institution_id 
FOREIGN KEY (institution_id) REFERENCES institutions(id);

-- 重新添加courses表的institution_id外键约束（如果字段存在）
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'courses' AND column_name = 'institution_id') THEN
        ALTER TABLE courses 
        ADD CONSTRAINT fk_courses_institution_id 
        FOREIGN KEY (institution_id) REFERENCES institutions(id);
    END IF;
END $$;