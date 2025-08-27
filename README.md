# 万里后端项目

基于Spring Boot 3.5的纯后端项目，采用标准的Maven项目结构。

## 项目结构

```
wanli-backend4/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── wanli/
│   │   │           ├── WanliBackendApplication.java    # 主启动类
│   │   │           ├── controller/                     # 控制器层
│   │   │           │   ├── UserController.java
│   │   │           │   └── HealthController.java
│   │   │           ├── service/                        # 服务层
│   │   │           │   ├── UserService.java
│   │   │           │   └── impl/
│   │   │           │       └── UserServiceImpl.java
│   │   │           ├── repository/                     # 数据访问层
│   │   │           │   └── UserRepository.java
│   │   │           ├── entity/                         # 实体类
│   │   │           │   └── User.java
│   │   │           ├── config/                         # 配置类
│   │   │           │   └── WebConfig.java
│   │   │           ├── common/                         # 通用类
│   │   │           │   └── Result.java
│   │   │           ├── exception/                      # 异常处理
│   │   │           │   ├── BusinessException.java
│   │   │           │   └── GlobalExceptionHandler.java
│   │   │           ├── dto/                            # 数据传输对象
│   │   │           └── vo/                             # 视图对象
│   │   └── resources/
│   │       └── application.yml                         # 配置文件
│   └── test/
│       ├── java/                                       # 测试代码
│       └── resources/                                  # 测试资源
├── pom.xml                                             # Maven配置文件
└── README.md                                           # 项目说明
```

## 技术栈

- **Spring Boot**: 3.5.0
- **Java**: 17
- **Maven**: 项目构建工具
- **Spring Data JPA**: 数据持久化
- **PostgreSQL**: 数据库
- **Spring Security**: 安全框架
- **JWT**: 身份认证
- **Lombok**: 简化代码
- **Validation**: 参数校验

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- PostgreSQL 15+

### 2. 数据库配置

创建数据库：
```sql
CREATE DATABASE wanli_db;
CREATE USER wanli_user WITH PASSWORD 'wanli_password';
GRANT ALL PRIVILEGES ON DATABASE wanli_db TO wanli_user;
```

### 3. 运行项目

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

### 4. 访问接口

项目启动后，可以通过以下地址访问：

- 健康检查：`GET http://localhost:8080/api/health`
- API文档：`http://localhost:8080/api/swagger-ui.html`

## API 接口

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/me` - 获取当前用户信息
- `POST /api/auth/logout` - 用户登出

### 用户管理
- `POST /api/users` - 创建用户（管理员）
- `GET /api/users` - 获取所有用户（管理员）
- `GET /api/users/{id}` - 根据ID获取用户
- `PUT /api/users/{id}` - 更新用户信息
- `DELETE /api/users/{id}` - 删除用户（管理员）

### 系统接口
- `GET /api/health` - 系统健康检查
- `GET /api/info` - 系统信息

## 开发规范

### 命名规则
1. 文件夹命名：小写字母，下划线分隔
2. 文件命名：小写字母，下划线分隔
3. 类命名：驼峰命名
4. 方法命名：驼峰命名
5. 变量命名：驼峰命名
6. 常量命名：大写字母，下划线分隔

### 代码规范
- 每个方法不超过200行
- 使用Lombok简化代码
- 统一异常处理
- 统一响应格式

## 项目特性

- ✅ 标准的Spring Boot 3.5项目结构
- ✅ JWT身份认证和授权
- ✅ 基于角色的访问控制（RBAC）
- ✅ 统一的响应格式封装
- ✅ 全局异常处理
- ✅ 参数校验
- ✅ 跨域配置
- ✅ JPA数据持久化
- ✅ 完整的用户管理系统
- ✅ 健康检查接口
- ✅ API文档集成
- ✅ 单元测试覆盖率80%+

## Git工作流

本项目采用GitFlow工作流：

- `main` - 生产环境分支
- `staging` - 测试环境分支  
- `dev` - 开发环境分支
- `feature/*` - 功能开发分支

## 许可证

MIT License