# 万里后端系统 API 接口文档

## 1. 概述

本文档描述了万里后端系统的所有API接口，包括用户认证、用户管理和系统健康检查等功能模块。

**基础信息：**
- 项目名称: 万里后端系统
- 版本: v1.0.0
- 认证方式: JWT Token
- 内容类型: `application/json`
- 字符编码: `UTF-8`

**环境配置：**

| 环境 | 基础URL | 描述 |
|------|---------|------|
| 开发环境 | `http://localhost:8080` | 本地开发环境 |
| 测试环境 | `https://wanli-backend-staging.railway.app` | Railway Staging 环境 |
| 生产环境 | `https://wanli-backend.railway.app` | Railway Production 环境 |

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

## 3. 用户管理接口

### 3.1 更新用户信息

**接口描述：** 更新当前用户的个人信息

**请求信息：**
- **方法：** PUT
- **路径：** `/api/auth/profile`
- **Content-Type：** `application/json`
- **认证：** 需要Bearer Token

**请求头：**
```
Authorization: Bearer {token}
```

**请求参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| fullName | string | 否 | 用户真实姓名 | "张三" |
| email | string | 否 | 邮箱地址 | "zhangsan@example.com" |
| phone | string | 否 | 手机号码 | "13800138000" |

**请求示例：**
```json
{
  "fullName": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138000"
}
```

**响应格式：**

成功响应 (200 OK):
```json
{
  "success": true,
  "message": "用户信息更新成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "fullName": "张三",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "createdAt": "2025-01-28T00:00:00Z",
    "updatedAt": "2025-01-28T14:20:00Z"
  }
}
```

失败响应 (400 Bad Request):
```json
{
  "success": false,
  "message": "邮箱格式不正确",
  "data": null
}
```

### 3.2 修改密码

**接口描述：** 修改当前用户密码

**请求信息：**
- **方法：** POST
- **路径：** `/api/auth/change-password`
- **Content-Type：** `application/json`
- **认证：** 需要Bearer Token

**请求头：**
```
Authorization: Bearer {token}
```

**请求参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| currentPassword | string | 是 | 当前密码 | "Test123456" |
| newPassword | string | 是 | 新密码，至少8位 | "NewTest123456" |
| confirmPassword | string | 是 | 确认新密码 | "NewTest123456" |

**请求示例：**
```json
{
  "currentPassword": "Test123456",
  "newPassword": "NewTest123456",
  "confirmPassword": "NewTest123456"
}
```

**响应格式：**

成功响应 (200 OK):
```json
{
  "success": true,
  "message": "密码修改成功",
  "data": null
}
```

失败响应 (400 Bad Request):
```json
{
  "success": false,
  "message": "当前密码不正确",
  "data": null
}
```

## 4. 系统接口

### 4.1 健康检查

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
    "version": "1.0.0",
    "database": {
      "status": "UP",
      "responseTime": "5ms"
    }
  }
}
```

## 5. 错误码说明

### 5.1 系统级错误码 (1000-1999)

| 错误码 | HTTP状态码 | 描述 | 解决方案 |
|--------|------------|------|----------|
| 1001 | 400 | 请求参数无效 | 检查请求参数格式和必填字段 |
| 1002 | 401 | 未授权访问 | 提供有效的JWT Token |
| 1003 | 403 | 权限不足 | 联系管理员获取相应权限 |
| 1004 | 404 | 资源不存在 | 检查请求的资源路径 |
| 1005 | 409 | 资源冲突 | 检查是否存在重复数据 |
| 1006 | 500 | 服务器内部错误 | 联系技术支持 |
| 1007 | 429 | 请求频率过高 | 降低请求频率，稍后重试 |

### 5.2 用户相关错误码 (2000-2999)

| 错误码 | HTTP状态码 | 描述 | 解决方案 |
|--------|------------|------|----------|
| 2001 | 400 | 用户名已存在 | 使用不同的用户名 |
| 2002 | 400 | 邮箱已被注册 | 使用不同的邮箱地址 |
| 2003 | 400 | 密码格式不正确 | 密码至少8位，包含字母和数字 |
| 2004 | 401 | 用户名或密码错误 | 检查登录凭据 |
| 2005 | 401 | 账户已被禁用 | 联系管理员解除禁用 |
| 2006 | 404 | 用户不存在 | 检查用户ID或用户名 |
| 2007 | 400 | 当前密码不正确 | 输入正确的当前密码 |

## 6. 通用响应格式

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

## 7. 认证说明

系统使用JWT (JSON Web Token) 进行用户认证：

1. 用户通过登录接口获取token
2. 在需要认证的接口请求头中添加：`Authorization: Bearer {token}`
3. Token有效期为1小时，过期后需要重新登录
4. 登出后token立即失效

## 8. 接口测试

### 8.1 测试环境
- 开发环境：`http://localhost:8080`
- 测试环境：`https://wanli-backend-staging.railway.app`
- 测试工具：Postman、curl、Jest

### 8.2 测试流程
1. 调用用户注册接口创建测试账号
2. 调用用户登录接口获取token
3. 使用token调用需要认证的接口
4. 测试错误场景和边界条件
5. 调用登出接口结束会话

### 8.3 基础功能测试用例

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

### 8.4 错误场景测试用例

**重复用户名注册测试：**
```bash
# 预期返回错误码 2001
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456",
    "confirmPassword": "Test123456",
    "email": "test2@example.com",
    "fullName": "测试用户2"
  }'
```

**错误密码登录测试：**
```bash
# 预期返回错误码 2004
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "WrongPassword"
  }'
```

**无效Token访问测试：**
```bash
# 预期返回错误码 1002
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer invalid_token"
```

**参数验证测试：**
```bash
# 预期返回错误码 1001
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ab",
    "password": "123",
    "email": "invalid-email"
  }'
```

### 8.5 性能测试

**并发登录测试：**
```bash
# 使用Apache Bench进行并发测试
ab -n 100 -c 10 -p login_data.json -T application/json http://localhost:8080/api/auth/login
```

**Token验证性能测试：**
```bash
# 测试Token验证接口的响应时间
ab -n 1000 -c 50 -H "Authorization: Bearer {token}" http://localhost:8080/api/auth/me
```

### 8.6 自动化测试

**Jest测试示例：**
```javascript
describe('用户认证API测试', () => {
  let authToken;
  
  test('用户注册成功', async () => {
    const response = await request(app)
      .post('/api/auth/register')
      .send({
        username: 'testuser',
        password: 'Test123456',
        confirmPassword: 'Test123456',
        email: 'test@example.com',
        fullName: '测试用户'
      });
    
    expect(response.status).toBe(201);
    expect(response.body.success).toBe(true);
  });
  
  test('用户登录成功', async () => {
    const response = await request(app)
      .post('/api/auth/login')
      .send({
        username: 'testuser',
        password: 'Test123456'
      });
    
    expect(response.status).toBe(200);
    expect(response.body.success).toBe(true);
    expect(response.body.data.token).toBeDefined();
    authToken = response.body.data.token;
  });
  
  test('获取用户信息成功', async () => {
    const response = await request(app)
      .get('/api/auth/me')
      .set('Authorization', `Bearer ${authToken}`);
    
    expect(response.status).toBe(200);
    expect(response.body.success).toBe(true);
    expect(response.body.data.username).toBe('testuser');
  });
});
```

---

**文档版本：** v1.0.0  
**最后更新：** 2025-01-28  
**维护者：** 万里后端开发团队