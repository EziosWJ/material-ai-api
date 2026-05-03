# 任务拆解索引
本目录用于给独立 agent 分配后端开发任务。未来 `tmp` 目录可能删除，因此每个任务文件必须自包含本模块接口、DDL 摘要、业务规则和验收标准。
## 必读规范
- 执行任一任务前必须阅读 `doc/development-constraints.md`。
- 涉及模块开发、表结构、CRUD、认证、SQL、日志、文件、权限时，还必须阅读 `doc/ai-project-prompt.md`。
## 全局开发约束
- 使用 Java 21、Spring Boot 3.x、MySQL 8.0、MyBatis-Plus、Sa-Token、Lombok、SpringDoc OpenAPI、Spring Validation、Hutool。
- 单 Spring Boot 应用，单 Maven module，包级模块化。
- 接口统一前缀 `/api`。
- 后台资源路径统一使用单数：`/api/system/user`、`/api/system/role`、`/api/system/menu`。
- 统一响应：`{"code":200,"message":"success","data":{}}`。
- 统一分页：`{"records":[],"total":100,"page":1,"pageSize":10}`。
- 数据库表名使用 `sys_` 前缀，字段使用 `snake_case`，Java/JSON 字段使用 `camelCase`。
- 不使用数据库外键，删除约束由 Service 层控制。
- 默认逻辑删除；日志清空除外。
- 除登录接口外，后台接口默认需要登录。
- 第一版不实现验证码、按钮权限、接口权限、数据权限、Excel 导出、对象存储、OAuth2、第三方登录、多租户。
## 可并行执行说明
- 这些任务可以由多个独立 agent 并行执行。
- 每个任务应优先完成自己模块的 Controller、Service、Mapper、Entity、DTO、VO、必要 XML 和测试/验证。
- 跨模块调用只依赖接口契约和表字段，不要求读取其他任务文件。
- 如果共享基础类不存在，当前任务可以按 `01-foundation.md` 的契约补最小实现，避免阻塞。
- 不同 agent 修改同一共享类时，应保持最小改动并遵守既有命名。
## 跨模块归属
- 用户与角色绑定归 `03-user.md`：`PUT /api/system/user/{id}/roles`。
- 角色与菜单绑定归 `04-role.md`：`PUT /api/system/role/{id}/menus`。
- 当前用户菜单归 `02-auth.md`：`GET /api/auth/menus`。
- 文件上传归 `08-file.md`，用户头像接口只保存头像 URL。
- 登录日志写入由认证流程触发，日志查询和清空归 `09-log.md`。
## 任务列表
- `01-foundation.md`：基础设施与通用能力。
- `02-auth.md`：认证模块。
- `03-user.md`：用户模块。
- `04-role.md`：角色模块。
- `05-menu.md`：菜单模块。
- `06-dept.md`：部门模块。
- `07-dict.md`：字典模块。
- `08-file.md`：文件模块。
- `09-log.md`：日志模块。
- `10-sql-init.md`：SQL 脚本与初始化数据。
- `11-config.md`：配置模块。
- `12-file-fix.md`：文件模块问题修复（预览放行、批量上传部分失败处理、文档更新）。
