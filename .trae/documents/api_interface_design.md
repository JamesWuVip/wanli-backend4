# SP1后端API接口设计文档

## 文档信息

* **文档名称**: SP1后端API接口设计文档
* **版本**: v1.0.0
* **创建日期**: 2025-01-15
* **适用阶段**: SP1（核心功能实现阶段）
* **文档级别**: P0（核心文档）

## 1. 概述

### 1.1 文档目的

本文档详细定义SP1阶段所有后端API接口规范，为前端开发、接口测试和系统集成提供标准化的接口定义。

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

## 2. API设计原则

### 2.1 RESTful设计原则

* 使用HTTP动词表示操作：GET（查询）、POST（创建）、PUT（更新）、DELETE（删除）
* 使用名词表示资源：`/api/courses`、`/api/lessons`
* 使用HTTP状态码表示结果：200（成功）、400（客户端错误）、500（服务器错误）
* 支持资源的层次化：`/api/courses/{courseId}/lessons`

### 2.2 统一响应格式

```json
{
  "success": true,
  "message": "操作成功",
  "data": {},
  "timestamp": "2025-01-15T10:30:00Z",
  "requestId": "req-123456789"
}
```

### 2.3 错误处理规范

```json
{
  "success": false,
  "message": "错误描述",
  "errorCode": "BUSINESS_ERROR_001",
  "details": "详细错误信息",
  "timestamp": "2025-01-15T10:30:00Z",
  "requestId": "req-123456789"
}
```

## 3. 认证与授权

### 3.1 JWT认证机制

* **认证方式**: Bearer Token
* **Token位置**: HTTP Header `Authorization: Bearer <token>`
* **Token有效期**: 24小时
* **刷新机制**: 自动刷新（Token过期前30分钟）

### 3.2 权限控制

* **ROLE\_HQ\_TEACHER**: 总部教师角色，拥有所有课程和课时的管理权限
* **权限验证**: 基于Spring Security的方法级权限控制

## 4. 认证API设计

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
  "email": "teacher001@wanli.edu",
  "fullName": "张老师",
  "phoneNumber": "13800138000"
}
```

| 参数名         | 类型     | 必填 | 描述  | 验证规则                 |
| :---------- | :----- | :- | :-- | :------------------- |
| username    | String | 是  | 用户名 | 4-20字符，字母数字下划线       |
| password    | String | 是  | 密码  | 8-50字符，包含大小写字母数字特殊字符 |
| email       | String | 是  | 邮箱  | 有效邮箱格式               |
| fullName    | String | 是  | 姓名  | 2-20字符               |
| phoneNumber | String | 否  | 手机号 | 11位数字                |

**响应示例**

```json
{
  "success": true,
  "message": "注册成功",
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
| :------- | :----- | :- | :----- |
| username | String | 是  | 用户名或邮箱 |
| password | String | 是  | 密码     |

**响应示例**

```json
{
  "success": true,
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

**请求参数**
无

**响应示例**

```json
{
  "success": true,
  "message": "获取成功",
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

**请求参数**
无

**响应示例**

```json
{
  "success": true,
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

## 5. 课程管理API设计

### 5.1 创建课程

**接口信息**

* **URL**: `POST /api/courses`
* **描述**: 创建新课程
* **权限**: ROLE\_HQ\_TEACHER

**请求参数**

```json
{
  "courseName": "小学一年级语文",
  "courseDescription": "小学一年级语文基础课程，包含拼音、识字、阅读等内容",
  "gradeLevel": "GRADE_1",
  "subject": "CHINESE"
}
```

| 参数名               | 类型     | 必填 | 描述   | 验证规则                       |
| :---------------- | :----- | :- | :--- | :------------------------- |
| courseName        | String | 是  | 课程名称 | 2-100字符，不能包含特殊字符           |
| courseDescription | String | 否  | 课程描述 | 最大500字符                    |
| gradeLevel        | String | 是  | 年级   | 枚举值：GRADE\_1\~GRADE\_6     |
| subject           | String | 是  | 学科   | 枚举值：CHINESE, MATH, ENGLISH |

**响应示例**

```json
{
  "success": true,
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

**cURL示例**

```bash
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "courseName": "小学一年级语文",
    "courseDescription": "小学一年级语文基础课程，包含拼音、识字、阅读等内容",
    "gradeLevel": "GRADE_1",
    "subject": "CHINESE"
  }'
```

### 5.2 获取课程列表

**接口信息**

* **URL**: `GET /api/courses`
* **描述**: 获取课程列表（支持分页和筛选）
* **权限**: ROLE\_HQ\_TEACHER

**请求参数**

| 参数名        | 类型      | 必填 | 描述          | 默认值            |
| :--------- | :------ | :- | :---------- | :------------- |
| page       | Integer | 否  | 页码（从0开始）    | 0              |
| size       | Integer | 否  | 每页大小        | 20             |
| sort       | String  | 否  | 排序字段        | createdAt,desc |
| gradeLevel | String  | 否  | 年级筛选        | 无              |
| subject    | String  | 否  | 学科筛选        | 无              |
| search     | String  | 否  | 搜索关键词（课程名称） | 无              |
| isActive   | Boolean | 否  | 是否激活        | true           |

**响应示例**

```json
{
  "success": true,
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
  "requestId": "req-123456789"
}
```

**cURL示例**

```bash
curl -X GET "http://localhost:8080/api/courses?page=0&size=20&gradeLevel=GRADE_1&subject=CHINESE" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 5.3 获取课程详情

**接口信息**

* **URL**: `GET /api/courses/{courseId}`
* **描述**: 获取指定课程的详细信息
* **权限**: ROLE\_HQ\_TEACHER

**路径参数**

| 参数名      | 类型   | 必填 | 描述   |
| :------- | :--- | :- | :--- |
| courseId | UUID | 是  | 课程ID |

**响应示例**

```json
{
  "success": true,
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
  "requestId": "req-123456789"
}
```

**cURL示例**

```bash
curl -X GET http://localhost:8080/api/courses/550e8400-e29b-41d4-a716-446655440001 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 5.4 更新课程

**接口信息**

* **URL**: `PUT /api/courses/{courseId}`
* **描述**: 更新指定课程信息
* **权限**: ROLE\_HQ\_TEACHER

**路径参数**

| 参数名      | 类型   | 必填 | 描述   |
| :------- | :--- | :- | :--- |
| courseId | UUID | 是  | 课程ID |

**请求参数**

```json
{
  "courseName": "小学一年级语文（更新版）",
  "courseDescription": "更新后的课程描述",
  "gradeLevel": "GRADE_1",
  "subject": "CHINESE"
}
```

**响应示例**

```json
{
  "success": true,
  "message": "课程更新成功",
  "data": {
    "courseId": "550e8400-e29b-41d4-a716-446655440001",
    "courseCode": "G1CH001",
    "courseName": "小学一年级语文（更新版）",
    "courseDescription": "更新后的课程描述",
    "gradeLevel": "GRADE_1",
    "subject": "CHINESE",
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdAt": "2025-01-15T10:30:00Z",
    "updatedAt": "2025-01-15T11:00:00Z",
    "isActive": true,
    "lessonCount": 5
  },
  "timestamp": "2025-01-15T11:00:00Z",
  "requestId": "req-123456790"
}
```

**cURL示例**

```bash
curl -X PUT http://localhost:8080/api/courses/550e8400-e29b-41d4-a716-446655440001 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "courseName": "小学一年级语文（更新版）",
    "courseDescription": "更新后的课程描述",
    "gradeLevel": "GRADE_1",
    "subject": "CHINESE"
  }'
```

### 5.5 删除课程

**接口信息**

* **URL**: `DELETE /api/courses/{courseId}`
* **描述**: 删除指定课程（软删除）
* **权限**: ROLE\_HQ\_TEACHER

**路径参数**

| 参数名      | 类型   | 必填 | 描述   |
| :------- | :--- | :- | :--- |
| courseId | UUID | 是  | 课程ID |

**响应示例**

```json
{
  "success": true,
  "message": "课程删除成功",
  "data": null,
  "timestamp": "2025-01-15T11:00:00Z",
  "requestId": "req-123456791"
}
```

**cURL示例**

```bash
curl -X DELETE http://localhost:8080/api/courses/550e8400-e29b-41d4-a716-446655440001 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 6. 课时管理API设计

### 6.1 创建课时

**接口信息**

* **URL**: `POST /api/courses/{courseId}/lessons`
* **描述**: 为指定课程创建新课时
* **权限**: ROLE\_HQ\_TEACHER

**路径参数**

| 参数名      | 类型   | 必填 | 描述   |
| :------- | :--- | :- | :--- |
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
| :------------ | :------ | :- | :--- | :-------- |
| lessonTitle   | String  | 是  | 课时标题 | 2-200字符   |
| lessonContent | String  | 否  | 课时内容 | 最大10000字符 |
| lessonOrder   | Integer | 是  | 课时顺序 | 正整数       |

**响应示例**

```json
{
  "success": true,
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
  "requestId": "req-123456792"
}
```

**cURL示例**

```bash
curl -X POST http://localhost:8080/api/courses/550e8400-e29b-41d4-a716-446655440001/lessons \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "lessonTitle": "第一课：拼音基础",
    "lessonContent": "本课时主要学习拼音的基础知识，包括声母、韵母的发音方法...",
    "lessonOrder": 1
  }'
```

### 6.2 获取课时列表

**接口信息**

* **URL**: `GET /api/courses/{courseId}/lessons`
* **描述**: 获取指定课程的课时列表
* **权限**: ROLE\_HQ\_TEACHER

**路径参数**

| 参数名      | 类型   | 必填 | 描述   |
| :------- | :--- | :- | :--- |
| courseId | UUID | 是  | 课程ID |

**请求参数**

| 参数名      | 类型      | 必填 | 描述       | 默认值             |
| :------- | :------ | :- | :------- | :-------------- |
| page     | Integer | 否  | 页码（从0开始） | 0               |
| size     | Integer | 否  | 每页大小     | 20              |
| sort     | String  | 否  | 排序字段     | lessonOrder,asc |
| isActive | Boolean | 否  | 是否激活     | true            |

**响应示例**

```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "lessonId": "550e8400-e29b-41d4-a716-446655440002",
        "courseId": "550e8400-e29b-41d4-a716-446655440001",
        "lessonTitle": "第一课：拼音基础",
        "lessonContent": "本课时主要学习拼音的基础知识...",
        "lessonOrder": 1,
        "createdBy": "550e8400-e29b-41d4-a716-446655440000",
        "createdAt": "2025-01-15T11:00:00Z",
        "updatedAt": "2025-01-15T11:00:00Z",
        "isActive": true
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "orders": [
          {
            "property": "lessonOrder",
            "direction": "ASC"
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
  "timestamp": "2025-01-15T11:00:00Z",
  "requestId": "req-123456793"
}
```

**cURL示例**

```bash
curl -X GET "http://localhost:8080/api/courses/550e8400-e29b-41d4-a716-446655440001/lessons?page=0&size=20" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 6.3 获取课时详情

**接口信息**

* **URL**: `GET /api/lessons/{lessonId}`
* **描述**: 获取指定课时的详细信息
* **权限**: ROLE\_HQ\_TEACHER

**路径参数**

| 参数名      | 类型   | 必填 | 描述   |
| :------- | :--- | :- | :--- |
| lessonId | UUID | 是  | 课时ID |

**响应示例**

```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "lessonId": "550e8400-e29b-41d4-a716-446655440002",
    "courseId": "550e8400-e29b-41d4-a716-446655440001",
    "course": {
      "courseId": "550e8400-e29b-41d4-a716-446655440001",
      "courseCode": "G1CH001",
      "courseName": "小学一年级语文",
      "gradeLevel": "GRADE_1",
      "subject": "CHINESE"
    },
    "lessonTitle": "第一课：拼音基础",
    "lessonContent": "本课时主要学习拼音的基础知识，包括声母、韵母的发音方法...",
    "lessonOrder": 1,
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdAt": "2025-01-15T11:00:00Z",
    "updatedAt": "2025-01-15T11:00:00Z",
    "isActive": true
  },
  "timestamp": "2025-01-15T11:00:00Z",
  "requestId": "req-123456794"
}
```

**cURL示例**

```bash
curl -X GET http://localhost:8080/api/lessons/550e8400-e29b-41d4-a716-446655440002 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 6.4 更新课时

**接口信息**

* **URL**: `PUT /api/lessons/{lessonId}`
* **描述**: 更新指定课时信息
* **权限**: ROLE\_HQ\_TEACHER

**路径参数**

| 参数名      | 类型   | 必填 | 描述   |
| :------- | :--- | :- | :--- |
| lessonId | UUID | 是  | 课时ID |

**请求参数**

```json
{
  "lessonTitle": "第一课：拼音基础（更新版）",
  "lessonContent": "更新后的课时内容...",
  "lessonOrder": 1
}
```

**响应示例**

```json
{
  "success": true,
  "message": "课时更新成功",
  "data": {
    "lessonId": "550e8400-e29b-41d4-a716-446655440002",
    "courseId": "550e8400-e29b-41d4-a716-446655440001",
    "lessonTitle": "第一课：拼音基础（更新版）",
    "lessonContent": "更新后的课时内容...",
    "lessonOrder": 1,
    "createdBy": "550e8400-e29b-41d4-a716-446655440000",
    "createdAt": "2025-01-15T11:00:00Z",
    "updatedAt": "2025-01-15T11:30:00Z",
    "isActive": true
  },
  "timestamp": "2025-01-15T11:30:00Z",
  "requestId": "req-123456795"
}
```

**cURL示例**

```bash
curl -X PUT http://localhost:8080/api/lessons/550e8400-e29b-41d4-a716-446655440002 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "lessonTitle": "第一课：拼音基础（更新版）",
    "lessonContent": "更新后的课时内容...",
    "lessonOrder": 1
  }'
```

### 6.5 删除课时

**接口信息**

* **URL**: `DELETE /api/lessons/{lessonId}`
* **描述**: 删除指定课时（软删除）
* **权限**: ROLE\_HQ\_TEACHER

**路径参数**

| 参数名      | 类型   | 必填 | 描述   |
| :------- | :--- | :- | :--- |
| lessonId | UUID | 是  | 课时ID |

**响应示例**

```json
{
  "success": true,
  "message": "课时删除成功",
  "data": null,
  "timestamp": "2025-01-15T11:30:00Z",
  "requestId": "req-123456796"
}
```

**cURL示例**

```bash
curl -X DELETE http://localhost:8080/api/lessons/550e8400-e29b-41d4-a716-446655440002 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 7. 系统监控API设计

### 7.1 健康检查

**接口信息**

* **URL**: `GET /api/health`
* **描述**: 系统健康检查接口
* **权限**: 无需认证

**响应示例**

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "redis": {
      "status": "UP",
      "details": {
        "version": "7.0.0"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 91943821312,
        "threshold": 10485760,
        "exists": true
      }
    }
  }
}
```

**cURL示例**

```bash
curl -X GET http://localhost:8080/api/health
```

### 7.2 系统信息

**接口信息**

* **URL**: `GET /api/info`
* **描述**: 获取系统基本信息
* **权限**: 无需认证

**响应示例**

```json
{
  "app": {
    "name": "Wanli Academy Backend",
    "version": "1.0.0",
    "description": "万里学院后端API服务"
  },
  "build": {
    "version": "1.0.0",
    "artifact": "wanli-backend",
    "name": "wanli-backend",
    "group": "com.wanli",
    "time": "2025-01-15T10:00:00Z"
  },
  "git": {
    "branch": "dev",
    "commit": {
      "id": "abc123def456",
      "time": "2025-01-15T09:00:00Z"
    }
  }
}
```

**cURL示例**

```bash
curl -X GET http://localhost:8080/api/info
```

## 8. 错误码定义

### 8.1 HTTP状态码

| 状态码 | 描述                    | 使用场景     |
| :-- | :-------------------- | :------- |
| 200 | OK                    | 请求成功     |
| 201 | Created               | 资源创建成功   |
| 400 | Bad Request           | 请求参数错误   |
| 401 | Unauthorized          | 未认证或认证失败 |
| 403 | Forbidden             | 权限不足     |
| 404 | Not Found             | 资源不存在    |
| 409 | Conflict              | 资源冲突     |
| 422 | Unprocessable Entity  | 业务逻辑错误   |
| 429 | Too Many Requests     | 请求频率限制   |
| 500 | Internal Server Error | 服务器内部错误  |

### 8.2 业务错误码

| 错误码             | 描述        | HTTP状态码 |
| :-------------- | :-------- | :------ |
| AUTH\_001       | 用户名或密码错误  | 401     |
| AUTH\_002       | Token已过期  | 401     |
| AUTH\_003       | Token无效   | 401     |
| AUTH\_004       | 权限不足      | 403     |
| AUTH\_005       | 用户已存在     | 409     |
| COURSE\_001     | 课程不存在     | 404     |
| COURSE\_002     | 课程名称已存在   | 409     |
| COURSE\_003     | 课程代码生成失败  | 500     |
| LESSON\_001     | 课时不存在     | 404     |
| LESSON\_002     | 课时顺序冲突    | 409     |
| LESSON\_003     | 课时标题已存在   | 409     |
| VALIDATION\_001 | 参数验证失败    | 400     |
| VALIDATION\_002 | 必填参数缺失    | 400     |
| VALIDATION\_003 | 参数格式错误    | 400     |
| SYSTEM\_001     | 数据库连接失败   | 500     |
| SYSTEM\_002     | Redis连接失败 | 500     |
| SYSTEM\_003     | 系统内部错误    | 500     |

### 8.3 错误响应示例

**参数验证错误**

```json
{
  "success": false,
  "message": "参数验证失败",
  "errorCode": "VALIDATION_001",
  "details": {
    "courseName": "课程名称不能为空",
    "gradeLevel": "年级必须是有效的枚举值"
  },
  "timestamp": "2025-01-15T11:30:00Z",
  "requestId": "req-123456797"
}
```

**认证失败错误**

```json
{
  "success": false,
  "message": "Token已过期",
  "errorCode": "AUTH_002",
  "details": "请重新登录获取新的访问令牌",
  "timestamp": "2025-01-15T11:30:00Z",
  "requestId": "req-123456798"
}
```

**业务逻辑错误**

```json
{
  "success": false,
  "message": "课程不存在",
  "errorCode": "COURSE_001",
  "details": "指定的课程ID不存在或已被删除",
  "timestamp": "2025-01-15T11:30:00Z",
  "requestId": "req-123456799"
}
```

## 9. Postman测试集合

### 9.1 环境配置

**开发环境变量**

```json
{
  "id": "dev-environment",
  "name": "Development",
  "values": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080/api",
      "enabled": true
    },
    {
      "key": "accessToken",
      "value": "",
      "enabled": true
    },
    {
      "key": "userId",
      "value": "",
      "enabled": true
    },
    {
      "key": "courseId",
      "value": "",
      "enabled": true
    },
    {
      "key": "lessonId",
      "value": "",
      "enabled": true
    }
  ]
}
```

**生产环境变量**

```json
{
  "id": "prod-environment",
  "name": "Production",
  "values": [
    {
      "key": "baseUrl",
      "value": "https://wanli-backend.railway.app/api",
      "enabled": true
    },
    {
      "key": "accessToken",
      "value": "",
      "enabled": true
    }
  ]
}
```

### 9.2 预请求脚本

**自动设置Authorization头**

```javascript
// 预请求脚本：自动添加Authorization头
if (pm.environment.get("accessToken")) {
    pm.request.headers.add({
        key: "Authorization",
        value: "Bearer " + pm.environment.get("accessToken")
    });
}

// 添加请求ID
pm.request.headers.add({
    key: "X-Request-ID",
    value: pm.variables.replaceIn("{{$randomUUID}}")
});
```

### 9.3 测试脚本

**通用测试脚本**

```javascript
// 测试脚本：验证响应格式
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has success field", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('success');
});

pm.test("Response has timestamp", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('timestamp');
});

pm.test("Response has requestId", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('requestId');
});
```

**登录测试脚本**

```javascript
// 登录成功后保存Token
pm.test("Login successful", function () {
    pm.response.to.have.status(200);
    const jsonData = pm.response.json();
    
    pm.expect(jsonData.success).to.be.true;
    pm.expect(jsonData.data).to.have.property('accessToken');
    pm.expect(jsonData.data).to.have.property('user');
    
    // 保存Token到环境变量
    pm.environment.set("accessToken", jsonData.data.accessToken);
    pm.environment.set("userId", jsonData.data.user.userId);
    
    console.log("Access token saved:", jsonData.data.accessToken);
});
```

**创建课程测试脚本**

```javascript
// 创建课程成功后保存课程ID
pm.test("Course created successfully", function () {
    pm.response.to.have.status(200);
    const jsonData = pm.response.json();
    
    pm.expect(jsonData.success).to.be.true;
    pm.expect(jsonData.data).to.have.property('courseId');
    pm.expect(jsonData.data).to.have.property('courseCode');
    
    // 保存课程ID到环境变量
    pm.environment.set("courseId", jsonData.data.courseId);
    
    console.log("Course ID saved:", jsonData.data.courseId);
});
```

### 9.4 完整测试流程

**测试集合结构**

```
Wanli Academy API Tests
├── 01. Authentication
│   ├── Register User
│   ├── Login User
│   ├── Get User Info
│   └── Logout User
├── 02. Course Management
│   ├── Create Course
│   ├── Get Course List
│   ├── Get Course Detail
│   ├── Update Course
│   └── Delete Course
├── 03. Lesson Management
│   ├── Create Lesson
│   ├── Get Lesson List
│   ├── Get Lesson Detail
│   ├── Update Lesson
│   └── Delete Lesson
├── 04. System Monitoring
│   ├── Health Check
│   └── System Info
└── 05. Error Scenarios
    ├── Invalid Token
    ├── Missing Parameters
    ├── Resource Not Found
    └── Permission Denied
```

**自动化测试运行器**

```javascript
// Collection Runner脚本
const newman = require('newman');

newman.run({
    collection: 'Wanli-Academy-API-Tests.postman_collection.json',
    environment: 'Development.postman_environment.json',
    reporters: ['cli', 'html'],
    reporter: {
        html: {
            export: './test-results/api-test-report.html'
        }
    }
}, function (err) {
    if (err) { throw err; }
    console.log('API测试完成！');
});
```

## 10. 性能基准

### 10.1 响应时间要求

| 接口类型 | 平均响应时间  | 95%响应时间 | 最大响应时间   |
| :--- | :------ | :------ | :------- |
| 认证接口 | < 200ms | < 500ms | < 1000ms |
| 查询接口 | < 100ms | < 300ms | < 500ms  |
| 创建接口 | < 300ms | < 800ms | < 1500ms |
| 更新接口 | < 200ms | < 600ms | < 1000ms |
| 删除接口 | < 150ms | < 400ms | < 800ms  |

### 10.2 并发性能要求

| 场景   | 并发用户数 | TPS要求 | 成功率要求 |
| :--- | :---- | :---- | :---- |
| 正常业务 | 100   | > 50  | > 99% |
| 高峰业务 | 500   | > 200 | > 95% |
| 压力测试 | 1000  | > 300 | > 90% |

### 10.3 资源使用限制

| 资源类型     | 限制值   | 监控阈值 |
| :------- | :---- | :--- |
| CPU使用率   | < 80% | 70%  |
| 内存使用率    | < 85% | 75%  |
| 数据库连接数   | < 80% | 70%  |
| Redis连接数 | < 90% | 80%  |

## 11. 验收标准

### 11.1 功能验收标准

* 所有API接口按照规范实现
* 认证授权功能正常工作
* 课程和课时CRUD操作完整
* 错误处理和异常响应正确
* 数据验证和安全检查有效
* 系统监控接口可用

### 11.2 性能验收标准

* 接口响应时间满足要求
* 并发性能达到指标
* 资源使用在限制范围内
* 数据库查询优化有效
* 缓存策略正确实施

### 11.3 质量验收标准

* API文档完整准确
* Postman测试集合可用
* 单元测试覆盖率 > 80%
* 集成测试通过率 > 95%
* 代码质量检查通过
* 安全扫描无高危漏洞

## 12. 附录

### 12.1 相关文档

* 《SP1任务说明书.md》
* 《SP1数据库实体设计文档.md》
* 《SP1 Spring Security配置文档.md》
* 《SP1业务逻辑设计文档.md》
* 《SP1测试策略文档.md》

### 12.2 开发工具

* **API文档**: Swagger UI
* **接口测试**: Postman, Newman
* **性能测试**: JMeter, Artillery
* **监控工具**: Micrometer, Actuator

### 12.3 最佳实践

* 遵循RESTful API设计原则
* 使用统一的响应格式
* 实施完整的错误处理
* 进行充分的输入验证
* 实现适当的缓存策略
* 添加详细的日志记录

### 12.4 版本历史

| 版本     | 日期         | 修改内容                | 修改人  |
| :----- | :--------- | :------------------ | :--- |
| v1.0.0 | 2025-01-15 | 初始版本，定义SP1阶段API接口规范 | 开发团队 |

