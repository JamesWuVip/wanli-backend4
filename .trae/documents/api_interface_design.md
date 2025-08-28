# API接口文档 - 完整版

## 文档信息

* **文档名称**: API接口文档 - 完整版

* **版本**: v2.0.0

* **创建日期**: 2025-01-17

* **最后更新**: 2025-01-28

* **适用阶段**: SP1-SP2（完整功能实现阶段）

* **文档级别**: P0（核心文档）

## 1. 概述

### 1.1 文档目的

本文档详细定义了万里后端系统的完整API接口规范，包括用户认证、教育机构管理、课程管理、学员管理等所有功能模块的RESTful API接口设计。

### 1.2 适用范围

* 后端API开发团队

* 前端开发团队

* 测试团队

* 系统集成团队

### 1.3 技术栈

* **框架**: Spring Boot 3.5

* **安全**: Spring Security 6.x + JWT

* **数据库**: PostgreSQL 15

* **缓存**: Redis 7

* **文档**: OpenAPI 3.0 (Swagger)

### 1.4 基础信息

**环境配置：**

| 环境   | 基础URL                                       | 描述                    |
| ---- | ------------------------------------------- | --------------------- |
| 开发环境 | `http://localhost:8080`                     | 本地开发环境                |
| 测试环境 | `https://wanli-backend-staging.railway.app` | Railway Staging 环境    |
| 生产环境 | `https://wanli-backend.railway.app`         | Railway Production 环境 |

**通用配置：**

* **API版本**: v1.0.0

* **认证方式**: JWT Bearer Token

* **Content-Type**: `application/json`

* **字符编码**: UTF-8

## 2. API设计原则

### 2.1 RESTful设计原则

* 使用HTTP动词表示操作：GET（查询）、POST（创建）、PUT（更新）、DELETE（删除）

* 使用名词表示资源：`/api/courses`、`/api/lessons`、`/api/institutions`

* 使用HTTP状态码表示结果：200（成功）、400（客户端错误）、500（服务器错误）

* 支持资源的层次化：`/api/courses/{courseId}/lessons`

### 2.2 HTTP状态码

* `200 OK`: 请求成功

* `201 Created`: 资源创建成功

* `400 Bad Request`: 请求参数错误

* `401 Unauthorized`: 未授权

* `403 Forbidden`: 权限不足

* `404 Not Found`: 资源不存在

* `409 Conflict`: 资源冲突

* `429 Too Many Requests`: 请求频率过高

* `500 Internal Server Error`: 服务器内部错误

### 2.3 统一响应格式

#### 成功响应

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2025-01-17T10:30:00Z",
  "requestId": "req-123456789"
}
```

#### 错误响应

```json
{
  "success": false,
  "code": 400,
  "message": "请求参数错误",
  "errorCode": "BUSINESS_ERROR_001",
  "errors": [
    {
      "field": "name",
      "message": "名称不能为空"
    }
  ],
  "timestamp": "2025-01-17T10:30:00Z",
  "requestId": "req-123456789"
}
```

#### 分页响应格式

```json
{
  "success": true,
  "code": 200,
  "message": "查询成功",
  "data": {
    "content": [],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false
  },
  "timestamp": "2025-01-17T10:30:00Z"
}
```

### 2.4 通用查询参数

* `page`: 页码，从0开始，默认0

* `size`: 每页大小，默认20，最大100

* `sort`: 排序字段，格式：`field,direction`，如：`createdAt,desc`

* `search`: 搜索关键词

## 3. 认证与授权

### 3.1 JWT认证机制

* **认证方式**: Bearer Token

* **Token位置**: HTTP Header `Authorization: Bearer <token>`

* **Token有效期**: 24小时（开发环境1小时）

* **刷新机制**: 自动刷新（Token过期前30分钟）

### 3.2 权限控制

* **ROLE\_HQ\_TEACHER**: 总部教师角色，拥有所有课程和课时的管理权限

* **ROLE\_ADMIN**: 管理员角色，拥有系统管理权限

* **权限验证**: 基于Spring Security的方法级权限控制

## 4. 用户认证API

### 4.1 用户注册

**接口信息**

* **URL**: `POST /api/auth/register`

* **描述**: 用户注册接口

* **权限**: 无需认证

**请求参数**

```json
{
  "username": "teacher001",
  "password": "SecurePass123!",
  "confirmPassword": "SecurePass123!",
  "email": "teacher001@wanli.edu",
  "fullName": "张老师",
  "phoneNumber": "13800138000"
}
```

| 参数名             | 类型     | 必填 | 描述   | 验证规则                 |
| --------------- | ------ | -- | ---- | -------------------- |
| username        | String | 是  | 用户名  | 4-20字符，字母数字下划线       |
| password        | String | 是  | 密码   | 8-50字符，包含大小写字母数字特殊字符 |
| confirmPassword | String | 是  | 确认密码 | 必须与password一致        |
| email           | String | 是  | 邮箱   | 有效邮箱格式               |
| fullName        | String | 是  | 姓名   | 2-20字符               |
| phoneNumber     | String | 否  | 手机号  | 11位数字                |

**响应示例**

```json
{
  "success": true,
  "code": 201,
  "message": "用户注册成功",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "teacher001",
    "email": "teacher001@wanli.edu",
    "fullName": "张老师",
    "role": "ROLE_HQ_TEACHER",
    "createdAt": "2025-01-15T10:30:00Z"
  },
  "timestamp": "2025-01-15T10:30:00Z",
  "requestId": "req-123456789"
}
```

**cURL示例**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teacher001",
    "password": "SecurePass123!",
    "confirmPassword": "SecurePass123!",
    "email": "teacher001@wanli.edu",
    "fullName": "张老师",
    "phoneNumber": "13800138000"
  }'
```

### 4.2 用户登录

**接口信息**

* **URL**: `POST /api/auth/login`

* **描述**: 用户登录接口

* **权限**: 无需认证

**请求参数**

```json
{
  "username": "teacher001",
  "password": "SecurePass123!"
}
```

| 参数名      | 类型     | 必填 | 描述     |
| -------- | ------ | -- | ------ |
| username | String | 是  | 用户名或邮箱 |
| password | String | 是  | 密码     |

**响应示例**

```json
{
  "success": true,
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "username": "teacher001",
      "email": "teacher001@wanli.edu",
      "fullName": "张老师",
      "role": "ROLE_HQ_TEACHER",
      "lastLoginAt": "2025-01-15T10:30:00Z"
    }
  },
  "timestamp": "2025-01-15T10:30:00Z",
  "requestId": "req-123456789"
}
```

**cURL示例**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teacher001",
    "password": "SecurePass123!"
  }'
```

### 4.3 获取用户信息

**接口信息**

* **URL**: `GET /api/auth/me`

* **描述**: 获取当前登录用户信息

* **权限**: 需要认证

**请求头**

```
Authorization: Bearer {token}
```

**响应示例**

```json
{
  "success": true,
  "code": 200,
  "message": "获取用户信息成功",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "teacher001",
    "email": "teacher001@wanli.edu",
    "fullName": "张老师",
    "phoneNumber": "13800138000",
    "role": "ROLE_HQ_TEACHER",
    "createdAt": "2025-01-15T10:30:00Z",
    "lastLoginAt": "2025-01-15T10:30:00Z",
    "isActive": true
  },
  "timestamp": "2025-01-15T10:30:00Z",
  "requestId": "req-123456789"
}
```

**cURL示例**

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 4.4 用户登出

**接口信息**

* **URL**: `POST /api/auth/logout`

* **描述**: 用户登出接口（将Token加入黑名单）

* **权限**: 需要认证

**请求头**

```
Authorization: Bearer {token}
```

**响应示例**

```json
{
  "success": true,
  "code": 200,
  "message": "登出成功",
  "data": null,
  "timestamp": "2025-01-15T10:30:00Z",
  "requestId": "req-123456789"
}
```

**cURL示例**

```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 4.5 更新用户信息

**接口信息**

* **URL**: `PUT /api/auth/profile`

* **描述**: 更新当前用户的个人信息

* **权限**: 需要认证

**请求头**

```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**

```json
{
  "fullName": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138000"
}
```

| 参数名      | 类型     | 必填 | 描述     |
| -------- | ------ | -- | ------ |
| fullName | String | 否  | 用户真实姓名 |
| email    | String | 否  | 邮箱地址   |
| phone    | String | 否  | 手机号码   |

**响应示例**

```json
{
  "success": true,
  "code": 200,
  "message": "用户信息更新成功",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "teacher001",
    "fullName": "张三",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "createdAt": "2025-01-28T00:00:00Z",
    "updatedAt": "2025-01-28T14:20:00Z"
  },
  "timestamp": "2025-01-28T14:20:00Z",
  "requestId": "req-123456790"
}
```

### 4.6 修改密码

**接口信息**

* **URL**: `POST /api/auth/change-password`

* **描述**: 修改当前用户密码

* **权限**: 需要认证

**请求头**

```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**

```json
{
  "currentPassword": "SecurePass123!",
  "newPassword": "NewSecurePass123!",
  "confirmPassword": "NewSecurePass123!"
}
```

| 参数名             | 类型     | 必填 | 描述       |
| --------------- | ------ | -- | -------- |
| currentPassword | String | 是  | 当前密码     |
| newPassword     | String | 是  | 新密码，至少8位 |
| confirmPassword | String | 是  | 确认新密码    |

**响应示例**

```json
{
  "success": true,
  "code": 200,
  "message": "密码修改成功",
  "data": null,
  "timestamp": "2025-01-28T14:30:00Z",
  "requestId": "req-123456791"
}
```

## 5. 教育机构管理API

### 5.1 创建机构

**接口信息**

* **URL**: `POST /api/v1/institutions`

* **描述**: 创建新的教育机构

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**

```json
{
  "name": "万里学院总部",
  "description": "万里学院主要教学机构",
  "contactEmail": "contact@wanli.edu",
  "contactPhone": "010-12345678",
  "address": "北京市朝阳区教育路1号"
}
```

| 参数名          | 类型     | 必填 | 说明           |
| ------------ | ------ | -- | ------------ |
| name         | String | 是  | 机构名称，最大100字符 |
| description  | String | 否  | 机构描述         |
| contactEmail | String | 否  | 联系邮箱         |
| contactPhone | String | 否  | 联系电话         |
| address      | String | 否  | 机构地址         |

**响应示例**

```json
{
  "success": true,
  "code": 201,
  "message": "机构创建成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "万里学院总部",
    "description": "万里学院主要教学机构",
    "contactEmail": "contact@wanli.edu",
    "contactPhone": "010-12345678",
    "address": "北京市朝阳区教育路1号",
    "status": "ACTIVE",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdAt": "2025-01-17T10:30:00Z",
    "updatedAt": "2025-01-17T10:30:00Z"
  },
  "timestamp": "2025-01-17T10:30:00Z",
  "requestId": "req-123456792"
}
```

### 5.2 查询机构列表

**接口信息**

* **URL**: `GET /api/v1/institutions`

* **描述**: 获取教育机构列表（支持分页和筛选）

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
```

**查询参数**

| 参数名    | 类型      | 必填 | 说明                               |
| ------ | ------- | -- | -------------------------------- |
| page   | Integer | 否  | 页码，默认0                           |
| size   | Integer | 否  | 每页大小，默认20                        |
| sort   | String  | 否  | 排序，默认createdAt,desc              |
| search | String  | 否  | 搜索关键词（机构名称）                      |
| status | String  | 否  | 机构状态：ACTIVE, INACTIVE, SUSPENDED |

**响应示例**

```json
{
  "success": true,
  "code": 200,
  "message": "查询成功",
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440001",
        "name": "万里学院总部",
        "description": "万里学院主要教学机构",
        "contactEmail": "contact@wanli.edu",
        "contactPhone": "010-12345678",
        "address": "北京市朝阳区教育路1号",
        "status": "ACTIVE",
        "createdAt": "2025-01-17T10:30:00Z",
        "updatedAt": "2025-01-17T10:30:00Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "timestamp": "2025-01-17T10:30:00Z",
  "requestId": "req-123456793"
}
```

### 5.3 查询机构详情

**接口信息**

* **URL**: `GET /api/v1/institutions/{id}`

* **描述**: 获取指定机构的详细信息

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
```

**路径参数**

| 参数名 | 类型   | 必填 | 说明   |
| --- | ---- | -- | ---- |
| id  | UUID | 是  | 机构ID |

**响应示例**

```json
{
  "success": true,
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "万里学院总部",
    "description": "万里学院主要教学机构",
    "contactEmail": "contact@wanli.edu",
    "contactPhone": "010-12345678",
    "address": "北京市朝阳区教育路1号",
    "status": "ACTIVE",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdAt": "2025-01-17T10:30:00Z",
    "updatedAt": "2025-01-17T10:30:00Z"
  },
  "timestamp": "2025-01-17T10:30:00Z",
  "requestId": "req-123456794"
}
```

### 5.4 更新机构信息

**接口信息**

* **URL**: `PUT /api/v1/institutions/{id}`

* **描述**: 更新指定机构信息

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
Content-Type: application/json
```

**路径参数**

| 参数名 | 类型   | 必填 | 说明   |
| --- | ---- | -- | ---- |
| id  | UUID | 是  | 机构ID |

**请求参数**

```json
{
  "name": "万里学院总部（更新）",
  "description": "万里学院主要教学机构（更新）",
  "contactEmail": "contact@wanli.edu",
  "contactPhone": "010-12345678",
  "address": "北京市朝阳区教育路1号"
}
```

### 5.5 删除机构

**接口信息**

* **URL**: `DELETE /api/v1/institutions/{id}`

* **描述**: 删除指定机构（软删除）

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
```

**路径参数**

| 参数名 | 类型   | 必填 | 说明   |
| --- | ---- | -- | ---- |
| id  | UUID | 是  | 机构ID |

**响应示例**

```json
{
  "success": true,
  "code": 200,
  "message": "机构删除成功",
  "data": null,
  "timestamp": "2025-01-17T10:30:00Z",
  "requestId": "req-123456795"
}
```

## 6. 课程管理API

### 6.1 创建课程

**接口信息**

* **URL**: `POST /api/courses` (SP1) / `POST /api/v1/courses` (SP2)

* **描述**: 创建新课程

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数（SP1格式）**

```json
{
  "courseName": "小学一年级语文",
  "courseDescription": "小学一年级语文基础课程，包含拼音、识字、阅读等内容",
  "gradeLevel": "GRADE_1",
  "subject": "CHINESE"
}
```

**请求参数（SP2格式）**

```json
{
  "name": "Java基础编程",
  "description": "Java编程语言基础课程",
  "institutionId": "550e8400-e29b-41d4-a716-446655440001",
  "price": 2999.00,
  "durationHours": 40,
  "startDate": "2025-02-01T09:00:00Z",
  "endDate": "2025-03-01T18:00:00Z",
  "maxStudents": 30
}
```

**参数说明**

| 参数名                           | 类型         | 必填 | 描述       | 验证规则                       |
| ----------------------------- | ---------- | -- | -------- | -------------------------- |
| courseName/name               | String     | 是  | 课程名称     | 2-100字符，不能包含特殊字符           |
| courseDescription/description | String     | 否  | 课程描述     | 最大500字符                    |
| gradeLevel                    | String     | 是  | 年级       | 枚举值：GRADE\_1\~GRADE\_6     |
| subject                       | String     | 是  | 学科       | 枚举值：CHINESE, MATH, ENGLISH |
| institutionId                 | UUID       | 是  | 所属机构ID   | SP2新增                      |
| price                         | BigDecimal | 否  | 课程价格     | 默认0.00                     |
| durationHours                 | Integer    | 否  | 课程时长(小时) | 默认0                        |
| startDate                     | DateTime   | 否  | 开始时间     | ISO 8601格式                 |
| endDate                       | DateTime   | 否  | 结束时间     | ISO 8601格式                 |
| maxStudents                   | Integer    | 否  | 最大学员数    | 默认50                       |

**响应示例（SP1）**

```json
{
  "success": true,
  "code": 201,
  "message": "课程创建成功",
  "data": {
    "courseId": "550e8400-e29b-41d4-a716-446655440001",
    "courseCode": "G1CH001",
    "courseName": "小学一年级语文",
    "courseDescription": "小学一年级语文基础课程，包含拼音、识字、阅读等内容",
    "gradeLevel": "GRADE_1",
    "subject": "CHINESE",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdAt": "2025-01-15T10:30:00Z",
    "updatedAt": "2025-01-15T10:30:00Z",
    "isActive": true,
    "lessonCount": 0
  },
  "timestamp": "2025-01-15T10:30:00Z",
  "requestId": "req-123456789"
}
```

**响应示例（SP2）**

```json
{
  "success": true,
  "code": 201,
  "message": "课程创建成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "name": "Java基础编程",
    "description": "Java编程语言基础课程",
    "institutionId": "550e8400-e29b-41d4-a716-446655440001",
    "institutionName": "万里学院总部",
    "price": 2999.00,
    "durationHours": 40,
    "status": "DRAFT",
    "startDate": "2025-02-01T09:00:00Z",
    "endDate": "2025-03-01T18:00:00Z",
    "maxStudents": 30,
    "currentStudents": 0,
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdAt": "2025-01-17T10:30:00Z",
    "updatedAt": "2025-01-17T10:30:00Z"
  },
  "timestamp": "2025-01-17T10:30:00Z",
  "requestId": "req-123456796"
}
```

**cURL示例**

```bash
# SP1格式
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "courseName": "小学一年级语文",
    "courseDescription": "小学一年级语文基础课程，包含拼音、识字、阅读等内容",
    "gradeLevel": "GRADE_1",
    "subject": "CHINESE"
  }'

# SP2格式
curl -X POST http://localhost:8080/api/v1/courses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "name": "Java基础编程",
    "description": "Java编程语言基础课程",
    "institutionId": "550e8400-e29b-41d4-a716-446655440001",
    "price": 2999.00,
    "durationHours": 40,
    "maxStudents": 30
  }'
```

### 6.2 获取课程列表

**接口信息**

* **URL**: `GET /api/courses` (SP1) / `GET /api/v1/courses` (SP2)

* **描述**: 获取课程列表（支持分页和筛选）

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
```

**查询参数**

| 参数名           | 类型      | 必填 | 描述          | 默认值            |
| ------------- | ------- | -- | ----------- | -------------- |
| page          | Integer | 否  | 页码（从0开始）    | 0              |
| size          | Integer | 否  | 每页大小        | 20             |
| sort          | String  | 否  | 排序字段        | createdAt,desc |
| gradeLevel    | String  | 否  | 年级筛选        | 无              |
| subject       | String  | 否  | 学科筛选        | 无              |
| search        | String  | 否  | 搜索关键词（课程名称） | 无              |
| isActive      | Boolean | 否  | 是否激活        | true           |
| institutionId | UUID    | 否  | 机构ID（SP2）   | 无              |
| status        | String  | 否  | 课程状态（SP2）   | 无              |

**响应示例**

```json
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "courseId": "550e8400-e29b-41d4-a716-446655440001",
        "courseCode": "G1CH001",
        "courseName": "小学一年级语文",
        "courseDescription": "小学一年级语文基础课程",
        "gradeLevel": "GRADE_1",
        "subject": "CHINESE",
        "createdBy": "550e8400-e29b-41d4-a716-446655440000",
        "createdAt": "2025-01-15T10:30:00Z",
        "updatedAt": "2025-01-15T10:30:00Z",
        "isActive": true,
        "lessonCount": 5
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "orders": [
          {
            "property": "createdAt",
            "direction": "DESC"
          }
        ]
      }
    },
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true,
    "numberOfElements": 1
  },
  "timestamp": "2025-01-15T10:30:00Z",
  "requestId": "req-123456797"
}
```

### 6.3 获取课程详情

**接口信息**

* **URL**: `GET /api/courses/{courseId}` (SP1) / `GET /api/v1/courses/{id}` (SP2)

* **描述**: 获取指定课程的详细信息

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
```

**路径参数**

| 参数名         | 类型   | 必填 | 描述   |
| ----------- | ---- | -- | ---- |
| courseId/id | UUID | 是  | 课程ID |

**响应示例**

```json
{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "courseId": "550e8400-e29b-41d4-a716-446655440001",
    "courseCode": "G1CH001",
    "courseName": "小学一年级语文",
    "courseDescription": "小学一年级语文基础课程，包含拼音、识字、阅读等内容",
    "gradeLevel": "GRADE_1",
    "subject": "CHINESE",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdAt": "2025-01-15T10:30:00Z",
    "updatedAt": "2025-01-15T10:30:00Z",
    "isActive": true,
    "lessonCount": 5,
    "lessons": [
      {
        "lessonId": "550e8400-e29b-41d4-a716-446655440002",
        "lessonTitle": "第一课：拼音基础",
        "lessonOrder": 1,
        "createdAt": "2025-01-15T10:30:00Z"
      }
    ]
  },
  "timestamp": "2025-01-15T10:30:00Z",
  "requestId": "req-123456798"
}
```

### 6.4 更新课程

**接口信息**

* **URL**: `PUT /api/courses/{courseId}` (SP1) / `PUT /api/v1/courses/{id}` (SP2)

* **描述**: 更新指定课程信息

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
Content-Type: application/json
```

**路径参数**

| 参数名         | 类型   | 必填 | 描述   |
| ----------- | ---- | -- | ---- |
| courseId/id | UUID | 是  | 课程ID |

**请求参数**

```json
{
  "courseName": "小学一年级语文（更新版）",
  "courseDescription": "更新后的课程描述",
  "gradeLevel": "GRADE_1",
  "subject": "CHINESE"
}
```

### 6.5 删除课程

**接口信息**

* **URL**: `DELETE /api/courses/{courseId}` (SP1) / `DELETE /api/v1/courses/{id}` (SP2)

* **描述**: 删除指定课程（软删除）

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
```

**路径参数**

| 参数名         | 类型   | 必填 | 描述   |
| ----------- | ---- | -- | ---- |
| courseId/id | UUID | 是  | 课程ID |

**响应示例**

```json
{
  "success": true,
  "code": 200,
  "message": "课程删除成功",
  "data": null,
  "timestamp": "2025-01-15T11:00:00Z",
  "requestId": "req-123456799"
}
```

### 6.6 发布课程（SP2）

**接口信息**

* **URL**: `POST /api/v1/courses/{id}/publish`

* **描述**: 发布课程

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
```

**路径参数**

| 参数名 | 类型   | 必填 | 说明   |
| --- | ---- | -- | ---- |
| id  | UUID | 是  | 课程ID |

**响应示例**

```json
{
  "success": true,
  "code": 200,
  "message": "课程发布成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "status": "PUBLISHED"
  },
  "timestamp": "2025-01-17T10:30:00Z",
  "requestId": "req-123456800"
}
```

### 6.7 取消课程（SP2）

**接口信息**

* **URL**: `POST /api/v1/courses/{id}/cancel`

* **描述**: 取消课程

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
```

## 7. 课时管理API（SP1）

### 7.1 创建课时

**接口信息**

* **URL**: `POST /api/courses/{courseId}/lessons`

* **描述**: 为指定课程创建新课时

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
Content-Type: application/json
```

**路径参数**

| 参数名      | 类型   | 必填 | 描述   |
| -------- | ---- | -- | ---- |
| courseId | UUID | 是  | 课程ID |

**请求参数**

```json
{
  "lessonTitle": "第一课：拼音基础",
  "lessonContent": "本课时主要学习拼音的基础知识，包括声母、韵母的发音方法...",
  "lessonOrder": 1
}
```

| 参数名           | 类型      | 必填 | 描述   | 验证规则      |
| ------------- | ------- | -- | ---- | --------- |
| lessonTitle   | String  | 是  | 课时标题 | 2-200字符   |
| lessonContent | String  | 否  | 课时内容 | 最大10000字符 |
| lessonOrder   | Integer | 是  | 课时顺序 | 正整数       |

**响应示例**

```json
{
  "success": true,
  "code": 201,
  "message": "课时创建成功",
  "data": {
    "lessonId": "550e8400-e29b-41d4-a716-446655440002",
    "courseId": "550e8400-e29b-41d4-a716-446655440001",
    "lessonTitle": "第一课：拼音基础",
    "lessonContent": "本课时主要学习拼音的基础知识，包括声母、韵母的发音方法...",
    "lessonOrder": 1,
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdAt": "2025-01-15T11:00:00Z",
    "updatedAt": "2025-01-15T11:00:00Z",
    "isActive": true
  },
  "timestamp": "2025-01-15T11:00:00Z",
  "requestId": "req-123456801"
}
```

### 7.2 获取课时列表

**接口信息**

* **URL**: `GET /api/courses/{courseId}/lessons`

* **描述**: 获取指定课程的课时列表

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
```

**路径参数**

| 参数名      | 类型   | 必填 | 描述   |
| -------- | ---- | -- | ---- |
| courseId | UUID | 是  | 课程ID |

**查询参数**

| 参数名      | 类型      | 必填 | 描述       | 默认值             |
| -------- | ------- | -- | -------- | --------------- |
| page     | Integer | 否  | 页码（从0开始） | 0               |
| size     | Integer | 否  | 每页大小     | 20              |
| sort     | String  | 否  | 排序字段     | lessonOrder,asc |
| isActive | Boolean | 否  | 是否激活     | true            |

### 7.3 获取课时详情

**接口信息**

* **URL**: `GET /api/lessons/{lessonId}`

* **描述**: 获取指定课时的详细信息

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
```

**路径参数**

| 参数名      | 类型   | 必填 | 描述   |
| -------- | ---- | -- | ---- |
| lessonId | UUID | 是  | 课时ID |

### 7.4 更新课时

**接口信息**

* **URL**: `PUT /api/lessons/{lessonId}`

* **描述**: 更新指定课时信息

* **权限**: ROLE\_HQ\_TEACHER

### 7.5 删除课时

**接口信息**

* **URL**: `DELETE /api/lessons/{lessonId}`

* **描述**: 删除指定课时（软删除）

* **权限**: ROLE\_HQ\_TEACHER

## 8. 学员管理API（SP2）

### 8.1 创建学员

**接口信息**

* **URL**: `POST /api/v1/students`

* **描述**: 创建新学员

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**

```json
{
  "name": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138001",
  "birthDate": "1995-06-15",
  "gender": "MALE",
  "address": "北京市朝阳区",
  "institutionId": "550e8400-e29b-41d4-a716-446655440001"
}
```

| 参数名           | 类型     | 必填 | 说明                     |
| ------------- | ------ | -- | ---------------------- |
| name          | String | 是  | 学员姓名，最大50字符            |
| email         | String | 否  | 邮箱地址                   |
| phone         | String | 否  | 电话号码                   |
| birthDate     | Date   | 否  | 出生日期                   |
| gender        | String | 否  | 性别：MALE, FEMALE, OTHER |
| address       | String | 否  | 地址                     |
| institutionId | UUID   | 是  | 所属机构ID                 |

**响应示例**

```json
{
  "success": true,
  "code": 201,
  "message": "学员创建成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440005",
    "name": "张三",
    "email": "zhangsan@example.com",
    "phone": "13800138001",
    "birthDate": "1995-06-15",
    "gender": "MALE",
    "address": "北京市朝阳区",
    "institutionId": "550e8400-e29b-41d4-a716-446655440001",
    "institutionName": "万里学院总部",
    "status": "ACTIVE",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdAt": "2025-01-17T10:30:00Z",
    "updatedAt": "2025-01-17T10:30:00Z"
  },
  "timestamp": "2025-01-17T10:30:00Z",
  "requestId": "req-123456802"
}
```

### 8.2 查询学员列表

**接口信息**

* **URL**: `GET /api/v1/students`

* **描述**: 获取学员列表（支持分页和筛选）

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
```

**查询参数**

| 参数名           | 类型      | 必填 | 说明                                        |
| ------------- | ------- | -- | ----------------------------------------- |
| page          | Integer | 否  | 页码，默认0                                    |
| size          | Integer | 否  | 每页大小，默认20                                 |
| sort          | String  | 否  | 排序，默认createdAt,desc                       |
| search        | String  | 否  | 搜索关键词（学员姓名、邮箱、电话）                         |
| institutionId | UUID    | 否  | 机构ID                                      |
| status        | String  | 否  | 学员状态：ACTIVE, INACTIVE, GRADUATED, DROPPED |
| gender        | String  | 否  | 性别：MALE, FEMALE, OTHER                    |

### 8.3 查询学员详情

**接口信息**

* **URL**: `GET /api/v1/students/{id}`

* **描述**: 获取指定学员的详细信息

* **权限**: ROLE\_HQ\_TEACHER

### 8.4 更新学员信息

**接口信息**

* **URL**: `PUT /api/v1/students/{id}`

* **描述**: 更新指定学员信息

* **权限**: ROLE\_HQ\_TEACHER

### 8.5 删除学员

**接口信息**

* **URL**: `DELETE /api/v1/students/{id}`

* **描述**: 删除指定学员（软删除）

* **权限**: ROLE\_HQ\_TEACHER

## 9. 课程学员关联管理API（SP2）

### 9.1 学员报名课程

**接口信息**

* **URL**: `POST /api/v1/courses/{courseId}/students/{studentId}/enroll`

* **描述**: 学员报名指定课程

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
Content-Type: application/json
```

**路径参数**

| 参数名       | 类型   | 必填 | 说明   |
| --------- | ---- | -- | ---- |
| courseId  | UUID | 是  | 课程ID |
| studentId | UUID | 是  | 学员ID |

**请求参数**

```json
{
  "paidAmount": 2999.00,
  "paymentDate": "2025-01-17T10:30:00Z",
  "notes": "全额付款"
}
```

| 参数名         | 类型         | 必填 | 说明          |
| ----------- | ---------- | -- | ----------- |
| paidAmount  | BigDecimal | 否  | 已付金额，默认0.00 |
| paymentDate | DateTime   | 否  | 付款时间        |
| notes       | String     | 否  | 备注信息        |

**响应示例**

```json
{
  "success": true,
  "code": 201,
  "message": "报名成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440007",
    "courseId": "550e8400-e29b-41d4-a716-446655440003",
    "courseName": "Java基础编程",
    "studentId": "550e8400-e29b-41d4-a716-446655440005",
    "studentName": "张三",
    "enrollmentDate": "2025-01-17T10:30:00Z",
    "status": "ENROLLED",
    "paidAmount": 2999.00,
    "paymentDate": "2025-01-17T10:30:00Z",
    "notes": "全额付款",
    "createdAt": "2025-01-17T10:30:00Z"
  },
  "timestamp": "2025-01-17T10:30:00Z",
  "requestId": "req-123456803"
}
```

### 9.2 查询课程学员列表

**接口信息**

* **URL**: `GET /api/v1/courses/{courseId}/students`

* **描述**: 获取指定课程的学员列表

* **权限**: ROLE\_HQ\_TEACHER

**请求头**

```
Authorization: Bearer {token}
```

**路径参数**

| 参数名      | 类型   | 必填 | 说明   |
| -------- | ---- | -- | ---- |
| courseId | UUID | 是  | 课程ID |

**查询参数**

| 参数名    | 类型      | 必填 | 说明                                           |
| ------ | ------- | -- | -------------------------------------------- |
| page   | Integer | 否  | 页码，默认0                                       |
| size   | Integer | 否  | 每页大小，默认20                                    |
| sort   | String  | 否  | 排序，默认enrollmentDate,desc                     |
| status | String  | 否  | 报名状态：ENROLLED, COMPLETED, DROPPED, SUSPENDED |

### 9.3 查询学员课程列表

**接口信息**

* **URL**: `GET /api/v1/students/{studentId}/courses`

* **描述**: 获取指定学员的课程列表

* **权限**: ROLE\_HQ\_TEACHER

### 9.4 学员退课

**接口信息**

* **URL**: `POST /api/v1/courses/{courseId}/students/{studentId}/drop`

* **描述**: 学员退出指定课程

* **权限**: ROLE\_HQ\_TEACHER

### 9.5 学员完成课程

**接口信息**

* **URL**: `POST /api/v1/courses/{courseId}/students/{studentId}/complete`

* **描述**: 标记学员完成课程

* **权限**: ROLE\_HQ\_TEACHER

## 10. 系统接口

### 10.1 健康检查

**接口信息**

* **URL**: `GET /api/health`

* **描述**: 检查系统运行状态

* **权限**: 无需认证

**响应示例**

```json
{
  "success": true,
  "code": 200,
  "message": "系统运行正常",
  "data": {
    "status": "UP",
    "timestamp": "2025-01-28T00:00:00Z",
    "version": "1.0.0",
    "database": {
      "status": "UP",
      "responseTime": "5ms"
    }
  },
  "timestamp": "2025-01-28T00:00:00Z",
  "requestId": "req-123456804"
}
```

## 11. 错误码定义

### 11.1 系统级错误 (1000-1999)

| 错误码  | HTTP状态码 | 描述     | 解决方案          |
| ---- | ------- | ------ | ------------- |
| 1000 | 500     | 系统内部错误 | 联系技术支持        |
| 1001 | 400     | 请求参数无效 | 检查请求参数格式和必填字段 |
| 1002 | 401     | 未授权访问  | 提供有效          |

