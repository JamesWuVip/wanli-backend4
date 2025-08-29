-- V1.10: 修复classes表结构，添加缺失的字段
-- 这些字段需要与ClassEntity实体类中的定义保持一致

ALTER TABLE classes
ADD COLUMN IF NOT EXISTS class_name VARCHAR(100) NOT NULL DEFAULT '',
ADD COLUMN IF NOT EXISTS class_code VARCHAR(20) NOT NULL DEFAULT '',
ADD COLUMN IF NOT EXISTS grade_level VARCHAR(20) NOT NULL DEFAULT 'GRADE_1',
ADD COLUMN IF NOT EXISTS description VARCHAR(500),
ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT true,
ADD COLUMN IF NOT EXISTS head_teacher_id UUID,
ADD COLUMN IF NOT EXISTS academic_year VARCHAR(10) NOT NULL DEFAULT '2024-2025',
ADD COLUMN IF NOT EXISTS semester VARCHAR(20) NOT NULL DEFAULT 'SPRING';

-- 添加唯一约束（如果不存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_classes_class_code') THEN
        ALTER TABLE classes ADD CONSTRAINT uk_classes_class_code UNIQUE (class_code);
    END IF;
END $$;

-- 添加检查约束（如果不存在）
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_classes_grade_level') THEN
        ALTER TABLE classes ADD CONSTRAINT chk_classes_grade_level 
        CHECK (grade_level IN ('GRADE_1', 'GRADE_2', 'GRADE_3', 'GRADE_4', 'GRADE_5', 'GRADE_6',
                              'GRADE_7', 'GRADE_8', 'GRADE_9', 'GRADE_10', 'GRADE_11', 'GRADE_12'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'chk_classes_semester') THEN
        ALTER TABLE classes ADD CONSTRAINT chk_classes_semester 
        CHECK (semester IN ('SPRING', 'FALL'));
    END IF;
END $$;

-- 添加索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_classes_class_code ON classes(class_code);
CREATE INDEX IF NOT EXISTS idx_classes_grade_level ON classes(grade_level);
CREATE INDEX IF NOT EXISTS idx_classes_academic_year ON classes(academic_year);
CREATE INDEX IF NOT EXISTS idx_classes_semester ON classes(semester);
CREATE INDEX IF NOT EXISTS idx_classes_head_teacher_id ON classes(head_teacher_id);
CREATE INDEX IF NOT EXISTS idx_classes_is_active ON classes(is_active);

-- 添加外键约束（如果head_teacher_id引用users表）
-- ALTER TABLE classes
-- ADD CONSTRAINT IF NOT EXISTS fk_classes_head_teacher_id 
-- FOREIGN KEY (head_teacher_id) REFERENCES users(id);

-- 注释：外键约束被注释掉，因为需要确认users表的结构和数据完整性