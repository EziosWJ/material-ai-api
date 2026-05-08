# 后端架构说明

## 项目定位

当前项目是“本地化智能材料写作平台”的 Java 业务后端，不是纯通用后台管理模板。项目基于 `base-api` 脚手架演进，保留脚手架提供的认证、RBAC、系统管理、文件、日志、统一响应、统一分页、统一异常处理等基础能力。

Java 后端是业务主控层，负责把前端请求转化为受权限、状态和审计约束的业务操作。

## 技术栈

- Java 21。
- Spring Boot 3.x，当前为 3.5.14。
- 单体 Spring Boot 应用。
- 单 Maven 模块。
- MySQL 8.0 系列。
- MyBatis-Plus。
- Sa-Token。
- Lombok。
- Knife4j / OpenAPI，当前通过 SpringDoc OpenAPI 暴露接口文档。
- Spring Validation。
- Hutool。

## 与前端、Python AI 服务的关系

系统调用链必须保持：

```text
前端 React 项目 -> Java 后端 API -> Python AI 服务
```

- 前端只调用 Java 后端 API。
- Java 后端通过 HTTP 调用 Python AI 服务。
- Python AI 服务是无状态 AI 能力服务。
- Python AI 服务不直接访问 Java 业务数据库。

Java 后端不应把 AI 相关请求简单透传给 Python，应负责权限校验、参数校验、材料范围控制、业务状态记录和调用审计。

## Java 后端职责

- 用户认证与登录态管理。
- RBAC 权限控制。
- 用户、角色、菜单、部门、字典、系统配置等系统管理。
- 文件上传与文件记录。
- 材料主数据、材料状态、材料处理记录。
- 写作任务、写作结果、写作版本。
- 问答记录、材料范围控制、结果保存。
- AI 调用记录、模型配置、Python 服务调用封装。
- 操作日志、登录日志、AI 调用审计。
- 统一认证、权限、日志、异常、分页和响应格式。

## Java 后端不负责的事情

- 不承担材料解析、核心切分算法、embedding、向量检索和大模型生成等 AI 算法能力。
- 不直接拼散落在多个层级的 Prompt 模板逻辑。
- 不让前端绕过 Java 后端直接调用 Python AI 服务。
- 不让 Python AI 服务管理用户、权限、材料主数据和写作任务状态。
- 不把新业务表全部放入 `sys_` 前缀。

## 推荐业务模块划分

| 模块 | 说明 |
|---|---|
| `system` | 系统管理，继承脚手架能力 |
| `auth` | 登录认证与会话 |
| `file` | 文件上传与文件记录，当前在 `system.file` 中继承脚手架能力 |
| `material` | 材料主数据、材料状态、材料处理记录 |
| `writing` | 写作任务、写作结果、写作版本 |
| `qa` | 材料问答记录 |
| `ai` | Python AI 服务调用封装、AI 调用日志、模型配置 |
| `knowledge` | 片段、来源片段、材料向量维护记录，可根据实际情况合并到 `material` 或 `ai` 模块 |

当前包根为 `cn.ezios.baseapi`，已有模块位于 `src/main/java/cn/ezios/baseapi/modules/`。后续新增业务模块应按领域放在该目录下，不要强行重构现有 `auth` 和 `system`。

## API 设计约定

- REST API 统一使用 `/api` 前缀。
- 资源路径优先使用单数名词，例如 `/api/system/user`、`/api/system/role`、`/api/material`、`/api/writing/task`。
- 入参 DTO 使用 Spring Validation 做参数校验。
- 返回结构使用项目统一响应模型。
- 分页接口使用项目统一分页结构。
- 业务异常交给统一异常处理。
- AI 相关接口必须先完成 Java 侧权限校验和材料范围控制，再调用 Python。

## 数据库设计约定

- 系统管理表继续使用 `sys_` 前缀。
- 材料业务表建议使用 `material_` 前缀。
- 写作任务表建议使用 `writing_` 前缀。
- AI 调用、模型配置、调用日志建议使用 `ai_` 前缀。
- 片段、来源片段、向量维护记录可使用 `kb_`、`material_` 或 `ai_` 前缀，但必须在设计文档中说明清楚。
- 主键优先使用 `BIGINT` 自增。
- 业务删除优先使用逻辑删除。
- 常用字段建议包含 `create_time`、`update_time`、`deleted`、`status`。
- 当前脚手架 SQL 未使用数据库外键，优先通过 Service 层维护关系。
- 字段命名使用 `snake_case`，Java/JSON 字段使用 `camelCase`。

## 权限与审计约定

- 用户登录态和权限判断由 Java 后端统一处理。
- 材料、写作任务、问答记录等业务数据必须校验用户权限和材料范围。
- 调用 Python AI 服务前，Java 后端应记录必要业务上下文。
- Python 调用失败时，Java 后端应保存失败状态和错误原因。
- 关键写操作优先沿用现有操作日志能力。
- AI 调用应设计独立调用审计，记录用户、材料、任务、请求参数摘要、响应状态、耗时和错误信息。

## 后续开发建议

- 先完善领域模型和接口契约，再新增业务代码。
- 新业务模块优先遵守现有 `controller`、`service`、`service.impl`、`mapper`、`entity`、`dto`、`vo` 分层。
- 系统管理能力继承脚手架，不要随意重构认证、权限、统一响应、统一异常和系统管理模块。
- Python AI 服务调用封装建议集中在 `ai` 模块，避免散落在多个业务 Service 中。
- 对外展示使用中文领域术语，代码英文命名参考 `docs/domain-language.md`。
