# 本地化智能材料写作平台 Java 后端

当前项目是“本地化智能材料写作平台”的 Java 业务后端，负责业务主控、认证权限、材料主数据、文件记录、写作任务、问答记录、业务状态管理和调用审计。

本项目基于 `base-api` 脚手架复制和改造而来。原脚手架提供了通用后台管理能力，包括认证、RBAC、用户、角色、菜单、部门、字典、系统配置、文件、登录日志、操作日志、统一响应、统一分页、统一异常处理等基础能力。当前仓库已经进入具体业务系统阶段，项目主身份不再是通用后台模板。

## 技术栈

| 组件 | 当前项目情况 |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.14 |
| MySQL | 8.0 系列；当前驱动为 mysql-connector-j 8.4.0 |
| MyBatis-Plus | 3.5.15 |
| Sa-Token | 1.45.0 |
| Lombok | 1.18.44 |
| Knife4j / OpenAPI | 当前通过 SpringDoc OpenAPI 2.8.6 暴露接口文档 |
| Spring Validation | spring-boot-starter-validation |
| Hutool | 5.8.38 |

## 系统关系

系统调用链必须保持：

```text
前端 React 项目 -> Java 后端 API -> Python AI 服务
```

- 前端只调用 Java 后端 API。
- Java 后端是业务主控层，对前端提供统一 API。
- Java 后端通过 HTTP 调用 Python AI 服务，并传入必要业务上下文。
- Python AI 服务是无状态 AI 能力服务，不直接访问 Java 业务数据库。

## Java 后端职责

- 用户认证与登录态管理。
- RBAC 权限控制。
- 用户、角色、菜单、部门、字典、系统配置等系统管理。
- 文件上传与文件记录管理。
- 材料主数据管理。
- 材料上传记录与处理状态管理。
- 写作任务管理。
- 问答记录管理。
- AI 调用审计。
- 调用 Python AI 服务。
- 统一响应、统一分页、统一异常处理。
- 操作日志、登录日志等审计能力。

Java 后端不应只作为 Python AI 服务的简单转发层。AI 相关业务请求进入 Java 后端后，应先完成认证、权限、参数校验、材料范围控制、业务状态记录和调用审计，再调用 Python AI 服务。

## Python AI 服务边界

Python AI 服务只提供无状态 AI 能力，包括材料解析、片段切分、embedding、材料向量维护、向量检索、大模型生成等。

Python AI 服务不负责：

- 直接访问 Java 业务数据库。
- 管理用户、角色、权限。
- 管理材料主数据。
- 管理写作任务状态。
- 保存 Java 后端展示所需的业务记录和审计日志。

## 项目结构

当前项目是单体 Spring Boot 应用、单 Maven 模块，主要目录如下：

```text
.
├── pom.xml
├── README.md
├── CLAUDE.md
├── AGENTS.md -> CLAUDE.md
├── docs/
│   ├── domain-language.md
│   ├── backend-architecture.md
│   ├── python-ai-service-contract.md
│   ├── BACKEND_PROJECT_DOCS_MIGRATION_SUMMARY.md
│   └── agents/
├── src/main/java/cn/ezios/baseapi/
│   ├── BaseApiAdminApplication.java
│   ├── common/
│   │   ├── enums/
│   │   ├── exception/
│   │   ├── model/
│   │   └── util/
│   ├── framework/
│   │   ├── config/
│   │   ├── handler/
│   │   └── log/
│   └── modules/
│       ├── auth/
│       └── system/
│           ├── config/
│           ├── dept/
│           ├── dict/
│           ├── file/
│           ├── log/
│           ├── menu/
│           ├── role/
│           └── user/
└── src/main/resources/
    ├── application.yml
    └── sql/
        ├── init.sql
        ├── schema.sql
        └── data.sql
```

## 模块分层

现有模块采用包级模块化，每个业务模块通常按以下层次组织：

- `controller`：接口入口、参数校验、响应封装。
- `service`：业务逻辑接口。
- `service.impl`：业务实现。
- `mapper`：数据库访问，基于 MyBatis-Plus。
- `entity`：数据库实体。
- `dto`：请求参数。
- `vo`：返回给前端的数据结构。

## 现有基础模块

### auth

认证登录模块，提供登录、登出、当前用户信息、当前用户菜单等能力。

### system

系统管理模块，继承脚手架能力，包含用户、角色、菜单、部门、字典、系统配置、文件、登录日志、操作日志等。

新业务代码不要污染 `system` 系统模块。材料、写作、问答、AI 调用等业务能力应按领域新增模块。

## 推荐业务模块

后续业务模块可在 `src/main/java/cn/ezios/baseapi/modules/` 下按领域组织：

| 模块 | 说明 |
|---|---|
| `material` | 材料主数据、材料状态、材料处理记录 |
| `writing` | 写作任务、写作结果、写作版本 |
| `qa` | 材料问答记录 |
| `ai` | Python AI 服务调用封装、AI 调用日志、模型配置 |
| `knowledge` | 片段、来源片段、材料向量维护记录，可根据实际情况合并到 `material` 或 `ai` |

如后续已有更明确包结构，应优先保持项目当前风格，不要为了文档建议强行重构。

## 接口约定

- REST API 统一使用 `/api` 前缀。
- 资源路径优先使用单数名词，例如 `/api/system/user`、`/api/system/role`、`/api/material`、`/api/writing/task`。
- 分页接口沿用统一分页结构。
- 响应沿用统一响应结构。
- 异常由全局异常处理统一转换。

### 响应结构示例

```json
{"code": 200, "message": "success", "data": {}}
```

```json
{"code": 200, "message": "success", "data": {"records": [], "total": 100, "page": 1, "pageSize": 10}}
```

```json
{"code": 400, "message": "错误信息", "data": null}
```

## 数据库约定

- 系统管理表继续使用 `sys_` 前缀。
- 材料业务表建议使用 `material_` 前缀。
- 写作任务表建议使用 `writing_` 前缀。
- AI 调用、模型配置、调用日志建议使用 `ai_` 前缀。
- 片段、来源片段、向量维护记录可使用 `kb_`、`material_` 或 `ai_` 前缀，但必须在文档中说明清楚。
- 不要把新业务表全部塞进 `sys_` 前缀。
- 主键优先使用 `BIGINT` 自增。
- 业务删除优先使用逻辑删除，字段沿用 `deleted`。
- 常用字段建议包含 `create_time`、`update_time`、`deleted`、`status`。
- 当前脚手架 SQL 未使用数据库外键，优先通过 Service 层维护业务关系。

## 本地启动

1. 准备 MySQL 数据库，默认连接配置来自 `src/main/resources/application.yml`：

   ```text
   DB_URL 默认值：jdbc:mysql://localhost:3306/base_api?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
   DB_USERNAME：必须通过环境变量提供
   DB_PASSWORD：必须通过环境变量提供
   ```

2. 在 MySQL 中执行初始化脚本。脚本位于 `src/main/resources/sql/`，`init.sql` 会依次加载 `schema.sql` 和 `data.sql`：

   ```sql
   SOURCE src/main/resources/sql/init.sql;
   ```

3. 启动应用：

   ```bash
   mvn spring-boot:run
   ```

   也可以在 IDE 中运行 `cn.ezios.baseapi.BaseApiAdminApplication`。

4. 默认端口为 `8080`。开发环境启用接口文档：

   ```text
   http://localhost:8080/swagger-ui.html
   ```

### 初始化账号

| 账号 | 密码 | 说明 |
|---|---|---|
| `admin` | `admin123` | 超级管理员 |

## 构建

```bash
mvn clean package -DskipTests
java -jar target/base_api_admin-v1.0.0.jar
```

## 开发约定

- 默认使用中文领域术语，详见 `docs/domain-language.md`。
- 新业务模块按领域组织。
- 系统管理能力继承脚手架，不要随意重构。
- 新业务代码不要污染 `sys` 系统模块。
- 不要引入不必要的新依赖。
- 不要改成多模块 Maven，除非用户明确要求。
- 每次完成开发或文档任务后，在 `docs` 目录写入总结文档，至少包含修改文件、实现内容、验证结果、未完成事项。

## 相关文档

- `CLAUDE.md`：AI 编程 Agent 统一入口说明。
- `docs/domain-language.md`：领域语言。
- `docs/backend-architecture.md`：后端架构边界。
- `docs/python-ai-service-contract.md`：Java 后端与 Python AI 服务对接边界。
