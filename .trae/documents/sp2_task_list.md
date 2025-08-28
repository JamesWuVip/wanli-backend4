# SP2 敏捷开发任务清单

## 1. Sprint 概览

### 1.1 Sprint 目标
- 实现极简用户认证系统（注册、登录、JWT认证）
- 完善基础用户信息管理功能
- 开发教育机构管理核心功能
- 实现课程管理系统
- 构建学员管理功能
- 专注业务价值交付，快速迭代

### 1.2 时间规划
- **Sprint 周期**: 2周 (10个工作日)
- **开始日期**: 2024年2月1日
- **结束日期**: 2024年2月14日
- **Sprint Planning**: 2024年2月1日 09:00-10:00
- **Daily Standup**: 每日 09:30-09:45
- **Sprint Review**: 2024年2月13日 14:00-15:00
- **Sprint Retrospective**: 2024年2月14日 15:00-16:00

### 1.2.1 里程碑
- **Day 2 (2024年2月2日)**: 极简认证系统完成
- **Day 4 (2024年2月6日)**: 教育机构管理功能完成
- **Day 6 (2024年2月8日)**: 课程管理系统完成
- **Day 8 (2024年2月12日)**: 学员管理功能完成
- **Day 10 (2024年2月14日)**: 所有功能测试和文档完成

### 1.3 团队容量
- **开发人员**: 1人 (全栈开发)
- **测试人员**: 开发人员兼任
- **预估总工时**: 85人时
- **认证功能**: 20小时 (23.5%)
- **业务功能**: 60小时 (70.6%)
- **测试文档**: 5小时 (5.9%)

## 2. 用户故事 (User Stories)

### 2.1 高优先级用户故事

#### US-005: 极简用户认证
**作为** 系统用户  
**我希望** 能够注册和登录系统  
**以便于** 访问教育管理功能  

**验收标准**:
- [ ] 用户可以通过邮箱注册账号（无需邮箱验证）
- [ ] 用户可以通过邮箱密码登录
- [ ] 系统使用JWT进行身份认证
- [ ] 用户可以查看和更新基本信息
- [ ] 登录状态持久化管理
- [ ] 基础的登录状态验证（已登录/未登录）

#### US-006: 教育机构管理
**作为** 系统管理员  
**我希望** 能够管理教育机构信息  
**以便于** 建立机构档案和层级关系  

**验收标准**:
- [ ] 创建、查看、编辑、删除机构信息
- [ ] 支持机构层级关系管理
- [ ] 机构基本信息包含名称、地址、联系方式
- [ ] 支持机构状态管理（启用/禁用）
- [ ] 机构信息分页查询和搜索

#### US-007: 课程管理系统
**作为** 教育机构管理员  
**我希望** 能够管理课程信息  
**以便于** 组织教学活动  

**验收标准**:
- [ ] 创建、查看、编辑、删除课程
- [ ] 课程信息包含名称、描述、价格、时长
- [ ] 支持课程分类和标签管理
- [ ] 课程状态管理（草稿、发布、下架）
- [ ] 课程信息分页查询和筛选

### 2.2 中优先级用户故事

#### US-008: 学员管理功能
**作为** 教育机构管理员  
**我希望** 能够管理学员信息  
**以便于** 跟踪学员学习情况  

**验收标准**:
- [ ] 创建、查看、编辑学员档案
- [ ] 学员信息包含姓名、联系方式、所属机构
- [ ] 支持学员课程报名管理
- [ ] 学员学习进度跟踪
- [ ] 学员信息分页查询和搜索

#### US-009: 用户信息管理
**作为** 系统用户  
**我希望** 能够管理个人信息  
**以便于** 维护账户资料  

**验收标准**:
- [ ] 查看个人基本信息
- [ ] 更新个人资料（姓名、手机等）
- [ ] 修改登录密码（简单验证）
- [ ] 查看账户创建时间
- [ ] 基础的身份验证（当前密码确认）

### 2.3 低优先级用户故事

#### US-010: 数据统计查看
**作为** 系统管理员  
**我希望** 能够查看基础数据统计  
**以便于** 了解系统使用情况  

**验收标准**:
- [ ] 查看用户注册统计
- [ ] 查看机构数量统计
- [ ] 查看课程创建统计
- [ ] 查看学员报名统计
- [ ] 基础数据图表展示

## 3. 任务分解 (Task Breakdown)

### 3.1 极简认证模块任务

#### T-012: 用户认证数据库优化
- **描述**: 优化用户表结构，支持基础JWT认证
- **估时**: 2小时
- **优先级**: 高
- **依赖**: 无
- **验收标准**:
  - [ ] 优化users表结构（保持简单）
  - [ ] 添加必要索引
  - [ ] 移除复杂的用户状态字段
  - [ ] 数据库迁移脚本

#### T-013: JWT认证服务
- **描述**: 实现JWT token生成和验证
- **估时**: 6小时
- **优先级**: 高
- **依赖**: T-012
- **验收标准**:
  - [ ] JWT工具类实现
  - [ ] Token生成和解析
  - [ ] Token过期处理
  - [ ] 安全配置

#### T-014: 用户注册登录接口
- **描述**: 实现用户注册和登录功能
- **估时**: 8小时
- **优先级**: 高
- **依赖**: T-013
- **验收标准**:
  - [ ] 用户注册接口
  - [ ] 用户登录接口
  - [ ] 密码加密存储
  - [ ] 输入参数验证

#### T-015: 用户信息管理
- **描述**: 实现用户基本信息管理
- **估时**: 4小时
- **优先级**: 高
- **依赖**: T-014
- **验收标准**:
  - [ ] 获取用户信息接口
  - [ ] 更新用户信息接口
  - [ ] 修改密码接口（简单验证）
  - [ ] 基础登录状态验证中间件

### 3.2 教育机构管理任务

#### T-016: 机构数据模型设计
- **描述**: 设计教育机构相关数据表
- **估时**: 3小时
- **优先级**: 高
- **依赖**: 无
- **验收标准**:
  - [ ] 机构表结构设计
  - [ ] 机构层级关系设计
  - [ ] 创建必要索引
  - [ ] 数据库迁移脚本

#### T-017: 机构管理服务层
- **描述**: 实现机构管理业务逻辑
- **估时**: 10小时
- **优先级**: 高
- **依赖**: T-016
- **验收标准**:
  - [ ] 机构CRUD操作
  - [ ] 机构层级管理
  - [ ] 机构状态管理
  - [ ] 机构查询和搜索
  - [ ] 数据验证和异常处理

### 3.3 课程管理模块任务

#### T-018: 课程数据模型设计
- **描述**: 设计课程管理相关数据表
- **估时**: 4小时
- **优先级**: 高
- **依赖**: T-016
- **验收标准**:
  - [ ] 课程表结构设计
  - [ ] 课程分类表设计
  - [ ] 课程标签关联设计
  - [ ] 创建必要索引

#### T-019: 课程管理服务层
- **描述**: 实现课程管理业务逻辑
- **估时**: 12小时
- **优先级**: 高
- **依赖**: T-018
- **验收标准**:
  - [ ] 课程CRUD操作
  - [ ] 课程分类管理
  - [ ] 课程状态管理
  - [ ] 课程查询和筛选
  - [ ] 课程数据验证

### 3.4 学员管理模块任务

#### T-020: 学员数据模型设计
- **描述**: 设计学员管理相关数据表
- **估时**: 3小时
- **优先级**: 中
- **依赖**: T-016, T-018
- **验收标准**:
  - [ ] 学员表结构设计
  - [ ] 学员课程关联表设计
  - [ ] 学员机构关联设计
  - [ ] 创建必要索引

#### T-021: 学员管理服务层
- **描述**: 实现学员管理业务逻辑
- **估时**: 10小时
- **优先级**: 中
- **依赖**: T-020
- **验收标准**:
  - [ ] 学员CRUD操作
  - [ ] 学员课程报名管理
  - [ ] 学员信息查询和搜索
  - [ ] 学员学习进度跟踪
  - [ ] 数据验证和异常处理

### 3.5 API接口开发任务

#### T-022: 机构管理API接口
- **描述**: 实现机构管理相关API接口
- **估时**: 8小时
- **优先级**: 高
- **依赖**: T-017
- **验收标准**:
  - [ ] 机构CRUD接口
  - [ ] 机构查询和搜索接口
  - [ ] 机构层级管理接口
  - [ ] 接口参数验证
  - [ ] 统一异常处理

#### T-023: 课程管理API接口
- **描述**: 实现课程管理相关API接口
- **估时**: 10小时
- **优先级**: 高
- **依赖**: T-019
- **验收标准**:
  - [ ] 课程CRUD接口
  - [ ] 课程查询和筛选接口
  - [ ] 课程分类管理接口
  - [ ] 接口权限控制
  - [ ] 响应数据格式化

#### T-024: 学员管理API接口
- **描述**: 实现学员管理相关API接口
- **估时**: 8小时
- **优先级**: 中
- **依赖**: T-021
- **验收标准**:
  - [ ] 学员CRUD接口
  - [ ] 学员查询和搜索接口
  - [ ] 学员课程报名接口
  - [ ] 学员进度查询接口
  - [ ] 接口数据验证

### 3.6 测试任务

#### T-025: 单元测试
- **描述**: 编写SP2功能单元测试
- **估时**: 3小时
- **优先级**: 中
- **依赖**: T-015, T-017, T-019, T-021
- **验收标准**:
  - [ ] 用户认证服务测试
  - [ ] 机构管理服务测试
  - [ ] 课程管理服务测试
  - [ ] 学员管理服务测试
  - [ ] 测试覆盖率达到80%以上

#### T-026: API接口测试
- **描述**: 编写API接口集成测试
- **估时**: 2小时
- **优先级**: 低
- **依赖**: T-022, T-023, T-024
- **验收标准**:
  - [ ] 认证接口测试
  - [ ] 机构管理接口测试
  - [ ] 课程管理接口测试
  - [ ] 学员管理接口测试
  - [ ] 接口响应格式验证

### 3.7 文档任务

#### T-027: API文档更新
- **描述**: 更新API接口文档
- **估时**: 1小时
- **优先级**: 低
- **依赖**: T-022, T-023, T-024
- **验收标准**:
  - [ ] 认证接口文档
  - [ ] 机构管理接口文档
  - [ ] 课程管理接口文档
  - [ ] 学员管理接口文档

## 4. 优先级排序

### 4.1 Must Have (必须有)
- T-012: 用户认证数据库优化
- T-013: JWT认证服务
- T-014: 用户注册登录接口
- T-015: 用户信息管理
- T-016: 机构数据模型设计
- T-017: 机构管理服务层
- T-018: 课程数据模型设计
- T-019: 课程管理服务层
- T-022: 机构管理API接口
- T-023: 课程管理API接口

### 4.2 Should Have (应该有)
- T-020: 学员数据模型设计
- T-021: 学员管理服务层
- T-024: 学员管理API接口
- T-025: 单元测试

### 4.3 Could Have (可以有)
- T-026: API接口测试
- T-027: API文档更新

### 4.4 Won't Have (暂不实现)
- 复杂RBAC权限管理系统
- 邮箱验证功能
- 密码重置功能
- 高级用户状态管理
- 操作日志记录系统
- 多因子认证
- 权限角色管理
- 用户组管理
- 细粒度权限控制

## 5. 风险评估

### 5.1 技术风险

#### 风险1: JWT认证安全性
- **概率**: 低
- **影响**: 中
- **缓解措施**: 
  - 使用成熟的JWT库
  - 设置合理的token过期时间
  - 实施token刷新机制

#### 风险2: 数据库设计变更
- **概率**: 中
- **影响**: 中
- **缓解措施**:
  - 详细的数据库设计评审
  - 使用数据库迁移脚本
  - 保持向后兼容性

#### 风险3: 业务逻辑复杂度
- **概率**: 低
- **影响**: 低
- **缓解措施**:
  - 采用简单直观的业务模型
  - 分模块独立开发
  - 充分的单元测试覆盖

### 5.2 进度风险

#### 风险4: 业务功能开发时间超预期
- **概率**: 中
- **影响**: 中
- **缓解措施**:
  - 优先实现核心CRUD功能
  - 简化业务逻辑设计
  - 并行开发不同模块

#### 风险5: 接口联调时间不足
- **概率**: 低
- **影响**: 低
- **缓解措施**:
  - 提前定义接口规范
  - 使用Mock数据测试
  - 分阶段集成测试

## 6. 团队分工建议

### 6.1 全栈开发工程师 (主要角色)
- **负责人**: 项目开发者
- **主要职责**: 
  - 用户认证系统设计和实现
  - 教育机构管理功能开发
  - 课程管理系统开发
  - 学员管理功能开发
  - 数据库设计和优化
  - 单元测试和接口测试
- **负责任务**: T-012~T-027
- **工作时间分配**:
  - Week 1 (40小时): 用户认证系统、教育机构管理功能
  - Week 2 (40小时): 课程管理系统、学员管理功能、测试文档

### 6.2 质量保证 (兼任角色)
- **负责人**: 开发工程师兼任
- **主要职责**:
  - 功能测试和回归测试
  - 安全性测试
  - 性能测试
  - API接口端到端测试
- **质量标准**:
  - 单元测试覆盖率 ≥ 80%
  - 所有认证功能正常
  - API接口响应成功率 ≥ 99%
  - 业务功能流程稳定可靠

### 6.3 运维部署 (兼任角色)
- **负责人**: 开发工程师兼任
- **主要职责**:
  - JWT服务配置和监控
  - 数据库表结构初始化
  - API日志配置
  - 基础安全配置和监控
- **部署计划**:
  - 开发环境: 本地JWT测试配置
  - 测试环境: Railway + 测试JWT配置
  - 生产环境: Railway + 生产JWT配置

## 7. Definition of Done (完成标准)

### 7.1 代码标准
- [ ] 代码通过静态分析检查
- [ ] 代码符合团队编码规范
- [ ] 关键业务逻辑有详细注释
- [ ] 敏感信息不在代码中硬编码
- [ ] 基础认证逻辑经过安全审查

### 7.2 测试标准
- [ ] 单元测试通过，覆盖率≥80%
- [ ] 集成测试通过
- [ ] 基础用户认证功能测试通过
- [ ] 教育业务功能测试通过
- [ ] API接口测试通过
- [ ] 基础安全性测试通过

### 7.3 文档标准
- [ ] API文档更新完整
- [ ] 基础认证配置说明文档
- [ ] 教育业务功能说明文档
- [ ] 简化的部署和运维文档

### 7.4 部署标准
- [ ] 在测试环境部署成功
- [ ] JWT服务配置正确
- [ ] 基础认证功能正常
- [ ] 性能指标满足要求
- [ ] 基础安全配置检查通过

## 8. Sprint 交付物

### 8.1 功能交付
- 极简用户认证系统（注册、登录、JWT）
- 教育机构管理功能
- 课程管理系统
- 学员管理功能
- 基础用户信息管理

### 8.2 技术交付
- 教育业务数据模型设计
- 极简JWT认证服务集成
- 基础安全机制（最小化）
- 完整的API接口和服务层
- 基础测试套件

### 8.3 质量指标
- 代码覆盖率 ≥ 80%
- 接口响应时间 < 300ms
- API接口成功率 ≥ 99%
- 零严重安全漏洞
- 文档完整性 100%

## 9. 后续Sprint规划预览

### SP3 计划功能
- 课程内容管理（课时、作业、考试）
- 学员学习进度跟踪
- 成绩管理系统
- 班级管理功能
- 教师管理功能

### SP4 计划功能
- 高级权限管理系统（如需要）
- 邮箱验证功能（如需要）
- 密码重置功能（如需要）
- 数据统计和报表
- 系统监控和告警

### SP5+ 计划功能
- 多因子认证(MFA)
- OAuth第三方登录
- 操作日志记录系统
- 高级搜索和筛选
- 移动端支持

## 10. 数据库设计补充

### 10.1 教育机构表

```sql
-- 教育机构表
CREATE TABLE institutions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    address VARCHAR(200),
    phone VARCHAR(20),
    email VARCHAR(100),
    website VARCHAR(200),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 课程表
CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    institution_id BIGINT NOT NULL REFERENCES institutions(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    duration_hours INTEGER,
    price DECIMAL(10,2),
    max_students INTEGER,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'DRAFT')),
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 学员表
CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    birth_date DATE,
    gender VARCHAR(10) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    address VARCHAR(200),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'GRADUATED')),
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 10.2 课程学员关联表

```sql
-- 课程学员关联表（选课记录）
CREATE TABLE course_enrollments (
    id BIGSERIAL PRIMARY KEY,
    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    student_id BIGINT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    enrollment_date DATE DEFAULT CURRENT_DATE,
    status VARCHAR(20) DEFAULT 'ENROLLED' CHECK (status IN ('ENROLLED', 'COMPLETED', 'DROPPED', 'SUSPENDED')),
    grade VARCHAR(10),
    notes TEXT,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(course_id, student_id)
);

-- 创建索引
CREATE INDEX idx_institutions_status ON institutions(status);
CREATE INDEX idx_institutions_created_at ON institutions(created_at DESC);
CREATE INDEX idx_courses_institution_id ON courses(institution_id);
CREATE INDEX idx_courses_status ON courses(status);
CREATE INDEX idx_courses_created_at ON courses(created_at DESC);
CREATE INDEX idx_students_email ON students(email);
CREATE INDEX idx_students_status ON students(status);
CREATE INDEX idx_students_created_at ON students(created_at DESC);
CREATE INDEX idx_course_enrollments_course_id ON course_enrollments(course_id);
CREATE INDEX idx_course_enrollments_student_id ON course_enrollments(student_id);
CREATE INDEX idx_course_enrollments_status ON course_enrollments(status);
```

### 10.3 初始化数据

```sql
-- 插入示例教育机构
INSERT INTO institutions (name, description, address, phone, email, website) VALUES
('阳光教育培训中心', '专业的K12教育培训机构', '北京市朝阳区教育路123号', '010-12345678', 'contact@sunshine-edu.com', 'https://www.sunshine-edu.com'),
('未来科技学院', '专注于IT技能培训', '上海市浦东新区科技园区456号', '021-87654321', 'info@future-tech.com', 'https://www.future-tech.com');

-- 插入示例课程
INSERT INTO courses (institution_id, name, description, duration_hours, price, max_students) VALUES
(1, '小学数学基础班', '针对小学1-3年级的数学基础课程', 40, 1200.00, 20),
(1, '初中英语提高班', '初中英语语法和阅读理解提升', 60, 1800.00, 15),
(2, 'Java编程入门', 'Java基础语法和面向对象编程', 80, 2500.00, 25),
(2, 'Python数据分析', 'Python在数据分析领域的应用', 60, 2200.00, 20);

-- 插入示例学员
INSERT INTO students (name, email, phone, birth_date, gender) VALUES
('张小明', 'zhangxiaoming@example.com', '13800138001', '2010-05-15', 'MALE'),
('李小红', 'lixiaohong@example.com', '13800138002', '2009-08-20', 'FEMALE'),
('王大力', 'wangdali@example.com', '13800138003', '1995-03-10', 'MALE'),
('赵美丽', 'zhaomeili@example.com', '13800138004', '1998-12-05', 'FEMALE');
```

---

**文档版本**: v1.0  
**创建日期**: 2024年1月29日  
**最后更新**: 2024年1月29日  
**负责人**: 项目开发团队  
**依赖Sprint**: SP1 (用户基础功能模块)