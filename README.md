# Base API Admin

通用后台管理系统后端模板，服务 React 管理后台。

## 技术栈

- Java 21
- Spring Boot 3.5.14
- MySQL 8.0
- MyBatis-Plus 3.5.15
- Sa-Token 1.45.0
- Knife4j 4.4.0
- Lombok
- Hutool

## 项目结构

```
src/main/java/cn/ezios/baseapi/
├── common/          # 公共模块
│   ├── enums/       # 枚举定义
│   ├── exception/   # 自定义异常
│   └── model/       # 通用模型（ApiResponse、PageResult等）
├── framework/       # 框架层
│   ├── config/      # 配置类（CORS、Sa-Token、MyBatis-Plus等）
│   ├── handler/     # 全局异常处理器、审计字段自动填充
│   └── log/         # 操作日志切面
└── modules/         # 业务模块
    ├── auth/        # 认证模块（登录、登出、获取当前用户信息）
    └── system/      # 系统管理模块
        ├── user/    # 用户管理
        ├── role/    # 角色管理
        ├── menu/    # 菜单管理
        ├── dept/    # 部门管理
        ├── dict/    # 字典管理
        ├── log/     # 日志管理（登录日志、操作日志）
        └── file/    # 文件管理
```

## 模块分层

每个业务模块统一分层：
- `controller` - 接口入口、参数校验、响应封装
- `service` - 业务逻辑
- `service.impl` - 服务实现
- `mapper` - 数据库访问
- `entity` - 数据库实体
- `dto` - 请求参数
- `vo` - 响应数据

## 权限模型

采用 RBAC 权限模型：用户 -> 角色 -> 菜单

- 使用 Sa-Token 实现认证鉴权
- 第一版实现登录态校验和菜单可见控制
- `permission_code` 字段预留，暂不实现按钮/接口/数据权限

## 接口规范

- 统一前缀：`/api`
- REST 风格，资源名使用单数
- 分页接口：`/page`
- 批量删除：`DELETE /batch`
- 响应结构：`{"code":200,"message":"success","data":{}}`

## 环境配置

- `dev`：开发环境，连接本机 MySQL，开启 Knife4j 文档
- `prod`：生产环境，关闭文档入口

## 快速启动

1. 创建数据库并执行初始化脚本
2. 修改 `application-dev.yml` 中的数据库配置
3. 运行 `BaseApiAdminApplication`
4. 访问 Knife4j 文档：`http://localhost:8080/doc.html`

## 开发约束

详见 `doc/development-constraints.md` 和 `doc/ai-project-prompt.md`
