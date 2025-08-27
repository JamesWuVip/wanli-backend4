# 万里后端系统 API 接口文档

## 1. 概述

本文档描述了万里后端系统的所有API接口，包括用户认证、用户管理和系统健康检查等功能模块。

**基础信息：**
- 基础URL: `http://localhost:8080`
- 内容类型: `application/json`
- 字符编码: `UTF-8`

## 2. 认证接口

### 2.1 用户注册

**接口描述：** 新用户注册账号

**请求信息：**
- **方法：** POST
- **路径：** `/api/auth/register`
- **Content-Type：** `application/json`

**请求参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| username | string | 是 | 用户名，3-20个字符 | "testuser" |
| password | string | 是 | 密码，至少8位 | "Test123456" |
| confirmPassword | string | 是 | 确认密码，必须与password一致 | "Test123456" |
| email | string | 是 | 邮箱地址 | "test@example.com" |
| fullName | string | 是 | 用户真实姓名 | "测试用户" |

**请求示例：**
```json
{
  "username": "testuser",
  "password": "Test123456",
  "confirmPassword": "Test123456",
  "email": "test@example.com",
  "fullName": "测试用户"
}
```

**响应格式：**

成功响应 (201 Created):
```json
{
  "success": true,
  "message": "用户注册成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "测试用户",
    "createdAt": "2025-01-28T00:00:00Z"
  }
}
```

失败响应 (400 Bad Request):
```json
{
  "success": false,
  "message": "用户名已存在",
  "data": null
}
```

### 2.2 用户登录

**接口描述：** 用户登录获取访问令牌

**请求信息：**
- **方法：** POST
- **路径：** `/api/auth/login`
- **Content-Type：** `application/json`

**请求参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| username | string | 是 | 用户名或邮箱 | "testuser" |
| password | string | 是 | 用户密码 | "Test123456" |

**请求示例：**
```json
{
  "username": "testuser",
  "password": "Test123456"
}
```

**响应格式：**

成功响应 (200 OK):
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "fullName": "测试用户"
    }
  }
}
```

失败响应 (401 Unauthorized):
```json
{
  "success": false,
  "message": "用户名或密码错误",
  "data": null
}
```

### 2.3 获取当前用户信息

**接口描述：** 获取当前登录用户的详细信息

**请求信息：**
- **方法：** GET
- **路径：** `/api/auth/me`
- **认证：** 需要Bearer Token

**请求头：**
```
Authorization: Bearer {token}
```

**响应格式：**

成功响应 (200 OK):
```json
{
  "success": true,
  "message": "获取用户信息成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "测试用户",
    "createdAt": "2025-01-28T00:00:00Z",
    "updatedAt": "2025-01-28T00:00:00Z"
  }
}
```

失败响应 (401 Unauthorized):
```json
{
  "success": false,
  "message": "未授权访问",
  "data": null
}
```

### 2.4 用户登出

**接口描述：** 用户登出，使当前token失效

**请求信息：**
- **方法：** POST
- **路径：** `/api/auth/logout`
- **认证：** 需要Bearer Token

**请求头：**
```
Authorization: Bearer {token}
```

**响应格式：**

成功响应 (200 OK):
```json
{
  "success": true,
  "message": "登出成功",
  "data": null
}
```

## 3. 系统接口

### 3.1 健康检查

**接口描述：** 检查系统运行状态

**请求信息：**
- **方法：** GET
- **路径：** `/api/health`
- **认证：** 无需认证

**响应格式：**

成功响应 (200 OK):
```json
{
  "success": true,
  "message": "系统运行正常",
  "data": {
    "status": "UP",
    "timestamp": "2025-01-28T00:00:00Z",
    "version": "1.0.0"
  }
}
```

## 4. 错误码说明

| HTTP状态码 | 错误码 | 描述 |
|------------|--------|------|
| 200 | 0 | 请求成功 |
| 201 | 0 | 创建成功 |
| 400 | 1001 | 请求参数错误 |
| 401 | 1002 | 未授权访问 |
| 403 | 1003 | 禁止访问 |
| 404 | 1004 | 资源不存在 |
| 409 | 1005 | 资源冲突 |
| 500 | 2001 | 服务器内部错误 |

## 5. 通用响应格式

所有API接口都遵循统一的响应格式：

```json
{
  "success": boolean,
  "message": "string",
  "data": object | null
}
```

**字段说明：**
- `success`: 请求是否成功的布尔值
- `message`: 响应消息，用于描述请求结果
- `data`: 响应数据，成功时包含具体数据，失败时为null

## 6. 认证说明

系统使用JWT (JSON Web Token) 进行用户认证：

1. 用户通过登录接口获取token
2. 在需要认证的接口请求头中添加：`Authorization: Bearer {token}`
3. Token有效期为1小时，过期后需要重新登录
4. 登出后token立即失效

## 7. 接口测试

### 7.1 测试环境
- 开发环境：`http://localhost:8080`
- 测试工具：Postman、curl

### 7.2 测试流程
1. 调用用户注册接口创建测试账号
2. 调用用户登录接口获取token
3. 使用token调用需要认证的接口
4. 调用登出接口结束会话

### 7.3 测试用例

**用户注册测试：**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456",
    "confirmPassword": "Test123456",
    "email": "test@example.com",
    "fullName": "测试用户"
  }'
```

**用户登录测试：**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456"
  }'
```

**获取用户信息测试：**
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer {your_token_here}"
```

**健康检查测试：**
```bash
curl -X GET http://localhost:8080/api/health
```

---

**文档版本：** v1.0.0  
**最后更新：** 2025-01-28  
**维护者：** 万里后端开发团队