# 项目命名规范

## 1. 数据库命名规范

### 1.1 表名
- 使用小写字母和下划线分隔
- 使用复数形式
- 例如：`users`, `courses`, `student_classes`

### 1.2 字段名
- 使用小写字母和下划线分隔
- 使用描述性名称
- 例如：`user_id`, `created_at`, `full_name`

### 1.3 索引名
- 格式：`idx_表名_字段名`
- 例如：`idx_users_email`, `idx_courses_creator_id`

### 1.4 外键约束名
- 格式：`fk_表名_字段名`
- 例如：`fk_courses_creator_id`

## 2. Java代码命名规范

### 2.1 类名
- 使用PascalCase（大驼峰命名法）
- 名词或名词短语
- 例如：`User`, `Course`, `StudentClass`

### 2.2 方法名
- 使用camelCase（小驼峰命名法）
- 动词或动词短语
- 例如：`createUser()`, `findById()`, `updateCourse()`

### 2.3 变量名
- 使用camelCase（小驼峰命名法）
- 名词或名词短语
- 例如：`userId`, `courseName`, `createdAt`

### 2.4 常量名
- 使用UPPER_SNAKE_CASE（全大写下划线分隔）
- 例如：`MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE`

### 2.5 包名
- 使用小写字母和点分隔
- 例如：`com.wanli.entity`, `com.wanli.service.impl`

### 2.6 枚举名
- 枚举类使用PascalCase
- 枚举值使用UPPER_SNAKE_CASE
- 例如：`enum Status { ACTIVE, INACTIVE, PENDING }`

## 3. 实体类与数据库映射规范

### 3.1 字段映射原则
- 数据库字段使用下划线命名：`user_name`
- Java字段使用驼峰命名：`userName`
- 使用`@Column(name = "user_name")`明确映射关系

### 3.2 统一字段命名
- 主键统一使用：`id`
- 创建时间：`created_at` -> `createdAt`
- 更新时间：`updated_at` -> `updatedAt`
- 删除时间：`deleted_at` -> `deletedAt`
- 创建者：`created_by` -> `createdBy`
- 更新者：`updated_by` -> `updatedBy`

## 4. API接口命名规范

### 4.1 URL路径
- 使用小写字母和连字符分隔
- 使用复数形式的资源名
- 例如：`/api/users`, `/api/courses`

### 4.2 HTTP方法与操作映射
- GET：查询操作
- POST：创建操作
- PUT：完整更新操作
- PATCH：部分更新操作
- DELETE：删除操作

### 4.3 请求参数
- 查询参数使用camelCase：`?pageSize=10&sortBy=createdAt`
- 路径参数使用kebab-case：`/users/{user-id}`

## 5. 文件和目录命名规范

### 5.1 目录名
- 使用小写字母和下划线分隔
- 例如：`src/main/java`, `test_resources`

### 5.2 Java文件名
- 与类名保持一致（PascalCase）
- 例如：`UserService.java`, `CourseController.java`

### 5.3 配置文件名
- 使用小写字母和连字符分隔
- 例如：`application-dev.yml`, `logback-spring.xml`

## 6. 注释和文档规范

### 6.1 类注释
```java
/**
 * 用户实体类
 * 用于管理系统用户信息
 * 
 * @author 开发者姓名
 * @since 1.0.0
 */
```

### 6.2 方法注释
```java
/**
 * 根据用户ID查询用户信息
 * 
 * @param userId 用户ID
 * @return 用户信息
 * @throws UserNotFoundException 用户不存在时抛出
 */
```

## 7. 数据库设计规范

### 7.1 通用字段
每个业务表都应包含以下通用字段：
- `id`：主键，使用UUID
- `created_at`：创建时间
- `updated_at`：更新时间
- `deleted_at`：软删除时间（可选）
- `created_by`：创建者
- `updated_by`：更新者

### 7.2 外键命名
- 外键字段名格式：`关联表名_id`
- 例如：`user_id`, `course_id`, `creator_id`

## 8. 异常处理规范

### 8.1 异常类命名
- 使用PascalCase + Exception后缀
- 例如：`UserNotFoundException`, `InvalidParameterException`

### 8.2 错误码规范
- 使用4位数字编码
- 1000-1999：系统级错误
- 2000-2999：用户相关错误
- 3000-3999：课程相关错误
- 6000-6999：数据操作错误

## 9. 测试命名规范

### 9.1 测试类命名
- 格式：`被测试类名 + Test`
- 例如：`UserServiceTest`, `CourseControllerTest`

### 9.2 测试方法命名
- 格式：`should_期望结果_when_测试条件`
- 例如：`should_ReturnUser_when_ValidIdProvided`

## 10. 版本控制规范

### 10.1 分支命名
- 主分支：`main`
- 开发分支：`dev`
- 功能分支：`feature/功能描述`
- 修复分支：`fix/问题描述`
- 发布分支：`release/版本号`

### 10.2 提交信息规范
- 格式：`类型(范围): 描述`
- 类型：feat, fix, docs, style, refactor, test, chore
- 例如：`feat(user): 添加用户注册功能`

---

**注意：本规范应在项目开发过程中严格遵守，确保代码的一致性和可维护性。**