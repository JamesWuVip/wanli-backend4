# 已合并到API接口文档 - 完整版

本文档内容已合并到 `api_interface_design.md` 文件中。

请查看 `api_interface_design.md` 获取完整的API接口文档。

### 1.4 基础信息

* **Base URL**: `http://localhost:8080/api/v1`

* **认证方式**: JWT Bearer Token

* **Content-Type**: `application/json`

* **字符编码**: UTF-8

## 2. 通用规范

### 2.1 HTTP状态码

* `200 OK`: 请求成功

* `201 Created`: 资源创建成功

* `400 Bad Request`: 请求参数错误

* `401 Unauthorized`: 未授权

* `403 Forbidden`: 权限不足

* `404 Not Found`: 资源不存在

* `409 Conflict`: 资源冲突

* `500 Internal Server Error`: 服务器内部错误

### 2.2 统一响应格式

#### 成功响应

```json
{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": "2025-01-17T10:30:00Z"
}
```

#### 错误响应

```json
{
  "success": false,
  "code": 400,
  "message": "请求参数错误",
  "errors": [
    {
      "field": "name",
      "message": "名称不能为空"
    }
  ],
  "timestamp": "2025-01-17T10:30:00Z"
}
```

### 2.3 分页响应格式

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

## 3. 教育机构管理API

### 3.1 创建机构

**接口地址**: `POST /institutions`

**请求头**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:

```json
{
  "name": "万里学院总部",
  "description": "万里学院主要教学机构",
  "contactEmail": "contact@wanli.edu",
  "contactPhone": "010-12345678",
  "address": "北京市朝阳区教育路1号"
}
```

**参数说明**:

| 参数名          | 类型     | 必填 | 说明           |
| ------------ | ------ | -- | ------------ |
| name         | String | 是  | 机构名称，最大100字符 |
| description  | String | 否  | 机构描述         |
| contactEmail | String | 否  | 联系邮箱         |
| contactPhone | String | 否  | 联系电话         |
| address      | String | 否  | 机构地址         |

**响应示例**:

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
  "timestamp": "2025-01-17T10:30:00Z"
}
```

### 3.2 查询机构列表

**接口地址**: `GET /institutions`

**请求头**:

```
Authorization: Bearer {token}
```

**查询参数**:

| 参数名    | 类型      | 必填 | 说明                               |
| ------ | ------- | -- | -------------------------------- |
| page   | Integer | 否  | 页码，默认0                           |
| size   | Integer | 否  | 每页大小，默认20                        |
| sort   | String  | 否  | 排序，默认createdAt,desc              |
| search | String  | 否  | 搜索关键词（机构名称）                      |
| status | String  | 否  | 机构状态：ACTIVE, INACTIVE, SUSPENDED |

**响应示例**:

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
  "timestamp": "2025-01-17T10:30:00Z"
}
```

### 3.3 查询机构详情

**接口地址**: `GET /institutions/{id}`

**请求头**:

```
Authorization: Bearer {token}
```

**路径参数**:

| 参数名 | 类型   | 必填 | 说明   |
| --- | ---- | -- | ---- |
| id  | UUID | 是  | 机构ID |

**响应示例**:

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
  "timestamp": "2025-01-17T10:30:00Z"
}
```

### 3.4 更新机构信息

**接口地址**: `PUT /institutions/{id}`

**请求头**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

**路径参数**:

| 参数名 | 类型   | 必填 | 说明   |
| --- | ---- | -- | ---- |
| id  | UUID | 是  | 机构ID |

**请求参数**:

```json
{
  "name": "万里学院总部（更新）",
  "description": "万里学院主要教学机构（更新）",
  "contactEmail": "contact@wanli.edu",
  "contactPhone": "010-12345678",
  "address": "北京市朝阳区教育路1号"
}
```

### 3.5 删除机构

**接口地址**: `DELETE /institutions/{id}`

**请求头**:

```
Authorization: Bearer {token}
```

**路径参数**:

| 参数名 | 类型   | 必填 | 说明   |
| --- | ---- | -- | ---- |
| id  | UUID | 是  | 机构ID |

**响应示例**:

```json
{
  "success": true,
  "code": 200,
  "message": "机构删除成功",
  "timestamp": "2025-01-17T10:30:00Z"
}
```

## 4. 课程管理API

### 4.1 创建课程

**接口地址**: `POST /courses`

**请求头**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:

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

**参数说明**:

| 参数名           | 类型         | 必填 | 说明           |
| ------------- | ---------- | -- | ------------ |
| name          | String     | 是  | 课程名称，最大100字符 |
| description   | String     | 否  | 课程描述         |
| institutionId | UUID       | 是  | 所属机构ID       |
| price         | BigDecimal | 否  | 课程价格，默认0.00  |
| durationHours | Integer    | 否  | 课程时长(小时)，默认0 |
| startDate     | DateTime   | 否  | 开始时间         |
| endDate       | DateTime   | 否  | 结束时间         |
| maxStudents   | Integer    | 否  | 最大学员数，默认50   |

**响应示例**:

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
  "timestamp": "2025-01-17T10:30:00Z"
}
```

### 4.2 查询课程列表

**接口地址**: `GET /courses`

**请求头**:

```
Authorization: Bearer {token}
```

**查询参数**:

| 参数名           | 类型      | 必填 | 说明                                                   |
| ------------- | ------- | -- | ---------------------------------------------------- |
| page          | Integer | 否  | 页码，默认0                                               |
| size          | Integer | 否  | 每页大小，默认20                                            |
| sort          | String  | 否  | 排序，默认createdAt,desc                                  |
| search        | String  | 否  | 搜索关键词（课程名称）                                          |
| institutionId | UUID    | 否  | 机构ID                                                 |
| status        | String  | 否  | 课程状态：DRAFT, PUBLISHED, ONGOING, COMPLETED, CANCELLED |

### 4.3 查询课程详情

**接口地址**: `GET /courses/{id}`

**请求头**:

```
Authorization: Bearer {token}
```

**路径参数**:

| 参数名 | 类型   | 必填 | 说明   |
| --- | ---- | -- | ---- |
| id  | UUID | 是  | 课程ID |

### 4.4 更新课程信息

**接口地址**: `PUT /courses/{id}`

**请求头**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

### 4.5 发布课程

**接口地址**: `POST /courses/{id}/publish`

**请求头**:

```
Authorization: Bearer {token}
```

**路径参数**:

| 参数名 | 类型   | 必填 | 说明   |
| --- | ---- | -- | ---- |
| id  | UUID | 是  | 课程ID |

**响应示例**:

```json
{
  "success": true,
  "code": 200,
  "message": "课程发布成功",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "status": "PUBLISHED"
  },
  "timestamp": "2025-01-17T10:30:00Z"
}
```

### 4.6 取消课程

**接口地址**: `POST /courses/{id}/cancel`

**请求头**:

```
Authorization: Bearer {token}
```

### 4.7 删除课程

**接口地址**: `DELETE /courses/{id}`

**请求头**:

```
Authorization: Bearer {token}
```

## 5. 学员管理API

### 5.1 创建学员

**接口地址**: `POST /students`

**请求头**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:

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

**参数说明**:

| 参数名           | 类型     | 必填 | 说明                     |
| ------------- | ------ | -- | ---------------------- |
| name          | String | 是  | 学员姓名，最大50字符            |
| email         | String | 否  | 邮箱地址                   |
| phone         | String | 否  | 电话号码                   |
| birthDate     | Date   | 否  | 出生日期                   |
| gender        | String | 否  | 性别：MALE, FEMALE, OTHER |
| address       | String | 否  | 地址                     |
| institutionId | UUID   | 是  | 所属机构ID                 |

**响应示例**:

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
  "timestamp": "2025-01-17T10:30:00Z"
}
```

### 5.2 查询学员列表

**接口地址**: `GET /students`

**请求头**:

```
Authorization: Bearer {token}
```

**查询参数**:

| 参数名           | 类型      | 必填 | 说明                                        |
| ------------- | ------- | -- | ----------------------------------------- |
| page          | Integer | 否  | 页码，默认0                                    |
| size          | Integer | 否  | 每页大小，默认20                                 |
| sort          | String  | 否  | 排序，默认createdAt,desc                       |
| search        | String  | 否  | 搜索关键词（学员姓名、邮箱、电话）                         |
| institutionId | UUID    | 否  | 机构ID                                      |
| status        | String  | 否  | 学员状态：ACTIVE, INACTIVE, GRADUATED, DROPPED |
| gender        | String  | 否  | 性别：MALE, FEMALE, OTHER                    |

### 5.3 查询学员详情

**接口地址**: `GET /students/{id}`

**请求头**:

```
Authorization: Bearer {token}
```

### 5.4 更新学员信息

**接口地址**: `PUT /students/{id}`

**请求头**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

### 5.5 删除学员

**接口地址**: `DELETE /students/{id}`

**请求头**:

```
Authorization: Bearer {token}
```

## 6. 课程学员关联管理API

### 6.1 学员报名课程

**接口地址**: `POST /courses/{courseId}/students/{studentId}/enroll`

**请求头**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

**路径参数**:

| 参数名       | 类型   | 必填 | 说明   |
| --------- | ---- | -- | ---- |
| courseId  | UUID | 是  | 课程ID |
| studentId | UUID | 是  | 学员ID |

**请求参数**:

```json
{
  "paidAmount": 2999.00,
  "paymentDate": "2025-01-17T10:30:00Z",
  "notes": "全额付款"
}
```

**参数说明**:

| 参数名         | 类型         | 必填 | 说明          |
| ----------- | ---------- | -- | ----------- |
| paidAmount  | BigDecimal | 否  | 已付金额，默认0.00 |
| paymentDate | DateTime   | 否  | 付款时间        |
| notes       | String     | 否  | 备注信息        |

**响应示例**:

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
  "timestamp": "2025-01-17T10:30:00Z"
}
```

### 6.2 查询课程学员列表

**接口地址**: `GET /courses/{courseId}/students`

**请求头**:

```
Authorization: Bearer {token}
```

**路径参数**:

| 参数名      | 类型   | 必填 | 说明   |
| -------- | ---- | -- | ---- |
| courseId | UUID | 是  | 课程ID |

**查询参数**:

| 参数名    | 类型      | 必填 | 说明                                           |
| ------ | ------- | -- | -------------------------------------------- |
| page   | Integer | 否  | 页码，默认0                                       |
| size   | Integer | 否  | 每页大小，默认20                                    |
| sort   | String  | 否  | 排序，默认enrollmentDate,desc                     |
| status | String  | 否  | 报名状态：ENROLLED, COMPLETED, DROPPED, SUSPENDED |

### 6.3 查询学员课程列表

**接口地址**: `GET /students/{studentId}/courses`

**请求头**:

```
Authorization: Bearer {token}
```

**路径参数**:

| 参数名       | 类型   | 必填 | 说明   |
| --------- | ---- | -- | ---- |
| studentId | UUID | 是  | 学员ID |

### 6.4 学员退课

**接口地址**: `POST /courses/{courseId}/students/{studentId}/drop`

**请求头**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:

```json
{
  "notes": "个人原因退课"
}
```

### 6.5 学员完成课程

**接口地址**: `POST /courses/{courseId}/students/{studentId}/complete`

**请求头**:

```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:

```json
{
  "notes": "课程完成"
}
```

## 7. 错误码定义

### 7.1 系统级错误 (1000-1999)

* `1000`: 系统内部错误

* `1001`: 数据库连接错误

* `1002`: 服务不可用

### 7.2 认证授权错误 (2000-2999)

* `2000`: 未授权访问

* `2001`: Token无效

* `2002`: Token已过期

* `2003`: 权限不足

### 7.3 教育机构错误 (3000-3099)

* `3000`: 机构不存在

* `3001`: 机构名称已存在

* `3002`: 机构状态无效

* `3003`: 机构下存在课程，无法删除

### 7.4 课程错误 (3100-3199)

* `3100`: 课程不存在

* `3101`: 课程名称已存在

* `3102`: 课程状态无效

* `3103`: 课程已满员

* `3104`: 课程时间冲突

* `3105`: 课程已开始，无法修改

### 7.5 学员错误 (3200-3299)

* `3200`: 学员不存在

* `3201`: 学员邮箱已存在

* `3202`: 学员电话已存在

* `3203`: 学员状态无效

* `3204`: 学员已报名该课程

* `3205`: 学员未报名该课程

### 7.6 数据操作错误 (6000-6999)

* `6000`: 数据验证失败

* `6001`: 必填字段为空

* `6002`: 字段长度超限

* `6003`: 字段格式错误

* `6004`: 数据重复

* `6005`: 外键约束违反

## 8. 接口测试示例

### 8.1 Postman测试集合

#### 环境变量

```json
{
  "baseUrl": "http://localhost:8080/api/v1",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 测试用例

**1. 创建机构**

```
POST {{baseUrl}}/institutions
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "name": "测试机构",
  "description": "这是一个测试机构",
  "contactEmail": "test@example.com",
  "contactPhone": "13800138000",
  "address": "测试地址"
}
```

**2. 查询机构列表**

```
GET {{baseUrl}}/institutions?page=0&size=10&sort=createdAt,desc
Authorization: Bearer {{token}}
```

**3. 创建课程**

```
POST {{baseUrl}}/courses
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "name": "测试课程",
  "description": "这是一个测试课程",
  "institutionId": "{{institutionId}}",
  "price": 1999.00,
  "durationHours": 30,
  "maxStudents": 20
}
```

**4. 创建学员**

```
POST {{baseUrl}}/students
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "name": "测试学员",
  "email": "student@example.com",
  "phone": "13800138001",
  "birthDate": "1995-01-01",
  "gender": "MALE",
  "institutionId": "{{institutionId}}"
}
```

**5. 学员报名课程**

```
POST {{baseUrl}}/courses/{{courseId}}/students/{{studentId}}/enroll
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "paidAmount": 1999.00,
  "paymentDate": "2025-01-17T10:30:00Z",
  "notes": "测试报名"
}
```

## 9. 版本更新记录

### v1.0.0 (2025-01-17)

* 初始版本发布

* 定义教育机构管理API

* 定义课程管理API

* 定义学员管理API

* 定义课程学员关联管理API

* 定义统一响应格式和错误码

***

**文档维护**

* 负责人：开发团队

* 更新频率：随API变更更新

* 审核周期：每周审核一次

* 最后更新：2025-01-17

