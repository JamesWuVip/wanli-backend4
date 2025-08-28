-- 万里教育后台管理系统 - Staging环境数据库初始化脚本（修复版本）
-- 包含表结构和测试数据
-- 适用于Railway PostgreSQL数据库

-- 启用UUID扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 设置基本配置
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

-- 清理现有表（如果存在）
DROP TABLE IF EXISTS public.student_answers CASCADE;
DROP TABLE IF EXISTS public.submissions CASCADE;
DROP TABLE IF EXISTS public.questions CASCADE;
DROP TABLE IF EXISTS public.homeworks CASCADE;
DROP TABLE IF EXISTS public.lessons CASCADE;
DROP TABLE IF EXISTS public.class_members CASCADE;
DROP TABLE IF EXISTS public.classes CASCADE;
DROP TABLE IF EXISTS public.courses CASCADE;
DROP TABLE IF EXISTS public.users CASCADE;
DROP TABLE IF EXISTS public.franchises CASCADE;

-- 创建表结构

-- 1. 加盟商表
CREATE TABLE public.franchises (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name character varying(255) NOT NULL,
    status character varying(50) DEFAULT 'ACTIVE'::character varying NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted_at timestamp with time zone,
    CONSTRAINT franchises_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying])::text[])))
);

-- 2. 用户表
CREATE TABLE public.users (
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

-- 3. 课程表
CREATE TABLE public.courses (
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

-- 4. 班级表
CREATE TABLE public.classes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    franchise_id uuid NOT NULL,
    name character varying(100) NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted_at timestamp with time zone
);

-- 5. 班级成员表
CREATE TABLE public.class_members (
    class_id uuid NOT NULL,
    user_id uuid NOT NULL
);

-- 6. 课时表
CREATE TABLE public.lessons (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    course_id uuid NOT NULL,
    title character varying(255) NOT NULL,
    order_index integer DEFAULT 0 NOT NULL,
    created_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted_at timestamp with time zone
);

-- 7. 作业表
CREATE TABLE public.homeworks (
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

-- 8. 题目表
CREATE TABLE public.questions (
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

-- 9. 提交表
CREATE TABLE public.submissions (
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

-- 10. 学生答案表
CREATE TABLE public.student_answers (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    submission_id uuid NOT NULL,
    question_id uuid NOT NULL,
    answer_content jsonb,
    deleted_at timestamp with time zone
);

-- 添加主键约束
ALTER TABLE ONLY public.franchises ADD CONSTRAINT franchises_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.users ADD CONSTRAINT users_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.courses ADD CONSTRAINT courses_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.classes ADD CONSTRAINT classes_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.class_members ADD CONSTRAINT class_members_pkey PRIMARY KEY (class_id, user_id);
ALTER TABLE ONLY public.lessons ADD CONSTRAINT lessons_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.homeworks ADD CONSTRAINT homeworks_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.questions ADD CONSTRAINT questions_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.submissions ADD CONSTRAINT submissions_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.student_answers ADD CONSTRAINT student_answers_pkey PRIMARY KEY (id);

-- 添加唯一约束
ALTER TABLE ONLY public.users ADD CONSTRAINT users_email_key UNIQUE (email);
ALTER TABLE ONLY public.users ADD CONSTRAINT users_username_key UNIQUE (username);

-- 创建索引
CREATE INDEX idx_franchises_status ON public.franchises USING btree (status);
CREATE INDEX idx_franchises_deleted_at ON public.franchises USING btree (deleted_at);
CREATE INDEX idx_users_franchise_id ON public.users USING btree (franchise_id);
CREATE INDEX idx_users_role ON public.users USING btree (role);
CREATE INDEX idx_users_full_name ON public.users USING btree (full_name);
CREATE INDEX idx_users_deleted_at ON public.users USING btree (deleted_at);
CREATE UNIQUE INDEX idx_users_email ON public.users USING btree (email) WHERE (deleted_at IS NULL);
CREATE UNIQUE INDEX idx_users_username ON public.users USING btree (username) WHERE (deleted_at IS NULL);
CREATE INDEX idx_courses_creator_id ON public.courses USING btree (creator_id);
CREATE INDEX idx_courses_status_deleted_at ON public.courses USING btree (status, deleted_at);
CREATE INDEX idx_courses_created_at ON public.courses USING btree (created_at);
CREATE INDEX idx_classes_franchise_id ON public.classes USING btree (franchise_id);
CREATE INDEX idx_classes_deleted_at ON public.classes USING btree (deleted_at);
CREATE INDEX idx_lessons_course_id ON public.lessons USING btree (course_id);
CREATE INDEX idx_lessons_deleted_at ON public.lessons USING btree (deleted_at);
CREATE UNIQUE INDEX idx_lessons_course_order ON public.lessons USING btree (course_id, order_index) WHERE (deleted_at IS NULL);
CREATE INDEX idx_homeworks_lesson_id ON public.homeworks USING btree (lesson_id);
CREATE INDEX idx_homeworks_status_deleted_at ON public.homeworks USING btree (status, deleted_at);
CREATE INDEX idx_homeworks_due_date ON public.homeworks USING btree (due_date);
CREATE INDEX idx_questions_homework_id ON public.questions USING btree (homework_id);
CREATE INDEX idx_questions_parent_id ON public.questions USING btree (parent_id);
CREATE INDEX idx_questions_type ON public.questions USING btree (question_type);
CREATE INDEX idx_questions_deleted_at ON public.questions USING btree (deleted_at);
CREATE INDEX idx_submissions_homework_id ON public.submissions USING btree (homework_id);
CREATE INDEX idx_submissions_student_id ON public.submissions USING btree (student_id);
CREATE INDEX idx_submissions_grader_id ON public.submissions USING btree (grader_id);
CREATE INDEX idx_submissions_status ON public.submissions USING btree (status);
CREATE INDEX idx_submissions_submitted_at ON public.submissions USING btree (submitted_at);
CREATE UNIQUE INDEX idx_submissions_homework_student ON public.submissions USING btree (homework_id, student_id) WHERE (deleted_at IS NULL);
CREATE INDEX idx_student_answers_submission_id ON public.student_answers USING btree (submission_id);
CREATE INDEX idx_student_answers_question_id ON public.student_answers USING btree (question_id);

-- 添加外键约束
ALTER TABLE ONLY public.users ADD CONSTRAINT users_franchise_id_fkey FOREIGN KEY (franchise_id) REFERENCES public.franchises(id) ON DELETE SET NULL;
ALTER TABLE ONLY public.courses ADD CONSTRAINT courses_creator_id_fkey FOREIGN KEY (creator_id) REFERENCES public.users(id) ON DELETE RESTRICT;
ALTER TABLE ONLY public.classes ADD CONSTRAINT classes_franchise_id_fkey FOREIGN KEY (franchise_id) REFERENCES public.franchises(id) ON DELETE RESTRICT;
ALTER TABLE ONLY public.class_members ADD CONSTRAINT class_members_class_id_fkey FOREIGN KEY (class_id) REFERENCES public.classes(id) ON DELETE CASCADE;
ALTER TABLE ONLY public.class_members ADD CONSTRAINT class_members_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;
ALTER TABLE ONLY public.lessons ADD CONSTRAINT lessons_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.courses(id) ON DELETE RESTRICT;
ALTER TABLE ONLY public.homeworks ADD CONSTRAINT homeworks_lesson_id_fkey FOREIGN KEY (lesson_id) REFERENCES public.lessons(id) ON DELETE RESTRICT;
ALTER TABLE ONLY public.questions ADD CONSTRAINT questions_homework_id_fkey FOREIGN KEY (homework_id) REFERENCES public.homeworks(id) ON DELETE RESTRICT;
ALTER TABLE ONLY public.questions ADD CONSTRAINT questions_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES public.questions(id) ON DELETE RESTRICT;
ALTER TABLE ONLY public.submissions ADD CONSTRAINT submissions_homework_id_fkey FOREIGN KEY (homework_id) REFERENCES public.homeworks(id) ON DELETE RESTRICT;
ALTER TABLE ONLY public.submissions ADD CONSTRAINT submissions_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.users(id) ON DELETE RESTRICT;
ALTER TABLE ONLY public.submissions ADD CONSTRAINT submissions_grader_id_fkey FOREIGN KEY (grader_id) REFERENCES public.users(id) ON DELETE RESTRICT;
ALTER TABLE ONLY public.student_answers ADD CONSTRAINT student_answers_submission_id_fkey FOREIGN KEY (submission_id) REFERENCES public.submissions(id) ON DELETE RESTRICT;
ALTER TABLE ONLY public.student_answers ADD CONSTRAINT student_answers_question_id_fkey FOREIGN KEY (question_id) REFERENCES public.questions(id) ON DELETE RESTRICT;

-- 插入测试数据

-- 1. 插入测试加盟商
INSERT INTO public.franchises (id, name, status, created_at, updated_at) VALUES 
('11111111-1111-1111-1111-111111111111', '北京万里教育', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222222', '上海万里教育', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. 插入测试用户
INSERT INTO public.users (id, franchise_id, username, email, role, full_name, password_hash, phone_number, login_attempts, status, created_at, updated_at) VALUES 
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'admin', 'admin@wanli.edu', 'ADMIN', '系统管理员', '$2a$10$afuI2LP7SGzDOBVhglM/7OUhgHqkpPwmnucuqfJsg1sqXsn6dJQgy', '13800138000', 0, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111', 'teacher1', 'teacher1@wanli.edu', 'TEACHER', '张老师', '$2a$10$afuI2LP7SGzDOBVhglM/7OUhgHqkpPwmnucuqfJsg1sqXsn6dJQgy', '13800138001', 0, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('cccccccc-cccc-cccc-cccc-cccccccccccc', '11111111-1111-1111-1111-111111111111', 'student1', 'student1@example.com', 'STUDENT', '李同学', '$2a$10$afuI2LP7SGzDOBVhglM/7OUhgHqkpPwmnucuqfJsg1sqXsn6dJQgy', '13800138002', 0, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. 插入测试课程
INSERT INTO public.courses (id, creator_id, title, description, status, created_at, updated_at) VALUES 
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '数学基础课程', '小学数学基础知识讲解', 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 4. 插入测试班级
INSERT INTO public.classes (id, franchise_id, name, created_at, updated_at) VALUES 
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '11111111-1111-1111-1111-111111111111', '一年级A班', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 5. 插入班级成员
INSERT INTO public.class_members (class_id, user_id) VALUES 
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'cccccccc-cccc-cccc-cccc-cccccccccccc');

-- 6. 插入测试课时
INSERT INTO public.lessons (id, course_id, title, order_index, created_at, updated_at) VALUES 
('ffffffff-ffff-ffff-ffff-ffffffffffff', 'dddddddd-dddd-dddd-dddd-dddddddddddd', '第一课：数字认识', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 数据库初始化完成
-- 测试账户信息：
-- 管理员: admin / password (密码已加密)
-- 教师: teacher1 / password (密码已加密)
-- 学生: student1 / password (密码已加密)