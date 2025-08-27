符合spring boot的标准项目结构，各项spring官方规范，尽可能使用标准组件而不是自定义或第三方组件
这是个后端项目，使用spring boot框架，数据库使用mysql，没有前端
后端项目的验收标准以对接口的测试为准，接口测试使用postman，最终要输出可用的API和接口文档
每个代码文件不超过800行，每个类不超过200行
git仓库操作遵循gitflow规范
github访问应当使用MCP，操作流程如下：
1.  clone项目到本地
2.  创建新分支
3.  提交代码
4.  创建pull request
5.  合并代码
dev分支是开发分支，main分支是生产分支，staging分支是测试分支
dev环境下，后端在本地运行，数据库在本地运行
staging和prod环境下，后端部署在railway，数据库部署在railway，数据库使用psql


