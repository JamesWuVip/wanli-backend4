--
-- PostgreSQL database dump
--

-- Dumped from database version 14.18 (Homebrew)
-- Dumped by pg_dump version 14.18 (Homebrew)

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

--
-- Data for Name: franchises; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.franchises (id, name, status, created_at, updated_at, deleted_at) FROM stdin;
\.


--
-- Data for Name: classes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.classes (id, franchise_id, name, created_at, updated_at, deleted_at) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (id, franchise_id, username, email, role, first_name, last_name, phone, avatar_url, is_active, email_verified, created_at, updated_at, deleted_at, version, created_by, updated_by, nickname, status, last_login_at, full_name, locked_until, login_attempts, password_hash, phone_number) FROM stdin;
2f76b95b-3924-47bc-a1f4-5c89011e752e	\N	testuser2025	testuser2025@example.com	STUDENT	\N	\N	\N	\N	t	f	2025-08-28 01:01:20.616057+08	2025-08-28 01:01:59.700658+08	\N	0	\N	\N	\N	ACTIVE	2025-08-28 01:01:59.700397+08	Test User 2025	\N	0	$2a$10$afuI2LP7SGzDOBVhglM/7OUhgHqkpPwmnucuqfJsg1sqXsn6dJQgy	13812345678
\.


--
-- Data for Name: class_members; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.class_members (class_id, user_id) FROM stdin;
\.


--
-- Data for Name: courses; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.courses (id, creator_id, title, description, status, created_at, updated_at, deleted_at) FROM stdin;
\.


--
-- Data for Name: lessons; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.lessons (id, course_id, title, order_index, created_at, updated_at, deleted_at) FROM stdin;
\.


--
-- Data for Name: homeworks; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.homeworks (id, lesson_id, title, description, due_date, max_score, status, created_at, updated_at, deleted_at) FROM stdin;
\.


--
-- Data for Name: questions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.questions (id, homework_id, parent_id, question_type, content, standard_answer, explanation, video_url, order_index, deleted_at) FROM stdin;
\.


--
-- Data for Name: submissions; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.submissions (id, homework_id, student_id, status, score, feedback, submitted_at, graded_at, grader_id, deleted_at) FROM stdin;
\.


--
-- Data for Name: student_answers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.student_answers (id, submission_id, question_id, answer_content, deleted_at) FROM stdin;
\.


--
-- PostgreSQL database dump complete
--

