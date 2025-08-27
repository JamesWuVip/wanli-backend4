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
- **MySQL**: 数据库
- **Lombok**: 简化代码
- **Validation**: 参数校验

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

创建数据库：
```sql
CREATE DATABASE wanli_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wanli_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
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
- 用户管理：`http://localhost:8080/api/users`

## API 接口

### 健康检查
- `GET /api/health` - 服务健康检查

### 用户管理
- `POST /api/users` - 创建用户
- `GET /api/users` - 获取所有用户
- `GET /api/users/{id}` - 根据ID获取用户
- `GET /api/users/username/{username}` - 根据用户名获取用户
- `PUT /api/users/{id}` - 更新用户信息
- `DELETE /api/users/{id}` - 删除用户
- `GET /api/users/check/username/{username}` - 检查用户名是否存在
- `GET /api/users/check/email/{email}` - 检查邮箱是否存在

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
- ✅ 统一的响应格式封装
- ✅ 全局异常处理
- ✅ 参数校验
- ✅ 跨域配置
- ✅ JPA数据持久化
- ✅ 完整的用户管理示例
- ✅ 健康检查接口

## 许可证

MIT License