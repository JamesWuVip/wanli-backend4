-- =====================================================
-- 万里教育后台管理系统 - 数据库初始化脚本
-- 适用于 staging 和 production 环境
-- PostgreSQL 数据库
-- =====================================================

-- 设置基本参数
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- 创建UUID扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;
COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';

SET default_tablespace = '';
SET default_table_access_method = heap;

-- =====================================================
-- 1. 创建表结构
-- =====================================================

-- 创建 franchises 表（加盟商表）
CREATE TABLE IF NOT EXISTS public.franchises (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name character varying(255) NOT NULL,
    status character varying(50) DEFAULT 'ACTIVE'::character varying NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted_at timestamp with time zone,
    CONSTRAINT franchises_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying])::text[])))
);

-- 创建 users 表（用户表）
CREATE TABLE IF NOT EXISTS public.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    franchise_id uuid,
    username character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    role character varying(20) NOT NULL,
    first_name character varying(50),
    last_name character varying(50),
    phone character varying(20),
    avatar_url character varying(500),
    is_active boolean DEFAULT true,
    email_verified boolean DEFAULT false,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    deleted_at timestamp with time zone,
    version bigint DEFAULT 0,
    created_by character varying(255),
    updated_by character varying(255),
    nickname character varying(50),
    status character varying(20) DEFAULT 'ACTIVE'::character varying,
    last_login_at timestamp with time zone,
    full_name character varying(100) NOT NULL,
    locked_until timestamp(6) with time zone,
    login_attempts integer NOT NULL,
    password_hash character varying(255) NOT NULL,
    phone_number character varying(20)
);

COMMENT ON TABLE public.users IS 'Users table - real_name column removed in V1.5, using full_name instead as per SP1 specification';

-- 创建 courses 表（课程表）
CREATE TABLE IF NOT EXISTS public.courses (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    creator_id uuid NOT NULL,
    title character varying(255) NOT NULL,
    description text,
    status character varying(50) DEFAULT 'DRAFT'::character varying NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted_at timestamp with time zone,
    CONSTRAINT courses_status_check CHECK (((status)::text = ANY ((ARRAY['DRAFT'::character varying, 'PUBLISHED'::character varying])::text[])))
);

-- 创建 classes 表（班级表）
CREATE TABLE IF NOT EXISTS public.classes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    franchise_id uuid NOT NULL,
    name character varying(100) NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted_at timestamp with time zone
);

-- 创建 class_members 表（班级成员关联表）
CREATE TABLE IF NOT EXISTS public.class_members (
    class_id uuid NOT NULL,
    user_id uuid NOT NULL
);

-- 创建 lessons 表（课时表）
CREATE TABLE IF NOT EXISTS public.lessons (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    course_id uuid NOT NULL,
    title character varying(255) NOT NULL,
    order_index integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted_at timestamp with time zone
);

-- 创建 homeworks 表（作业表）
CREATE TABLE IF NOT EXISTS public.homeworks (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    lesson_id uuid NOT NULL,
    title character varying(255) NOT NULL,
    description text,
    due_date timestamp with time zone,
    max_score integer DEFAULT 100,
    status character varying(50) DEFAULT 'DRAFT'::character varying NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted_at timestamp with time zone,
    CONSTRAINT homeworks_max_score_check CHECK (((max_score > 0) AND (max_score <= 1000))),
    CONSTRAINT homeworks_status_check CHECK (((status)::text = ANY ((ARRAY['DRAFT'::character varying, 'PUBLISHED'::character varying, 'CLOSED'::character varying])::text[])))
);

-- 创建 questions 表（题目表）
CREATE TABLE IF NOT EXISTS public.questions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    homework_id uuid NOT NULL,
    parent_id uuid,
    question_type character varying(50) NOT NULL,
    content jsonb NOT NULL,
    standard_answer jsonb,
    explanation text,
    video_url character varying(500),
    order_index integer NOT NULL,
    deleted_at timestamp with time zone
);

-- 创建 submissions 表（作业提交表）
CREATE TABLE IF NOT EXISTS public.submissions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    homework_id uuid NOT NULL,
    student_id uuid NOT NULL,
    status character varying(50) DEFAULT 'SUBMITTED'::character varying NOT NULL,
    score integer,
    feedback text,
    submitted_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    graded_at timestamp with time zone,
    grader_id uuid,
    deleted_at timestamp with time zone,
    CONSTRAINT submissions_score_check CHECK ((score >= 0)),
    CONSTRAINT submissions_status_check CHECK (((status)::text = ANY ((ARRAY['SUBMITTED'::character varying, 'GRADED'::character varying])::text[])))
);

-- 创建 student_answers 表（学生答案表）
CREATE TABLE IF NOT EXISTS public.student_answers (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    submission_id uuid NOT NULL,
    question_id uuid NOT NULL,
    answer_content jsonb,
    deleted_at timestamp with time zone
);

-- =====================================================
-- 2. 创建主键约束
-- =====================================================

ALTER TABLE ONLY public.franchises
    ADD CONSTRAINT franchises_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);

ALTER TABLE ONLY public.courses
    ADD CONSTRAINT courses_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.classes
    ADD CONSTRAINT classes_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.class_members
    ADD CONSTRAINT class_members_pkey PRIMARY KEY (class_id, user_id);

ALTER TABLE ONLY public.lessons
    ADD CONSTRAINT lessons_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.homeworks
    ADD CONSTRAINT homeworks_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.questions
    ADD CONSTRAINT questions_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.submissions
    ADD CONSTRAINT submissions_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.student_answers
    ADD CONSTRAINT student_answers_pkey PRIMARY KEY (id);

-- =====================================================
-- 3. 创建索引
-- =====================================================

-- Franchises 索引
CREATE INDEX IF NOT EXISTS idx_franchises_deleted_at ON public.franchises USING btree (deleted_at);
CREATE INDEX IF NOT EXISTS idx_franchises_status ON public.franchises USING btree (status);

-- Users 索引
CREATE INDEX IF NOT EXISTS idx_users_deleted_at ON public.users USING btree (deleted_at);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email ON public.users USING btree (email) WHERE (deleted_at IS NULL);
CREATE INDEX IF NOT EXISTS idx_users_franchise_id ON public.users USING btree (franchise_id);
CREATE INDEX IF NOT EXISTS idx_users_full_name ON public.users USING btree (full_name);
CREATE INDEX IF NOT EXISTS idx_users_role ON public.users USING btree (role);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username ON public.users USING btree (username) WHERE (deleted_at IS NULL);

-- Courses 索引
CREATE INDEX IF NOT EXISTS idx_courses_created_at ON public.courses USING btree (created_at);
CREATE INDEX IF NOT EXISTS idx_courses_creator_id ON public.courses USING btree (creator_id);
CREATE INDEX IF NOT EXISTS idx_courses_status_deleted_at ON public.courses USING btree (status, deleted_at);

-- Classes 索引
CREATE INDEX IF NOT EXISTS idx_classes_deleted_at ON public.classes USING btree (deleted_at);
CREATE INDEX IF NOT EXISTS idx_classes_franchise_id ON public.classes USING btree (franchise_id);

-- Lessons 索引
CREATE INDEX IF NOT EXISTS idx_lessons_course_id ON public.lessons USING btree (course_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_lessons_course_order ON public.lessons USING btree (course_id, order_index) WHERE (deleted_at IS NULL);
CREATE INDEX IF NOT EXISTS idx_lessons_deleted_at ON public.lessons USING btree (deleted_at);

-- Homeworks 索引
CREATE INDEX IF NOT EXISTS idx_homeworks_due_date ON public.homeworks USING btree (due_date);
CREATE INDEX IF NOT EXISTS idx_homeworks_lesson_id ON public.homeworks USING btree (lesson_id);
CREATE INDEX IF NOT EXISTS idx_homeworks_status_deleted_at ON public.homeworks USING btree (status, deleted_at);

-- Questions 索引
CREATE INDEX IF NOT EXISTS idx_questions_deleted_at ON public.questions USING btree (deleted_at);
CREATE INDEX IF NOT EXISTS idx_questions_homework_id ON public.questions USING btree (homework_id);
CREATE INDEX IF NOT EXISTS idx_questions_parent_id ON public.questions USING btree (parent_id);
CREATE INDEX IF NOT EXISTS idx_questions_type ON public.questions USING btree (question_type);

-- Submissions 索引
CREATE INDEX IF NOT EXISTS idx_submissions_grader_id ON public.submissions USING btree (grader_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_submissions_homework_student ON public.submissions USING btree (homework_id, student_id) WHERE (deleted_at IS NULL);
CREATE INDEX IF NOT EXISTS idx_submissions_status ON public.submissions USING btree (status);
CREATE INDEX IF NOT EXISTS idx_submissions_submitted_at ON public.submissions USING btree (submitted_at);

-- Student Answers 索引
CREATE INDEX IF NOT EXISTS idx_student_answers_question_id ON public.student_answers USING btree (question_id);
CREATE INDEX IF NOT EXISTS idx_student_answers_submission_id ON public.student_answers USING btree (submission_id);

-- =====================================================
-- 4. 创建外键约束
-- =====================================================

-- Users 外键
ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_franchise_id_fkey FOREIGN KEY (franchise_id) REFERENCES public.franchises(id) ON DELETE SET NULL;

-- Courses 外键
ALTER TABLE ONLY public.courses
    ADD CONSTRAINT courses_creator_id_fkey FOREIGN KEY (creator_id) REFERENCES public.users(id) ON DELETE RESTRICT;

-- Classes 外键
ALTER TABLE ONLY public.classes
    ADD CONSTRAINT classes_franchise_id_fkey FOREIGN KEY (franchise_id) REFERENCES public.franchises(id) ON DELETE RESTRICT;

-- Class Members 外键
ALTER TABLE ONLY public.class_members
    ADD CONSTRAINT class_members_class_id_fkey FOREIGN KEY (class_id) REFERENCES public.classes(id) ON DELETE CASCADE;

ALTER TABLE ONLY public.class_members
    ADD CONSTRAINT class_members_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;

-- Lessons 外键
ALTER TABLE ONLY public.lessons
    ADD CONSTRAINT lessons_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(id) ON DELETE RESTRICT;

-- Homeworks 外键
ALTER TABLE ONLY public.homeworks
    ADD CONSTRAINT homeworks_lesson_id_fkey FOREIGN KEY (lesson_id) REFERENCES public.lessons(id) ON DELETE RESTRICT;

-- Questions 外键
ALTER TABLE ONLY public.questions
    ADD CONSTRAINT questions_homework_id_fkey FOREIGN KEY (homework_id) REFERENCES public.homeworks(id) ON DELETE RESTRICT;

ALTER TABLE ONLY public.questions
    ADD CONSTRAINT questions_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES public.questions(id) ON DELETE RESTRICT;

-- Submissions 外键
ALTER TABLE ONLY public.submissions
    ADD CONSTRAINT submissions_homework_id_fkey FOREIGN KEY (homework_id) REFERENCES public.homeworks(id) ON DELETE RESTRICT;

ALTER TABLE ONLY public.submissions
    ADD CONSTRAINT submissions_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.users(id) ON DELETE RESTRICT;

ALTER TABLE ONLY public.submissions
    ADD CONSTRAINT submissions_grader_id_fkey FOREIGN KEY (grader_id) REFERENCES public.users(id) ON DELETE RESTRICT;

-- Student Answers 外键
ALTER TABLE ONLY public.student_answers
    ADD CONSTRAINT student_answers_submission_id_fkey FOREIGN KEY (submission_id) REFERENCES public.submissions(id) ON DELETE RESTRICT;

ALTER TABLE ONLY public.student_answers
    ADD CONSTRAINT student_answers_question_id_fkey FOREIGN KEY (question_id) REFERENCES public.questions(id) ON DELETE RESTRICT;

-- =====================================================
-- 5. 插入基础数据（仅用于staging环境测试）
-- =====================================================

-- 注意：以下数据仅用于staging环境测试，production环境请根据实际需求调整

-- 插入测试加盟商（仅staging环境）
INSERT INTO public.franchises (id, name, status, created_at, updated_at) 
VALUES 
    (gen_random_uuid(), '测试加盟商', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- 插入系统管理员用户（仅staging环境）
-- 密码: admin123 (BCrypt加密)
INSERT INTO public.users (id, username, email, role, full_name, password_hash, login_attempts, status, created_at, updated_at)
VALUES 
    (gen_random_uuid(), 'admin', 'admin@wanli.edu', 'ADMIN', '系统管理员', '$2a$10$N.zmdr9k7uOCQb0q8qzvW.L1oB3vJDdRdKClE.KAkxOKfYrYzY0S6', 0, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- 插入测试教师用户（仅staging环境）
-- 密码: teacher123 (BCrypt加密)
INSERT INTO public.users (id, username, email, role, full_name, password_hash, login_attempts, status, created_at, updated_at)
VALUES 
    (gen_random_uuid(), 'teacher', 'teacher@wanli.edu', 'TEACHER', '测试教师', '$2a$10$8K1p/wFkTR46wUBWlkgHQOxZr4QzO5kX6VqjNcJw5n5OOQF5qJQjG', 0, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- 插入测试学生用户（仅staging环境）
-- 密码: student123 (BCrypt加密)
INSERT INTO public.users (id, username, email, role, full_name, password_hash, login_attempts, status, created_at, updated_at)
VALUES 
    (gen_random_uuid(), 'student', 'student@wanli.edu', 'STUDENT', '测试学生', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 0, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- =====================================================
-- 6. 数据库初始化完成
-- =====================================================

-- 输出初始化完成信息
DO $$
BEGIN
    RAISE NOTICE '数据库初始化完成！';
    RAISE NOTICE '已创建以下表：franchises, users, courses, classes, class_members, lessons, homeworks, questions, submissions, student_answers';
    RAISE NOTICE '已创建相关索引和外键约束';
    RAISE NOTICE '已插入基础测试数据（仅staging环境）';
END $$;