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
- Java 与 Python 的跨服务字段、枚举和错误语义，以 Python 服务提供的 `docs/java-integration-guide.md` 和 `/openapi.json` 为准；临时 prompt 或建表建议不能覆盖真实接口契约。
- 写作类型 `writing_type` 与 Python `/generate` 接口保持一致，第一版使用 `outline`、`draft`、`polished`、`title`，不要使用 `polish`。
- Python 接口中已有的 `chunk_count`、`chunk_index` 等字段在 Java Python Client 边界层可以原样接收；进入 Java 业务表、Service、VO、文档和前端展示后统一转换为 `segment_count`、`segment_index`，避免 Python 实现术语反向污染 Java 业务领域。

## 来源片段快照约定

- `source_segments_json` 用于保存写作结果或问答回答引用的来源片段快照，可以为空。
- Java 业务层保存来源片段快照时使用 `segmentIndex`，不要使用 Python 原始字段 `chunk_index`。
- 来源片段快照最小字段为 `text`、`materialId`、`segmentIndex`、`score`。
- 来源片段快照可选保存 `materialTitle`、`originalFilename`，用于材料被删除或重命名后的历史结果溯源展示。
- `source_segments_json` 是结果快照，不作为材料关系表使用，不参与权限判断。
- 第一版不单独建立片段表；Java 后端只保存材料状态、处理流水和来源片段快照，避免 Java 业务库与 Python 向量库形成两套片段事实源。
- 后续如果需要前端浏览材料片段列表，可新增片段展示缓存表，并明确它不是向量检索的事实源。

## 数据库设计约定

- 系统管理表继续使用 `sys_` 前缀。
- 本平台第一版新增业务表统一使用 `biz_` 前缀，表名第二段表达领域，例如 `biz_material`、`biz_writing_task`、`biz_qa_session`、`biz_ai_call_log`。
- 不再为第一版业务表混用 `material_`、`writing_`、`ai_` 等多套表前缀，避免和 `sys_` 系统表之外再形成多套命名规则。
- 主键优先使用 `BIGINT` 自增。
- 业务删除优先使用逻辑删除。
- 常用字段建议包含 `create_time`、`update_time`、`deleted`、`status`。
- 第一版 `biz_` 业务表统一包含 `create_by`、`update_by`，与现有 MyBatis-Plus 自动填充机制保持一致。
- `user_id` 表示业务数据归属，用于权限校验和用户维度查询；`create_by`、`update_by` 表示创建人和最后修改人，用于审计，二者不能互相替代。
- `status` 表达业务流转状态，不表达删除状态；删除统一通过 `deleted=1` 表达，避免 `status=deleted` 与逻辑删除字段出现双重语义。
- 第一版 `biz_` 业务表的 `status` 统一使用 `VARCHAR(32)` 小写英文枚举，不使用 `TINYINT` 或大写枚举；现有 `sys_` 系统表状态字段不受此约束影响。
- `status` 字段应设置为 `NOT NULL`；业务主表可设置明确默认值，例如材料默认 `processing`、问答会话默认 `active`、写作任务默认 `pending`。
- 当前脚手架 SQL 未使用数据库外键，优先通过 Service 层维护关系。
- 字段命名使用 `snake_case`，Java/JSON 字段使用 `camelCase`。
- 业务表不要在逻辑删除字段 `deleted` 上构造唯一约束，例如不要使用 `session_id + material_id + deleted`、`task_id + material_id + deleted`、`task_id + version_no + deleted` 作为唯一键，避免同一业务关系或版本多次创建、删除后与已删除历史记录冲突。
- 涉及逻辑删除的业务唯一性优先由 Service 层校验；数据库侧使用普通组合索引支撑查询。
- 第一版结构化快照字段采用 `TEXT` 存 JSON 字符串，例如 `source_segments_json`、`material_ids_json`；不使用 MySQL `JSON` 类型，应用层负责序列化、反序列化和格式校验。
- 长正文使用 `LONGTEXT`，普通说明、摘要和错误原因使用 `TEXT`，避免字段长度过小导致写作结果、问答内容或错误详情被截断。
- 材料处理记录应保存本次处理对应的文件快照，至少包含 `file_id`，建议同时保存 `file_md5`、`original_filename`，用于还原重新处理、覆盖处理或处理失败时对应的文件版本。
- 材料处理记录只记录材料解析、切分、向量写入或覆盖处理；第一版 `process_type` 只使用 `initial`、`reprocess`。
- 删除材料向量不写入材料处理记录，只写入 `biz_ai_call_log`。
- 材料主表通过 `file_id` 关联现有 `sys_file`，并保存必要文件快照字段：`original_filename`、`file_type`、`file_size`、`file_md5`、`storage_path`。
- 材料主表第一版不新增 `file_url` 字段；如需访问地址，优先通过 `sys_file.access_url` 获取，避免与脚手架文件模块命名和访问策略不一致。
- 材料首次处理或重新处理失败时，材料主表置为 `status=failed`，`segment_count=0`，失败原因写入 `error_message`；历史成功片段数量从最近一条成功的材料处理记录追溯。

## 权限与审计约定

- 用户登录态和权限判断由 Java 后端统一处理。
- 材料、写作任务、问答记录等业务数据必须校验用户权限和材料范围。
- 调用 Python AI 服务前，Java 后端应记录必要业务上下文。
- Python 调用失败时，Java 后端应保存失败状态和错误原因。
- 关键写操作优先沿用现有操作日志能力。
- AI 调用应设计独立调用审计，记录用户、材料、任务、请求参数摘要、响应状态、耗时和错误信息。
- 第一版 `biz_ai_call_log` 应在基础调用信息之外记录 `http_status`、`error_code`、`source_count`、`trace_id`、`material_ids_json`，用于排查 Python HTTP 错误、来源片段数量和一次调用链路。
- 第一版不建议保存完整请求体和完整响应体，避免超大内容、材料正文或敏感信息进入日志表；优先保存 `request_summary`、`response_summary` 和必要 ID 快照。
- 第一版 `biz_ai_call_log.business_type` 使用固定值：`material_process`、`material_vector_delete`、`writing`、`qa`。

## 问答记录约定

- 第一版问答消息角色允许 `user`、`assistant`、`system`。
- `user`、`assistant` 是默认前端会话展示消息。
- `system` 用于记录与本轮问答强相关、需要审计或复现的系统上下文摘要。
- 前端默认不展示 `system` 消息，只在调试、审计、管理员视图或开发模式中按需展示。
- 不要把完整 Prompt、超长材料片段、敏感请求体直接写入 `system` 消息；这些内容应进入 `ai_call_log.request_summary` 或后续专门调用详情结构。
- 第一版问答材料范围采用会话级固定材料集合，通过 `biz_qa_session_material` 维护；`biz_qa_message` 不维护消息级 `material_ids_json`。
- 每次调用 Python 实际使用的材料 ID 快照由 `biz_ai_call_log.material_ids_json` 记录，用于审计和排查。
- `biz_qa_session_material` 只保存会话与材料的关系，不保存材料标题、原始文件名等展示快照；展示时实时查询材料主表，材料不存在时前端可降级显示“材料已删除”。

## 写作任务约定

- 第一版写作任务材料范围通过 `biz_writing_task_material` 维护，`biz_writing_task` 不保存 `material_ids_json`。
- 每次调用 Python 实际使用的材料 ID 快照由 `biz_ai_call_log.material_ids_json` 记录，用于审计和排查。
- 写作任务不指定材料时，不插入任务材料关联记录；调用 Python 时不传 `material_ids` 或传 `null`，按当前 `user_id` 的全部材料检索。
- 空 `material_ids` 数组属于参数错误，Java 后端不得向 Python 传空数组。
- `biz_writing_task_material` 只保存任务与材料的关系，不保存材料标题、原始文件名等展示快照；写作结果溯源展示优先使用 `biz_writing_result.source_segments_json`。

## 后续开发建议

- 先完善领域模型和接口契约，再新增业务代码。
- 新业务模块优先遵守现有 `controller`、`service`、`service.impl`、`mapper`、`entity`、`dto`、`vo` 分层。
- 系统管理能力继承脚手架，不要随意重构认证、权限、统一响应、统一异常和系统管理模块。
- Python AI 服务调用封装建议集中在 `ai` 模块，避免散落在多个业务 Service 中。
- 对外展示使用中文领域术语，代码英文命名参考 `docs/domain-language.md`。
- 后续真正新增业务建表 SQL 时，放入现有 SQL 初始化目录 `src/main/resources/sql/`，例如 `src/main/resources/sql/material_ai_schema_v1.sql`；数据库设计说明和任务总结放入 `docs/`。
