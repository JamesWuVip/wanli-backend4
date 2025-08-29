-- 统一franchise和institution的schema迁移脚本
-- 目标：统一使用institutions表，移除franchises表的依赖

BEGIN;

-- 1. 首先备份franchises表数据到institutions表（如果institutions表为空）
-- 注意：institutions表没有uuid字段，需要排除
INSERT INTO institutions (id, name, status, created_at, updated_at, deleted_at)
SELECT id, name, status, created_at, updated_at, deleted_at
FROM franchises
WHERE NOT EXISTS (SELECT 1 FROM institutions WHERE institutions.id = franchises.id);

-- 2. 更新classes表的franchise_id为institution_id
-- 首先添加institution_id列（如果不存在）
ALTER TABLE classes ADD COLUMN IF NOT EXISTS institution_id UUID;

-- 复制franchise_id的值到institution_id
UPDATE classes SET institution_id = franchise_id WHERE franchise_id IS NOT NULL;

-- 3. 删除classes表的franchise_id外键约束
ALTER TABLE classes DROP CONSTRAINT IF EXISTS fk_classes_franchise_id;

-- 4. 删除classes表的franchise_id列
ALTER TABLE classes DROP COLUMN IF EXISTS franchise_id;

-- 5. 为classes表的institution_id添加外键约束
ALTER TABLE classes ADD CONSTRAINT fk_classes_institution_id 
    FOREIGN KEY (institution_id) REFERENCES institutions(id);

-- 6. 删除users表的franchise_id外键约束和列（如果存在）
ALTER TABLE users DROP CONSTRAINT IF EXISTS fk_users_franchise_id;
ALTER TABLE users DROP COLUMN IF EXISTS franchise_id;

-- 7. 为institution_id列添加索引
CREATE INDEX IF NOT EXISTS idx_classes_institution_id ON classes(institution_id);

-- 8. 最后删除franchises表（注意：这会永久删除数据）
-- DROP TABLE IF EXISTS franchises CASCADE;
-- 注释掉删除franchises表的操作，以防数据丢失
-- 可以在确认迁移成功后手动执行

COMMIT;

-- 验证迁移结果的查询
-- SELECT 'classes表结构' as table_name;
-- \d classes;
-- SELECT 'users表结构' as table_name;
-- \d users;
-- SELECT 'institutions表数据' as table_name;
-- SELECT COUNT(*) FROM institutions;