# 万里教育后台管理系统 API 接口文档

## 概述

万里教育后台管理系统提供完整的RESTful API接口，支持用户管理、课程管理、机构管理等核心功能。本文档详细描述了所有可用的API接口、请求参数、响应格式以及环境配置信息。

## 环境配置

### Staging 环境
- **基础URL**: `https://wanli-backend-staging-staging.up.railway.app/api`
- **数据库**: Railway PostgreSQL
- **部署平台**: Railway
- **JWT过期时间**: 1小时（访问令牌）/ 2小时（刷新令牌）
- **日志级别**: INFO
- **健康检查**: `/api/health`

### Production 环境
- **基础URL**: `https://your-production-domain.com/api`
- **数据库**: 生产环境PostgreSQL
- **部署平台**: Railway或其他云平台
- **JWT过期时间**: 1小时（访问令牌）/ 2小时（刷新令牌）
- **日志级别**: WARN
- **健康检查**: `/api/health`

### Development 环境
- **基础URL**: `http://localhost:8080/api`
- **数据库**: 本地PostgreSQL
- **JWT过期时间**: 1小时（访问令牌）/ 2小时（刷新令牌）
- **日志级别**: DEBUG
- **健康检查**: `/api/health`

## 认证机制

系统采用JWT（JSON Web Token）进行身份认证：

1. **获取令牌**: 通过登录接口获取访问令牌
2. **使用令牌**: 在请求头中添加 `Authorization: Bearer <token>`
3. **令牌刷新**: 访问令牌过期后使用刷新令牌获取新的访问令牌
4. **令牌过期**: 访问令牌有效期1小时，刷新令牌有效期2小时

## 统一响应格式

所有API接口都遵循统一的响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 响应字段说明
- `code`: 响应状态码，200表示成功
- `message`: 响应消息描述
- `data`: 响应数据，可能为对象、数组或null

## 用户角色和状态枚举

### 用户角色（UserRole）
- `ADMIN`: 管理员，拥有所有权限
- `TEACHER`: 教师，可以管理课程和查看学生信息
- `STUDENT`: 学生，只能查看自己的信息

### 用户状态（UserStatus）
- `ACTIVE`: 活跃状态，可以正常使用系统
- `INACTIVE`: 非活跃状态，暂时无法使用系统
- `SUSPENDED`: 暂停状态，账户被暂停使用

### 课程状态（CourseStatus）
- `ACTIVE`: 活跃状态，课程正在进行
- `INACTIVE`: 非活跃状态，课程暂停
- `COMPLETED`: 已完成状态，课程已结束

### 机构状态（InstitutionStatus）
- `ACTIVE`: 活跃状态，机构正常运营
- `INACTIVE`: 非活跃状态，机构暂停运营
- `SUSPENDED`: 暂停状态，机构被暂停

## 用户信息响应模型

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "admin",
  "email": "admin@wanli.edu",
  "fullName": "系统管理员",
  "phone": "13800138000",
  "role": "ADMIN",
  "status": "ACTIVE",
  "institutionId": "550e8400-e29b-41d4-a716-446655440001",
  "createdAt": "2024-01-15T10:30:00.000Z",
  "updatedAt": "2024-01-15T10:30:00.000Z"
}
```

## API 接口详情

### 1. 认证相关接口

#### 用户注册
- **接口地址**: `POST /api/auth/register`
- **接口描述**: 注册新用户账户
- **权限要求**: 无需认证
- **请求参数**:
```json
{
  "username": "testuser",
  "password": "password123",
  "email": "testuser@example.com",
  "fullName": "测试用户",
  "phone": "13800138000",
  "role": "STUDENT",
  "institutionId": "550e8400-e29b-41d4-a716-446655440001"
}
```
- **参数说明**:
  - `username`: 用户名，必填，3-50字符，唯一
  - `password`: 密码，必填，6-100字符
  - `email`: 邮箱，必填，标准邮箱格式，唯一
  - `fullName`: 真实姓名，必填，最大100字符
  - `phone`: 手机号，选填，11位数字
  - `role`: 用户角色，必填，枚举值：ADMIN、TEACHER、STUDENT
  - `institutionId`: 所属机构ID，选填，UUID格式
- **成功响应**:
```json
{
  "code": 200,
  "message": "用户注册成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "testuser",
    "email": "testuser@example.com",
    "fullName": "测试用户",
    "phone": "13800138000",
    "role": "STUDENT",
    "status": "ACTIVE",
    "institutionId": "550e8400-e29b-41d4-a716-446655440001",
    "createdAt": "2024-01-15T10:30:00.000Z",
    "updatedAt": "2024-01-15T10:30:00.000Z"
  }
}
```

#### 用户登录
- **接口地址**: `POST /api/auth/login`
- **接口描述**: 用户登录获取访问令牌
- **权限要求**: 无需认证
- **请求参数**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```
- **参数说明**:
  - `username`: 用户名，必填
  - `password`: 密码，必填
- **成功响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "username": "admin",
      "email": "admin@wanli.edu",
      "fullName": "系统管理员",
      "role": "ADMIN",
      "status": "ACTIVE"
    }
  }
}
```

#### 获取当前用户信息
- **接口地址**: `GET /api/auth/me`
- **接口描述**: 获取当前登录用户的详细信息
- **权限要求**: 需要认证
- **请求头**: `Authorization: Bearer <token>`
- **成功响应**:
```json
{
  "code": 200,
  "message": "获取用户信息成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "admin",
    "email": "admin@wanli.edu",
    "fullName": "系统管理员",
    "phone": "13800138000",
    "role": "ADMIN",
    "status": "ACTIVE",
    "institutionId": "550e8400-e29b-41d4-a716-446655440001",
    "createdAt": "2024-01-15T10:30:00.000Z",
    "updatedAt": "2024-01-15T10:30:00.000Z"
  }
}
```

#### 用户登出
- **接口地址**: `POST /api/auth/logout`
- **接口描述**: 用户登出，使当前令牌失效
- **权限要求**: 需要认证
- **请求头**: `Authorization: Bearer <token>`
- **成功响应**:
```json
{
  "code": 200,
  "message": "登出成功",
  "data": null
}
```

### 2. 用户管理接口

#### 创建用户
- **接口地址**: `POST /api/users`
- **接口描述**: 创建新用户（管理员功能）
- **权限要求**: 需要认证，ADMIN角色
- **请求参数**: 同用户注册接口
- **成功响应**: 同用户注册接口

#### 根据ID获取用户
- **接口地址**: `GET /api/users/{id}`
- **接口描述**: 根据用户ID获取用户详细信息
- **权限要求**: 需要认证
- **路径参数**:
  - `id`: 用户ID，UUID格式
- **成功响应**:
```json
{
  "code": 200,
  "message": "获取用户信息成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "testuser",
    "email": "testuser@example.com",
    "fullName": "测试用户",
    "phone": "13800138000",
    "role": "STUDENT",
    "status": "ACTIVE",
    "institutionId": "550e8400-e29b-41d4-a716-446655440001",
    "createdAt": "2024-01-15T10:30:00.000Z",
    "updatedAt": "2024-01-15T10:30:00.000Z"
  }
}
```

#### 获取所有用户
- **接口地址**: `GET /api/users`
- **接口描述**: 分页获取用户列表
- **权限要求**: 需要认证，ADMIN角色
- **请求参数**:
  - `page`: 页码，默认0
  - `size`: 页大小，默认10，最大100
  - `role`: 角色过滤，选填，枚举值：ADMIN、TEACHER、STUDENT
  - `status`: 状态过滤，选填，枚举值：ACTIVE、INACTIVE、SUSPENDED
- **成功响应**:
```json
{
  "code": 200,
  "message": "获取用户列表成功",
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "username": "admin",
        "email": "admin@wanli.edu",
        "fullName": "系统管理员",
        "phone": "13800138000",
        "role": "ADMIN",
        "status": "ACTIVE",
        "institutionId": "550e8400-e29b-41d4-a716-446655440001",
        "createdAt": "2024-01-15T10:30:00.000Z",
        "updatedAt": "2024-01-15T10:30:00.000Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

#### 更新用户信息
- **接口地址**: `PUT /api/users/{id}`
- **接口描述**: 更新用户信息
- **权限要求**: 需要认证，ADMIN角色或用户本人
- **路径参数**:
  - `id`: 用户ID，UUID格式
- **请求参数**:
```json
{
  "email": "newemail@example.com",
  "fullName": "新的真实姓名",
  "phone": "13900139000",
  "institutionId": "550e8400-e29b-41d4-a716-446655440002"
}
```
- **参数说明**:
  - `email`: 邮箱，选填，标准邮箱格式
  - `fullName`: 真实姓名，选填，最大100字符
  - `phone`: 手机号，选填，11位数字
  - `institutionId`: 所属机构ID，选填，UUID格式
- **成功响应**:
```json
{
  "code": 200,
  "message": "用户信息更新成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "testuser",
    "email": "newemail@example.com",
    "fullName": "新的真实姓名",
    "phone": "13900139000",
    "role": "STUDENT",
    "status": "ACTIVE",
    "institutionId": "550e8400-e29b-41d4-a716-446655440002",
    "createdAt": "2024-01-15T10:30:00.000Z",
    "updatedAt": "2024-01-15T11:00:00.000Z"
  }
}
```

#### 删除用户
- **接口地址**: `DELETE /api/users/{id}`
- **接口描述**: 删除用户（软删除）
- **权限要求**: 需要认证，ADMIN角色
- **路径参数**:
  - `id`: 用户ID，UUID格式
- **成功响应**:
```json
{
  "code": 200,
  "message": "用户删除成功",
  "data": null
}
```

#### 根据用户名获取用户
- **接口地址**: `GET /api/users/username/{username}`
- **接口描述**: 根据用户名获取用户信息
- **权限要求**: 需要认证，ADMIN角色
- **路径参数**:
  - `username`: 用户名
- **成功响应**: 同根据ID获取用户接口

#### 检查用户名是否存在
- **接口地址**: `GET /api/users/check-username/{username}`
- **接口描述**: 检查用户名是否已被使用
- **权限要求**: 无需认证
- **路径参数**:
  - `username`: 用户名
- **成功响应**:
```json
{
  "code": 200,
  "message": "用户名检查完成",
  "data": {
    "exists": true,
    "username": "admin"
  }
}
```

#### 检查邮箱是否存在
- **接口地址**: `GET /api/users/check-email/{email}`
- **接口描述**: 检查邮箱是否已被使用
- **权限要求**: 无需认证
- **路径参数**:
  - `email`: 邮箱地址
- **成功响应**:
```json
{
  "code": 200,
  "message": "邮箱检查完成",
  "data": {
    "exists": false,
    "email": "test@example.com"
  }
}
```

### 3. 系统健康检查接口

#### 健康检查
- **接口地址**: `GET /api/health`
- **接口描述**: 检查系统运行状态
- **权限要求**: 无需认证
- **成功响应**:
```json
{
  "code": 200,
  "message": "系统运行正常",
  "data": {
    "status": "UP",
    "application": "wanli-backend",
    "profile": "staging",
    "timestamp": "2024-01-15T10:30:00.000Z",
    "version": "1.0.0"
  }
}
```

#### 系统信息
- **接口地址**: `GET /api/health/info`
- **接口描述**: 获取系统详细信息
- **权限要求**: 需要认证，ADMIN角色
- **成功响应**:
```json
{
  "code": 200,
  "message": "获取系统信息成功",
  "data": {
    "application": "wanli-backend",
    "version": "1.0.0",
    "environment": "staging",
    "javaVersion": "17.0.2",
    "springBootVersion": "3.2.1",
    "buildTime": "2024-01-15T08:00:00.000Z",
    "uptime": "2h 30m 15s"
  }
}
```

### 4. 课程管理接口

#### 创建课程
- **接口地址**: `POST /api/courses`
- **接口描述**: 创建新课程
- **权限要求**: 需要认证，ADMIN或TEACHER角色
- **请求参数**:
```json
{
  "code": "MATH001",
  "title": "小学数学基础",
  "description": "适合一年级学生的数学基础课程",
  "grade": "GRADE_1",
  "subject": "MATH",
  "institutionId": "550e8400-e29b-41d4-a716-446655440001"
}
```
- **参数说明**:
  - `code`: 课程编码，必填，最大20字符，唯一
  - `title`: 课程标题，必填，最大100字符
  - `description`: 课程描述，选填，最大500字符
  - `grade`: 年级等级，必填，枚举值：GRADE_1到GRADE_6
  - `subject`: 学科，必填，枚举值：CHINESE、MATH、ENGLISH
  - `institutionId`: 所属机构ID，必填，UUID格式
- **成功响应**:
```json
{
  "code": 200,
  "message": "课程创建成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "code": "MATH001",
    "title": "小学数学基础",
    "description": "适合一年级学生的数学基础课程",
    "grade": "GRADE_1",
    "subject": "MATH",
    "status": "ACTIVE",
    "institutionId": "550e8400-e29b-41d4-a716-446655440001",
    "createdAt": "2024-01-15T10:30:00.000Z",
    "updatedAt": "2024-01-15T10:30:00.000Z",
    "createdBy": "admin"
  }
}
```

#### 获取课程列表
- **接口地址**: `GET /api/courses`
- **接口描述**: 分页获取课程列表
- **权限要求**: 需要认证
- **请求参数**:
  - `page`: 页码，默认0
  - `size`: 页大小，默认10，最大100
  - `grade`: 年级过滤，选填，枚举值：GRADE_1到GRADE_6
  - `subject`: 学科过滤，选填，枚举值：CHINESE、MATH、ENGLISH
  - `status`: 状态过滤，选填，枚举值：ACTIVE、INACTIVE、COMPLETED
  - `institutionId`: 机构ID过滤，选填，UUID格式
- **成功响应**:
```json
{
  "code": 200,
  "message": "获取课程列表成功",
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "code": "MATH001",
        "title": "小学数学基础",
        "description": "适合一年级学生的数学基础课程",
        "grade": "GRADE_1",
        "subject": "MATH",
        "status": "ACTIVE",
        "institutionId": "550e8400-e29b-41d4-a716-446655440001",
        "createdAt": "2024-01-15T10:30:00.000Z",
        "updatedAt": "2024-01-15T10:30:00.000Z",
        "createdBy": "admin"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

#### 获取课程详情
- **接口地址**: `GET /api/courses/{id}`
- **接口描述**: 根据ID获取课程详情
- **权限要求**: 需要认证
- **路径参数**:
  - `id`: 课程ID，UUID格式
- **成功响应**: 同创建课程响应

#### 更新课程信息
- **接口地址**: `PUT /api/courses/{id}`
- **接口描述**: 更新课程信息
- **权限要求**: 需要认证，ADMIN或TEACHER角色
- **路径参数**:
  - `id`: 课程ID，UUID格式
- **请求参数**: 同创建课程
- **成功响应**: 同创建课程响应

#### 删除课程
- **接口地址**: `DELETE /api/courses/{id}`
- **接口描述**: 删除课程（软删除）
- **权限要求**: 需要认证，ADMIN角色
- **路径参数**:
  - `id`: 课程ID，UUID格式
- **成功响应**:
```json
{
  "code": 200,
  "message": "课程删除成功",
  "data": null
}
```

#### 切换课程状态
- **接口地址**: `PATCH /api/courses/{id}/status`
- **接口描述**: 切换课程状态（激活/停用）
- **权限要求**: 需要认证，ADMIN或TEACHER角色
- **路径参数**:
  - `id`: 课程ID，UUID格式
- **请求参数**:
```json
{
  "status": "INACTIVE"
}
```
- **参数说明**:
  - `status`: 课程状态，必填，枚举值：ACTIVE、INACTIVE、COMPLETED
- **成功响应**:
```json
{
  "code": 200,
  "message": "课程状态更新成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "status": "INACTIVE"
  }
}
```

### 5. 机构管理接口

#### 创建机构
- **接口地址**: `POST /api/institutions`
- **接口描述**: 创建新机构
- **权限要求**: 需要认证，ADMIN角色
- **请求参数**:
```json
{
  "name": "万里教育机构",
  "description": "专注于小学教育的优质机构",
  "contactEmail": "contact@wanli.edu",
  "contactPhone": "13800138000",
  "address": "北京市朝阳区教育大街123号"
}
```
- **参数说明**:
  - `name`: 机构名称，必填，最大100字符，唯一
  - `description`: 机构描述，选填，最大500字符
  - `contactEmail`: 联系邮箱，必填，标准邮箱格式
  - `contactPhone`: 联系电话，必填，11位数字
  - `address`: 机构地址，选填，最大200字符
- **成功响应**:
```json
{
  "code": 200,
  "message": "机构创建成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "万里教育机构",
    "description": "专注于小学教育的优质机构",
    "contactEmail": "contact@wanli.edu",
    "contactPhone": "13800138000",
    "address": "北京市朝阳区教育大街123号",
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00.000Z",
    "updatedAt": "2024-01-15T10:30:00.000Z",
    "createdBy": "admin"
  }
}
```

#### 获取机构详情
- **接口地址**: `GET /api/institutions/{id}`
- **接口描述**: 根据ID获取机构详情
- **权限要求**: 需要认证
- **路径参数**:
  - `id`: 机构ID，UUID格式
- **成功响应**: 同创建机构响应

#### 获取机构列表
- **接口地址**: `GET /api/institutions`
- **接口描述**: 分页获取机构列表
- **权限要求**: 需要认证
- **请求参数**:
  - `page`: 页码，默认0
  - `size`: 页大小，默认10，最大100
  - `status`: 状态过滤，选填，枚举值：ACTIVE、INACTIVE、SUSPENDED
- **成功响应**:
```json
{
  "code": 200,
  "message": "获取机构列表成功",
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "name": "万里教育机构",
        "description": "专注于小学教育的优质机构",
        "contactEmail": "contact@wanli.edu",
        "contactPhone": "13800138000",
        "address": "北京市朝阳区教育大街123号",
        "status": "ACTIVE",
        "createdAt": "2024-01-15T10:30:00.000Z",
        "updatedAt": "2024-01-15T10:30:00.000Z",
        "createdBy": "admin"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

#### 更新机构信息
- **接口地址**: `PUT /api/institutions/{id}`
- **接口描述**: 更新机构信息
- **权限要求**: 需要认证，ADMIN角色
- **路径参数**:
  - `id`: 机构ID，UUID格式
- **请求参数**: 同创建机构
- **成功响应**: 同创建机构响应

#### 更新机构状态
- **接口地址**: `PATCH /api/institutions/{id}/status`
- **接口描述**: 更新机构状态
- **权限要求**: 需要认证，ADMIN角色
- **路径参数**:
  - `id`: 机构ID，UUID格式
- **请求参数**:
```json
{
  "status": "INACTIVE"
}
```
- **参数说明**:
  - `status`: 机构状态，必填，枚举值：ACTIVE、INACTIVE、SUSPENDED
- **成功响应**:
```json
{
  "code": 200,
  "message": "机构状态更新成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "status": "INACTIVE"
  }
}
```

#### 删除机构
- **接口地址**: `DELETE /api/institutions/{id}`
- **接口描述**: 删除机构（软删除）
- **权限要求**: 需要认证，ADMIN角色
- **路径参数**:
  - `id`: 机构ID，UUID格式
- **成功响应**:
```json
{
  "code": 200,
  "message": "机构删除成功",
  "data": null
}
```

#### 获取活跃机构列表
- **接口地址**: `GET /api/institutions/active`
- **接口描述**: 获取所有活跃状态的机构列表
- **权限要求**: 需要认证
- **成功响应**:
```json
{
  "code": 200,
  "message": "获取活跃机构列表成功",
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "万里教育机构",
      "description": "专注于小学教育的优质机构",
      "contactEmail": "contact@wanli.edu",
      "contactPhone": "13800138000",
      "address": "北京市朝阳区教育大街123号",
      "status": "ACTIVE",
      "createdAt": "2024-01-15T10:30:00.000Z",
      "updatedAt": "2024-01-15T10:30:00.000Z",
      "createdBy": "admin"
    }
  ]
}
```

#### 获取机构统计信息
- **接口地址**: `GET /api/institutions/statistics`
- **接口描述**: 获取机构统计信息
- **权限要求**: 需要认证，ADMIN角色
- **成功响应**:
```json
{
  "code": 200,
  "message": "获取机构统计信息成功",
  "data": {
    "totalInstitutions": 10,
    "activeInstitutions": 8,
    "inactiveInstitutions": 1,
    "suspendedInstitutions": 1
  }
}
```

## 错误码说明

### 通用错误码
- `400`: 请求参数错误
- `401`: 未认证或令牌无效
- `403`: 权限不足
- `404`: 资源不存在
- `409`: 资源冲突（如用户名已存在）
- `500`: 服务器内部错误

### 业务错误码
- `1001`: 用户名已存在
- `1002`: 邮箱已存在
- `1003`: 用户不存在
- `1004`: 密码错误
- `1005`: 用户状态异常
- `2001`: 课程编码已存在
- `2002`: 课程不存在
- `2003`: 课程状态异常
- `3001`: 机构名称已存在
- `3002`: 机构不存在
- `3003`: 机构状态异常

## 常见错误响应示例

### 参数验证错误
```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": {
    "errors": [
      {
        "field": "username",
        "message": "用户名长度必须在3-50个字符之间"
      },
      {
        "field": "email",
        "message": "邮箱格式不正确"
      }
    ]
  }
}
```

### 认证失败
```json
{
  "code": 401,
  "message": "认证失败，请重新登录",
  "data": null
}
```

### 权限不足
```json
{
  "code": 403,
  "message": "权限不足，无法访问该资源",
  "data": null
}
```

### 资源不存在
```json
{
  "code": 404,
  "message": "用户不存在",
  "data": null
}
```

### 业务逻辑错误
```json
{
  "code": 1001,
  "message": "用户名已存在",
  "data": {
    "username": "admin"
  }
}
```

## API 使用示例

### 用户注册和登录流程

1. **注册新用户**
```bash
curl -X POST https://wanli-backend-staging-staging.up.railway.app/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student001",
    "password": "password123",
    "email": "student001@example.com",
    "fullName": "张三",
    "phone": "13800138001",
    "role": "STUDENT"
  }'
```

2. **用户登录**
```bash
curl -X POST https://wanli-backend-staging-staging.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student001",
    "password": "password123"
  }'
```

3. **获取用户信息**
```bash
curl -X GET https://wanli-backend-staging-staging.up.railway.app/api/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

4. **用户登出**
```bash
curl -X POST https://wanli-backend-staging-staging.up.railway.app/api/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 管理员操作示例

1. **获取所有用户**
```bash
curl -X GET "https://wanli-backend-staging-staging.up.railway.app/api/users?page=0&size=10" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

2. **创建课程**
```bash
curl -X POST https://wanli-backend-staging-staging.up.railway.app/api/courses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "code": "MATH001",
    "title": "小学数学基础",
    "description": "适合一年级学生的数学基础课程",
    "grade": "GRADE_1",
    "subject": "MATH",
    "institutionId": "550e8400-e29b-41d4-a716-446655440001"
  }'
```

3. **创建机构**
```bash
curl -X POST https://wanli-backend-staging-staging.up.railway.app/api/institutions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "name": "万里教育机构",
    "description": "专注于小学教育的优质机构",
    "contactEmail": "contact@wanli.edu",
    "contactPhone": "13800138000",
    "address": "北京市朝阳区教育大街123号"
  }'
```

## 环境接入配置

### Staging 环境接入

**基础配置**:
- 基础URL: `https://wanli-backend-staging-staging.up.railway.app/api`
- 数据库: Railway PostgreSQL (自动配置)
- 认证方式: JWT Bearer Token
- 内容类型: `application/json`

**环境变量**:
```bash
# 数据库配置（Railway自动注入）
SPRING_DATASOURCE_URL=jdbc:postgresql://...
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=...

# JWT配置
JWT_SECRET=stagingSecretKeyForWanliEducationBackendSystem2024
JWT_ACCESS_TOKEN_EXPIRATION=3600000
JWT_REFRESH_TOKEN_EXPIRATION=7200000

# 应用配置
SPRING_PROFILES_ACTIVE=staging
PORT=8080
APP_VERSION=1.0.0
```

**健康检查**:
- 端点: `GET /api/health`
- 预期响应: HTTP 200
- 检查间隔: 30秒

### Production 环境接入

**基础配置**:
- 基础URL: `https://your-production-domain.com/api`
- 数据库: 生产环境PostgreSQL
- 认证方式: JWT Bearer Token
- 内容类型: `application/json`
- SSL/TLS: 强制HTTPS

**环境变量**:
```bash
# 数据库配置
SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db-host:5432/wanli_backend_prod
SPRING_DATASOURCE_USERNAME=prod_user
SPRING_DATASOURCE_PASSWORD=secure_prod_password

# JWT配置（生产环境使用更强的密钥）
JWT_SECRET=productionSecretKeyForWanliEducationBackendSystem2024WithHighSecurity
JWT_ACCESS_TOKEN_EXPIRATION=3600000
JWT_REFRESH_TOKEN_EXPIRATION=7200000

# 应用配置
SPRING_PROFILES_ACTIVE=production
PORT=8080
APP_VERSION=1.0.0
```

**安全配置**:
- 启用HTTPS重定向
- 配置CORS策略
- 启用请求限流
- 配置日志审计

**监控配置**:
- 健康检查: `GET /api/health`
- 系统信息: `GET /api/health/info` (需要管理员权限)
- 日志级别: WARN
- 错误报告: 集成错误监控服务

## 注意事项

### 安全性
- 所有需要认证的接口都需要在请求头中携带有效的JWT令牌
- JWT令牌格式：`Authorization: Bearer <token>`
- 令牌过期时间为1小时（访问令牌）/ 2小时（刷新令牌）
- 生产环境强制使用HTTPS协议
- 敏感操作需要管理员权限验证

### 数据格式
- 所有时间字段使用ISO 8601格式：`YYYY-MM-DDTHH:mm:ss.SSSZ`
- 所有ID字段使用UUID格式
- 分页查询默认页大小为10，最大页大小为100
- 请求和响应均使用UTF-8编码

### 枚举值说明
- **年级等级**: GRADE_1(一年级), GRADE_2(二年级), GRADE_3(三年级), GRADE_4(四年级), GRADE_5(五年级), GRADE_6(六年级)
- **学科**: CHINESE(语文), MATH(数学), ENGLISH(英语)
- **用户角色**: ADMIN(管理员), TEACHER(教师), STUDENT(学生)
- **状态枚举**: ACTIVE(活跃), INACTIVE(非活跃), SUSPENDED(暂停)

### 限制说明
- 用户名长度：3-50个字符，只能包含字母、数字和下划线
- 密码长度：6-100个字符
- 邮箱格式必须符合标准邮箱格式
- 手机号格式：11位数字
- 课程编码长度：最大20字符，必须唯一
- 课程标题长度：最大100字符
- 机构名称长度：最大100字符，必须唯一

### 最佳实践
- 建议在生产环境中使用HTTPS协议
- 建议实现客户端令牌自动刷新机制
- 建议对敏感操作进行二次确认
- 建议实现适当的错误重试机制
- 建议在创建课程时指定所属机构
- 建议定期检查机构状态，及时处理异常机构
- 建议实现客户端请求缓存以提高性能
- 建议使用连接池管理数据库连接

### 版本控制
- API版本通过URL路径管理（当前版本：v1，路径：/api）
- 向后兼容性：新版本发布时保持向后兼容
- 废弃通知：废弃的接口会提前通知并保持6个月的兼容期

---

**文档版本：** 2.0.0  
**最后更新：** 2024年1月20日  
**维护团队：** 万里教育技术团队  
**联系方式：** 如有问题请联系开发团队或提交GitHub Issue