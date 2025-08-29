-- 统一franchise和institution schema
-- 删除重复的字段和约束，统一使用institutions表

-- 1. 删除users表中的franchise_id字段和相关约束
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_franchise_id_fkey;
DROP INDEX IF EXISTS idx_users_franchise_id;
ALTER TABLE users DROP COLUMN IF EXISTS franchise_id;

-- 2. 修改classes表，将franchise_id改为institution_id
-- 首先添加新的institution_id字段
ALTER TABLE classes ADD COLUMN IF NOT EXISTS institution_id UUID;

-- 将franchise_id的数据迁移到institution_id（如果franchises表中有对应的institutions记录）
-- 这里假设franchise和institution是一对一关系，需要根据实际情况调整
UPDATE classes SET institution_id = (
    SELECT i.id FROM institutions i 
    JOIN franchises f ON f.name = i.name 
    WHERE f.id = classes.franchise_id
) WHERE franchise_id IS NOT NULL;

-- 删除旧的franchise_id字段和约束
ALTER TABLE classes DROP CONSTRAINT IF EXISTS classes_franchise_id_fkey;
DROP INDEX IF EXISTS idx_classes_franchise_id;
ALTER TABLE classes DROP COLUMN IF EXISTS franchise_id;

-- 添加新的外键约束和索引
ALTER TABLE classes ADD CONSTRAINT fk_classes_institution_id 
    FOREIGN KEY (institution_id) REFERENCES institutions(id) ON DELETE RESTRICT;
CREATE INDEX IF NOT EXISTS idx_classes_institution_id ON classes(institution_id);

-- 3. 删除不再使用的franchises表（如果确认不需要）
-- 注意：这会删除所有franchise数据，请确保已经迁移到institutions表
-- DROP TABLE IF EXISTS franchises CASCADE;

-- 4. 确保institutions表有必要的索引
CREATE INDEX IF NOT EXISTS idx_institutions_name ON institutions(name);
CREATE INDEX IF NOT EXISTS idx_institutions_status ON institutions(status);
CREATE INDEX IF NOT EXISTS idx_institutions_deleted_at ON institutions(deleted_at);