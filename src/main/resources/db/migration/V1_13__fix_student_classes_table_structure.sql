-- V1.13: 修复student_classes表结构与StudentClass实体类的匹配问题
-- 任务: 确保student_classes表结构与StudentClass实体类完全匹配
-- 描述: 添加缺失的deleted_at字段以支持软删除功能

-- 1. 添加deleted_at字段（软删除支持）
ALTER TABLE public.student_classes 
ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP(6) WITH TIME ZONE;

-- 2. 创建deleted_at字段的索引（提高软删除查询性能）
CREATE INDEX IF NOT EXISTS idx_student_classes_deleted_at 
ON public.student_classes(deleted_at);

-- 3. 创建复合索引优化常用查询
CREATE INDEX IF NOT EXISTS idx_student_classes_student_active 
ON public.student_classes(student_id, is_active) 
WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_student_classes_class_active 
ON public.student_classes(class_id, is_active) 
WHERE deleted_at IS NULL;

-- 4. 创建joined_at字段的索引（用于时间范围查询）
CREATE INDEX IF NOT EXISTS idx_student_classes_joined_at 
ON public.student_classes(joined_at);

COMMIT;