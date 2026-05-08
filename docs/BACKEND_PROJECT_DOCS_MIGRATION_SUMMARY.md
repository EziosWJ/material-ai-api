# 后端项目文档改造总结

## 修改文件

- `README.md`
- `CLAUDE.md`
- `AGENTS.md`
- `docs/domain-language.md`
- `docs/backend-architecture.md`
- `docs/python-ai-service-contract.md`
- `docs/BACKEND_PROJECT_DOCS_MIGRATION_SUMMARY.md`

## 实现内容

- 将项目主身份从通用后台管理模板调整为“本地化智能材料写作平台 Java 后端”。
- 保留项目基于 `base-api` 脚手架演进的来源说明，并弱化脚手架作为当前主身份的表达。
- 明确系统调用链为“前端 React 项目 -> Java 后端 API -> Python AI 服务”。
- 明确 Java 后端是业务主控层，负责认证、权限、材料主数据、文件记录、写作任务、问答记录、业务状态、调用审计、统一响应、统一分页和统一异常处理。
- 明确 Python AI 服务是无状态 AI 能力服务，不直接访问 Java 业务数据库，不管理用户、权限、材料主数据和写作任务状态。
- 新增统一领域语言文档，规范材料、片段、来源片段、写作任务、问答、Prompt 模板、材料向量维护等术语。
- 新增后端架构说明文档，规范模块划分、API 设计、数据库命名、权限与审计约定。
- 新增 Java 后端与 Python AI 服务对接边界文档，规范参数传递、错误映射、调用审计和接口演进原则。
- 将 Agent 统一入口整理到 `CLAUDE.md`，并将 `AGENTS.md` 调整为指向 `CLAUDE.md` 的软链接。

## 验证结果

- 已查看项目真实结构，包括 `README.md`、`AGENTS.md`、`CLAUDE.md`、`pom.xml`、`src/main/resources/application.yml`、`src/main/java`、`src/main/resources`、`docs` 和 `src/main/resources/sql`。
- 本次只改造文档和 Agent 入口软链接，未修改 Java 源码、`pom.xml`、配置文件或 SQL 脚本。
- 已按任务要求执行 `git status`。
- 已按任务要求执行 `git diff -- README.md AGENTS.md CLAUDE.md docs`。
- 本次未执行 Maven 构建，因为变更仅涉及文档和软链接。

## 未完成事项

- 未新增业务接口。
- 未新增 Java 业务功能代码。
- 未修改数据库脚本。
- Python AI 服务的具体 HTTP 接口路径、请求体和响应体仍需在后续接口设计任务中细化。
