-- 为 student_classes 表添加 deleted_at 字段以支持软删除
ALTER TABLE student_classes ADD COLUMN deleted_at TIMESTAMP(6) WITH TIME ZONE;

-- 创建索引以提高查询性能
CREATE INDEX idx_student_classes_deleted_at ON student_classes(deleted_at);