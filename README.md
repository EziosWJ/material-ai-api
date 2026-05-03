# Base API Admin

通用后台管理系统后端模板，服务 React 管理后台。采用单 Spring Boot 应用 + 包级模块化的单体架构。

## 技术栈

| 组件 | 版本 |
|------|------|
| Java | 21 |
| Spring Boot | 3.5.14 |
| MySQL | 8.0 |
| MyBatis-Plus | 3.5.15 |
| Sa-Token | 1.45.0 |
| SpringDoc OpenAPI | 2.8.6 |
| Lombok | 1.18.44 |
| Hutool | 5.8.38 |

## 项目结构

```
src/main/java/cn/ezios/baseapi/
├── common/                  # 公共模块
│   ├── enums/               # 枚举定义（ResponseCode 等）
│   ├── exception/           # 自定义异常（BusinessException）
│   └── model/               # 通用模型（ApiResponse、PageResult、PageQuery、BatchIdsRequest、StatusUpdateRequest）
├── framework/               # 框架层
│   ├── config/              # 配置类
│   │   ├── CorsConfig       # CORS 跨域配置
│   │   ├── SaTokenConfig    # Sa-Token 认证配置
│   │   ├── MybatisPlusConfig# MyBatis-Plus 分页插件
│   │   ├── OpenApiConfig    # SpringDoc OpenAPI 接口文档
│   │   ├── PasswordConfig   # BCrypt 密码编码器
│   │   └── SystemProperties # 自定义配置项
│   ├── handler/             # 全局异常处理器、审计字段自动填充
│   └── log/                 # 操作日志切面（@OperLog 注解）
└── modules/                 # 业务模块
    ├── auth/                # 认证模块（登录、登出、当前用户信息、当前用户菜单）
    └── system/              # 系统管理模块
        ├── user/            # 用户管理
        ├── role/            # 角色管理
        ├── menu/            # 菜单管理
        ├── dept/            # 部门管理
        ├── dict/            # 字典管理（字典类型 + 字典数据）
        ├── log/             # 日志管理（登录日志、操作日志）
        ├── file/            # 文件管理
        └── config/          # 系统配置管理
```

## 模块分层

每个业务模块统一分层：

- `controller` — 接口入口、参数校验、响应封装
- `service` — 业务逻辑接口
- `service.impl` — 服务实现
- `mapper` — 数据库访问（MyBatis-Plus BaseMapper）
- `entity` — 数据库实体
- `dto` — 请求参数（入参校验）
- `vo` — 响应数据（返回前端）

## 权限模型

采用 RBAC 权限模型：**用户 -> 角色 -> 菜单**

- 使用 Sa-Token 实现认证鉴权，Token 通过 `Authorization: Bearer <token>` 传递
- Token 有效期 2 小时，滑动续期，允许同一账号多端登录
- 第一版实现登录态校验和菜单可见控制
- `permission_code` 字段预留，暂不实现按钮/接口/数据权限

## 数据库设计规范

- 表名统一使用 `sys_` 前缀
- 主键使用 `BIGINT` 自增
- 字段命名使用 `snake_case`，Java/JSON 字段使用 `camelCase`
- 布尔字段使用 `tinyint`：`status`(1=启用/0=禁用)、`visible`(1=显示/0=隐藏)、`is_builtin`(1=内置/0=普通)
- 逻辑删除字段 `deleted`：`0=正常`、`1=已删除`（语义与普通布尔相反）
- 时间字段统一使用 `datetime`，`create_time`/`update_time` 由 MyBatis-Plus 自动填充
- 不使用数据库外键，删除约束由 Service 层控制

### 核心表

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户表 |
| `sys_role` | 角色表 |
| `sys_menu` | 菜单表（支持目录、菜单、外链） |
| `sys_user_role` | 用户-角色关联表 |
| `sys_role_menu` | 角色-菜单关联表 |
| `sys_dept` | 部门表（树形结构） |
| `sys_dict_type` | 字典类型表 |
| `sys_dict_data` | 字典数据表 |
| `sys_file` | 文件记录表 |
| `sys_config` | 系统配置表 |
| `sys_login_log` | 登录日志表 |
| `sys_oper_log` | 操作日志表 |

## 接口规范

- 统一前缀：`/api`
- REST 风格，资源名使用单数
- 分页接口：`GET /page?page=1&pageSize=10`
- 批量删除：`POST /batch-delete`，Body: `{"ids":[1,2,3]}`
- 状态变更：`PATCH /{id}/status`，Body: `{"status":1}`
- 资源分配：`PUT /{id}/roles` 或 `PUT /{id}/menus`

### 响应结构

```json
// 成功
{"code": 200, "message": "success", "data": {...}}

// 分页
{"code": 200, "message": "success", "data": {"records": [], "total": 100, "page": 1, "pageSize": 10}}

// 失败
{"code": 400, "message": "错误信息", "data": null}
```

### 响应码

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 参数校验失败 |
| 401 | 未登录或 Token 失效 |
| 403 | 无权限 |
| 404 | 数据不存在 |
| 500 | 系统错误 |

## 功能模块

### 认证模块 (auth)

- 登录/登出：`POST /api/auth/login`、`POST /api/auth/logout`
- 当前用户信息：`GET /api/auth/me`
- 当前用户菜单树：`GET /api/auth/menus`

### 用户管理

- 标准 CRUD：分页、详情、新增、修改、删除、批量删除
- 启用/禁用、分配角色、重置密码
- 当前用户修改密码和头像

### 角色管理

- 标准 CRUD：分页、详情、新增、修改、删除、批量删除
- 启用/禁用、分配菜单、角色选择列表（下拉用）

### 菜单管理

- 菜单树查询、分页、详情、新增、修改、删除、批量删除
- 支持目录(DIR)、菜单(MENU)、外链(LINK) 三种类型
- 内置菜单保护

### 部门管理

- 部门树查询、部门选择树（下拉用）、分页、详情、新增、修改、删除、批量删除
- 树形结构，根节点 `parent_id=0`

### 字典管理

- 字典类型：分页、详情、新增、修改、删除、批量删除、启用/禁用
- 字典数据：分页、详情、新增、修改、删除、批量删除
- 按编码查询字典项：`GET /api/system/dict/{dictCode}/items`

### 文件管理

- 单文件上传、批量文件上传
- 文件列表、详情、修改、删除、批量删除、启用/禁用
- 文件预览：`GET /api/system/file/{id}/view`
- 文件下载：`GET /api/system/file/{id}/download`
- 本地文件存储，按日期分组：`uploads/yyyy/MM/dd/xxx.ext`
- 单文件最大 50MB

### 日志管理

- 登录日志：分页、详情、清空（物理删除）
- 操作日志：分页、详情、清空（物理删除）
- 操作日志通过 `@OperLog` 注解自动记录关键写操作
- 敏感字段自动脱敏（password、token 等）
- 清空接口通过 `system.log-clear-enabled` 配置开关控制

### 系统配置

- 配置项 CRUD：分页、详情、新增、修改、删除、批量删除
- 按配置键查询：`GET /api/system/config/key/{configKey}`
- 内置配置项保护

## 环境配置

| 配置项 | dev | prod |
|--------|-----|------|
| 接口文档 (SpringDoc) | 开启 | 关闭 |
| 日志清空接口 | 开启 | 关闭 |
| CORS 来源 | `http://localhost:5173` | 按实际域名配置 |
| 数据库 | 本机 MySQL | 按实际配置 |

### 自定义配置项

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `system.default-password` | `admin123` | 新增用户默认密码 |
| `system.log-clear-enabled` | dev=true, prod=false | 日志清空开关 |
| `system.file.upload-root` | `uploads` | 本地文件上传根目录 |

## 快速启动

1. 创建数据库并执行初始化脚本：
   ```sql
   SOURCE src/main/resources/sql/init.sql;
   ```
2. 修改 `application.yml` 中的数据库连接配置（环境变量 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`）
3. 运行 `BaseApiAdminApplication`
4. 访问接口文档：`http://localhost:8080/swagger-ui.html`

### 初始化账号

| 账号 | 密码 | 说明 |
|------|------|------|
| admin | admin123 | 超级管理员 |

## 开发约束

- 详见 [doc/development-constraints.md](doc/development-constraints.md)
- 详见 [doc/ai-project-prompt.md](doc/ai-project-prompt.md)
- 前端对接指南：[doc/frontend-api-guide.md](doc/frontend-api-guide.md)

## 构建

```bash
mvn clean package -DskipTests
java -jar target/base_api_admin-v1.0.0.jar
```
