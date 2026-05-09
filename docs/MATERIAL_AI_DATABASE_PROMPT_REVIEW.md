# 材料 AI 建表 Prompt 审查结论

本文档用于审查 `tmp/prompt2.md` 中第一版业务建表要求与当前项目约束、Python AI 服务对接文档之间的冲突，并沉淀已确认的修订规则。

本次只形成文档结论，不生成 SQL，不实现 Controller、Service、Mapper、Entity 或业务接口。

`tmp/` 目录只作为临时输入区，不作为项目长期文档或任务资料保留。后续执行建表设计时，应以本文档和 `docs/` 下正式文档为准，不依赖 `tmp/prompt2.md` 是否仍然存在。

## 参考文件

- `tmp/prompt2.md`，仅作为本次审查的临时输入材料
- `docs/domain-language.md`
- `docs/backend-architecture.md`
- `docs/python-ai-service-contract.md`
- `docs/java-integration-guide.md`
- `src/main/resources/sql/schema.sql`
- `src/main/resources/application.yml`

## 与当前系统一致的点

- 当前项目是基于 `base-api` 脚手架演进的 Java 后端项目。
- 系统调用链保持：前端 React 项目 -> Java 后端 API -> Python AI 服务。
- Java 后端负责业务主控、用户权限、材料主数据、写作任务、问答记录和 AI 调用审计。
- Python AI 服务负责材料解析、片段切分、向量检索、大模型生成，不直接访问 Java 业务数据库。
- 业务表使用 `BIGINT` 自增主键。
- 删除优先使用逻辑删除。
- 不使用数据库外键，关系由业务逻辑维护。
- 字段命名使用 `snake_case`。
- 重要查询字段需要加索引。
- 表和字段需要清晰注释。

## 已确认的总体修订规则

### 表名前缀

第一版新增业务表统一使用 `biz_` 前缀，方便与脚手架系统表 `sys_` 区分。

`tmp/prompt2.md` 中 9 张表应调整为：

| prompt2 原表名 | 修订后表名 |
|---|---|
| `material` | `biz_material` |
| `material_process_record` | `biz_material_process_record` |
| `qa_session` | `biz_qa_session` |
| `qa_message` | `biz_qa_message` |
| `qa_session_material` | `biz_qa_session_material` |
| `writing_task` | `biz_writing_task` |
| `writing_task_material` | `biz_writing_task_material` |
| `writing_result` | `biz_writing_result` |
| `ai_call_log` | `biz_ai_call_log` |

### 审计字段

第一版所有 `biz_` 业务表统一包含：

- `create_by BIGINT NULL`
- `update_by BIGINT NULL`

`user_id` 和 `create_by/update_by` 不能互相替代：

- `user_id` 表示业务数据归属，用于权限校验和用户维度查询。
- `create_by/update_by` 表示创建人和最后修改人，用于审计。

### 逻辑删除与唯一约束

建表规范长期遵守：

- 不要在 `deleted` 字段上构造唯一约束。
- 不要使用 `session_id + material_id + deleted`、`task_id + material_id + deleted`、`task_id + version_no + deleted` 作为唯一键。
- 涉及逻辑删除的业务唯一性由 Service 层校验。
- 数据库侧使用普通组合索引支撑查询。

### 状态字段

第一版 `biz_` 业务表的 `status` 统一使用 `VARCHAR(32)` 小写英文枚举。

- 不使用 `TINYINT`。
- 不使用大写枚举。
- `status` 字段应设置为 `NOT NULL`。
- `status` 表达业务流转状态，不表达删除状态。
- 删除统一通过 `deleted=1` 表达，不使用 `status=deleted`。

### TEXT 与 JSON

第一版不使用 MySQL `JSON` 类型。

- 结构化快照字段使用 `TEXT` 存 JSON 字符串，例如 `source_segments_json`、`material_ids_json`。
- 应用层负责序列化、反序列化和格式校验。
- 长正文使用 `LONGTEXT`。
- 普通说明、摘要和错误原因使用 `TEXT`。

### Python 接口优先级

`tmp/prompt2.md` 是建表建议，不是跨服务接口契约。

Java 与 Python 的跨服务字段、枚举和错误语义，以以下文档为准：

- `docs/java-integration-guide.md`
- Python 服务 `/openapi.json`

### chunk 与 segment

采用折中方案：

- Python 接口边界可保留真实字段 `chunk_count`、`chunk_index`。
- Java 的 Python Client 边界 DTO 可以按接口原样接收。
- Java 业务表、Service、VO、文档和前端展示统一使用 `segment_count`、`segment_index`。
- `chunk` 不作为 Java 业务领域命名。

### SQL 文件位置

后续真正新增业务建表 SQL 时，放入现有 SQL 初始化目录：

- `src/main/resources/sql/material_ai_schema_v1.sql`

数据库设计说明和任务总结放入：

- `docs/MATERIAL_AI_DATABASE_DESIGN.md`
- `docs/MATERIAL_AI_DATABASE_DESIGN_SUMMARY.md`

## 表设计修订建议

### biz_material

材料主表保存材料当前业务态。

相对 `tmp/prompt2.md` 需要调整：

- 表名改为 `biz_material`。
- `status` 示例不包含 `deleted`，第一版使用 `processing`、`available`、`failed`。
- 删除只通过 `deleted=1` 表达。
- 通过 `file_id` 关联现有 `sys_file`。
- 保存必要文件快照字段：`original_filename`、`file_type`、`file_size`、`file_md5`、`storage_path`。
- 第一版不新增 `file_url` 字段；如需访问地址，优先从 `sys_file.access_url` 获取。
- 处理失败时，`status=failed`，`segment_count=0`，失败原因写入 `error_message`。

### biz_material_process_record

材料处理记录只记录材料解析、切分、向量写入或覆盖处理。

相对 `tmp/prompt2.md` 需要调整：

- 表名改为 `biz_material_process_record`。
- `process_type` 第一版只使用 `initial`、`reprocess`。
- 不保留 `delete_vector`。
- 删除材料向量不写入本表，只写入 `biz_ai_call_log`。
- 增加本次处理对应的文件快照，至少包含 `file_id`，建议包含 `file_md5`、`original_filename`。

### biz_qa_session

问答会话表记录一次会话，材料范围第一版为会话级固定集合。

相对 `tmp/prompt2.md` 需要调整：

- 表名改为 `biz_qa_session`。
- `status` 使用小写字符串，第一版可使用 `active`、`archived`。
- 会话选择材料通过 `biz_qa_session_material` 维护，不在会话表保存 `material_ids_json`。

### biz_qa_message

问答消息表记录会话中的每条消息。

相对 `tmp/prompt2.md` 需要调整：

- 表名改为 `biz_qa_message`。
- `role` 允许 `user`、`assistant`、`system`。
- `user`、`assistant` 是默认前端会话展示消息。
- `system` 用于记录与本轮问答强相关、需要审计或复现的系统上下文摘要。
- 前端默认不展示 `system` 消息，只在调试、审计、管理员视图或开发模式中按需展示。
- 不要把完整 Prompt、超长材料片段、敏感请求体直接写入 `system` 消息。
- `source_segments_json` 使用 `TEXT` 存 JSON 字符串。
- `biz_qa_message` 不维护消息级 `material_ids_json`。

### biz_qa_session_material

问答会话材料关联表记录会话选择的材料集合。

相对 `tmp/prompt2.md` 需要调整：

- 表名改为 `biz_qa_session_material`。
- 不建 `session_id + material_id + deleted` 唯一约束。
- 使用普通组合索引支撑查询。
- 只保存关系，不保存材料标题、原始文件名等展示快照。
- 展示时实时查询材料主表，材料不存在时前端可降级显示“材料已删除”。

### biz_writing_task

写作任务表保存用户输入、任务参数和任务状态。

相对 `tmp/prompt2.md` 需要调整：

- 表名改为 `biz_writing_task`。
- `writing_type` 与 Python `/generate` 接口保持一致，第一版使用 `outline`、`draft`、`polished`、`title`。
- 不使用 `polish`。
- `status` 使用小写字符串，第一版可使用 `pending`、`running`、`success`、`failed`。
- 不保存 `material_ids_json`。
- 不指定材料时，不插入任务材料关联记录；调用 Python 时不传 `material_ids` 或传 `null`，按当前 `user_id` 的全部材料检索。
- 空 `material_ids` 数组属于参数错误，Java 后端不得向 Python 传空数组。

### biz_writing_task_material

写作任务材料关联表记录写作任务选择的材料集合。

相对 `tmp/prompt2.md` 需要调整：

- 表名改为 `biz_writing_task_material`。
- 不建 `task_id + material_id + deleted` 唯一约束。
- 使用普通组合索引支撑查询。
- 只保存关系，不保存材料标题、原始文件名等展示快照。
- 写作结果溯源展示优先使用 `biz_writing_result.source_segments_json`。

### biz_writing_result

写作结果表保存 AI 输出，并支持后续多版本。

相对 `tmp/prompt2.md` 需要调整：

- 表名改为 `biz_writing_result`。
- 不建 `task_id + version_no + deleted` 唯一约束。
- 使用普通组合索引支撑查询。
- 版本号连续性和未删除版本唯一性由 Service 层校验。
- `source_segments_json` 使用 `TEXT` 存 JSON 字符串。
- `source_segments_json` 是结果快照，不参与权限判断。

### biz_ai_call_log

AI 调用日志表记录 Java 调用 Python AI 服务的日志，便于排错和审计。

相对 `tmp/prompt2.md` 需要调整：

- 表名改为 `biz_ai_call_log`。
- `business_type` 第一版使用固定值：`material_process`、`material_vector_delete`、`writing`、`qa`。
- 增加 `http_status`，记录 Python HTTP 状态。
- 增加 `error_code`，记录 Python 错误码。
- 增加 `source_count`，记录来源片段数量。
- 增加 `trace_id`，记录调用链追踪 ID，可为空。
- 增加 `material_ids_json`，记录本次调用涉及的材料 ID 快照，使用 `TEXT` 存 JSON 字符串。
- 第一版不保存完整请求体和完整响应体，避免超大内容、材料正文或敏感信息进入日志表。
- 优先保存 `request_summary`、`response_summary`、必要 ID 快照、状态和错误摘要。

## 来源片段快照规范

`source_segments_json` 用于保存写作结果或问答回答引用的来源片段快照，可以为空。

Java 业务层保存来源片段快照时使用 `segmentIndex`，不要使用 Python 原始字段 `chunk_index`。

最小字段：

```json
[
  {
    "text": "引用的片段内容",
    "materialId": 1,
    "segmentIndex": 3,
    "score": 0.85
  }
]
```

可选字段：

- `materialTitle`
- `originalFilename`

这些可选字段用于材料被删除或重命名后的历史结果溯源展示。

## 不纳入第一版的内容

- 不建片段表。
- 不实现 Controller、Service、Mapper、Entity。
- 不新增业务接口。
- 不修改现有 `sys_` 表结构。
- 不修改 Java 源码、`pom.xml` 或配置文件。
- 不让 Python AI 服务直接访问 Java 业务数据库。
- 不让前端绕过 Java 后端直接调用 Python AI 服务。

## 后续执行建表时的验证要求

真正执行建表 SQL 和设计文档任务时，应至少检查：

1. `git status`
2. `git diff -- src/main/resources/sql docs`

如果只新增 SQL 和文档，不需要执行 Maven 构建。

如果误改 Java 源码、`pom.xml` 或配置文件，需要说明原因，并执行项目已有验证命令。
