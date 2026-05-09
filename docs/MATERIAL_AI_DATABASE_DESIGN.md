# 材料 AI 数据模型说明

本文档说明 `src/main/resources/sql/material_ai_schema_v1.sql` 中第一版材料 AI 业务数据模型。该模型服务于“本地化智能材料写作平台”Java 后端，覆盖材料、问答、写作任务和 AI 调用审计。Java 后端仍是业务主控层，Python AI 服务只提供材料解析、片段切分、向量维护、检索和生成能力。

## 设计原则

- 第一版新增业务表统一使用 `biz_` 前缀，与脚手架系统表 `sys_` 区分。
- 所有业务主键使用 `BIGINT` 自增。
- 不建立数据库外键，关系由 Java Service 层维护。
- 业务删除使用 `deleted` 逻辑删除字段，`status` 只表达业务流转状态。
- `status` 使用 `VARCHAR(32)` 小写英文枚举，不使用 `TINYINT` 或大写枚举。
- 业务归属使用 `user_id`；审计填充使用 `create_by`、`update_by`，两类字段不能互相替代。
- 结构化快照字段使用 `TEXT` 保存 JSON 字符串，第一版不使用 MySQL `JSON` 类型。
- 长正文使用 `LONGTEXT`，普通摘要、说明和错误信息使用 `TEXT`。
- 不在 `deleted` 上构造唯一约束；涉及逻辑删除的业务唯一性由 Java Service 层校验。

## 表总览

| 表名 | 职责 | 所属领域 |
|---|---|---|
| `biz_material` | 保存材料当前业务态、文件快照和处理结果摘要 | 材料 |
| `biz_material_process_record` | 保存材料解析、切分、向量写入或覆盖处理流水 | 材料 |
| `biz_qa_session` | 保存问答会话及会话级状态 | 问答 |
| `biz_qa_message` | 保存问答会话中的用户、助手和系统消息 | 问答 |
| `biz_qa_session_material` | 保存问答会话选择的材料集合 | 问答 |
| `biz_writing_task` | 保存写作任务输入、参数和任务状态 | 写作 |
| `biz_writing_task_material` | 保存写作任务选择的材料集合 | 写作 |
| `biz_writing_result` | 保存写作输出内容、版本和来源片段快照 | 写作 |
| `biz_ai_call_log` | 保存 Java 调用 Python AI 服务的审计日志 | AI 调用 |

## 通用字段语义

| 字段 | 语义 |
|---|---|
| `id` | 表内自增主键。 |
| `user_id` | 业务数据所属用户，用于权限校验、材料范围控制和用户维度查询。 |
| `create_time`、`update_time` | 业务数据创建和更新时间。 |
| `create_by`、`update_by` | 审计字段，表示创建人和最后修改人。 |
| `deleted` | 逻辑删除标记，`0` 表示正常，`1` 表示已删除。 |
| `status` | 当前业务流转状态，不表达删除。 |
| `error_message` | 最近一次失败原因或调用错误信息，供排查和前端展示失败摘要。 |

## 9 张业务表说明

### biz_material

`biz_material` 是材料主表，保存材料当前业务状态和文件快照。材料由 Java 后端管理，文件实体通过 `file_id` 关联现有 `sys_file`，但本表会冗余必要文件快照，避免历史材料展示完全依赖文件表当前状态。

核心字段：

| 字段 | 语义 |
|---|---|
| `user_id` | 材料所属用户。 |
| `title` | 材料标题，用于业务展示和检索入口。 |
| `original_filename` | 上传时原始文件名快照。 |
| `file_id` | 关联 `sys_file.id`。 |
| `file_type` | 文件类型，如 `pdf`、`docx`、`txt`。 |
| `file_size` | 文件大小，单位字节。 |
| `file_md5` | 文件 MD5 快照，用于处理记录追溯和重复判断扩展。 |
| `storage_path` | 文件存储路径快照。 |
| `status` | 材料状态：`processing`、`available`、`failed`。 |
| `segment_count` | 当前可用片段数量。 |
| `last_process_time` | 最近一次成功或完成处理的时间。 |
| `error_message` | 最近一次材料处理失败原因。 |

材料首次创建后默认为 `processing`。Python 处理成功后，Java 更新为 `available` 并写入 `segment_count`；处理失败时更新为 `failed`、`segment_count=0`，失败原因写入 `error_message`。

### biz_material_process_record

`biz_material_process_record` 记录材料解析、切分、向量写入或覆盖处理流水。它不是材料主表，也不记录材料向量删除；材料向量删除只进入 `biz_ai_call_log`。

核心字段：

| 字段 | 语义 |
|---|---|
| `material_id` | 对应 `biz_material.id`。 |
| `user_id` | 材料所属用户。 |
| `file_id` | 本次处理对应的 `sys_file.id`。 |
| `file_md5` | 本次处理文件 MD5 快照。 |
| `original_filename` | 本次处理原始文件名快照。 |
| `process_type` | 处理类型：`initial`、`reprocess`。 |
| `status` | 处理状态：`processing`、`success`、`failed`。 |
| `deleted_count` | 覆盖处理时 Python 删除的旧片段数量。 |
| `segment_count` | 本次新写入片段数量。 |
| `started_at`、`finished_at`、`duration_ms` | 处理开始、结束和耗时。 |

该表用于还原材料处理历史，尤其是重新处理、覆盖处理、失败重试和排查 Python 返回结果时的文件版本。

### biz_qa_session

`biz_qa_session` 保存问答会话。第一版问答材料范围是会话级固定集合，通过 `biz_qa_session_material` 维护，不在会话表保存 `material_ids_json`。

核心字段：

| 字段 | 语义 |
|---|---|
| `user_id` | 会话所属用户。 |
| `title` | 会话标题。 |
| `status` | 会话状态：`active`、`archived`。 |
| `last_message_time` | 最近消息时间，用于会话列表排序。 |
| `message_count` | 会话消息数量，便于列表展示和统计。 |

### biz_qa_message

`biz_qa_message` 保存问答会话中的每条消息。默认前端展示 `user` 和 `assistant` 消息；`system` 消息仅用于记录与本轮问答强相关、需要审计或复现的系统上下文摘要。

核心字段：

| 字段 | 语义 |
|---|---|
| `session_id` | 对应 `biz_qa_session.id`。 |
| `user_id` | 消息所属用户。 |
| `role` | 消息角色：`user`、`assistant`、`system`。 |
| `content` | 消息正文，使用 `LONGTEXT`。 |
| `source_segments_json` | 助手回答引用的来源片段快照 JSON 字符串。 |
| `model_name` | 本次回答使用的模型名称。 |
| `ai_call_log_id` | 对应 `biz_ai_call_log.id`，用于追溯本轮 Python 调用。 |

`system` 消息不得保存完整 Prompt、超长材料片段或敏感请求体。完整调用排查信息应优先使用 `biz_ai_call_log.request_summary`、`response_summary` 等摘要字段。

### biz_qa_session_material

`biz_qa_session_material` 保存问答会话和材料的关联关系。它只保存关系，不保存材料标题、原始文件名等展示快照。

核心字段：

| 字段 | 语义 |
|---|---|
| `session_id` | 对应 `biz_qa_session.id`。 |
| `material_id` | 对应 `biz_material.id`。 |
| `user_id` | 关系所属用户，用于权限过滤和查询。 |

展示会话材料时实时查询 `biz_material`。如果材料已删除，前端可降级显示“材料已删除”。

### biz_writing_task

`biz_writing_task` 保存用户发起的写作任务，包括任务输入、写作类型和执行状态。材料范围通过 `biz_writing_task_material` 维护，不在任务表保存 `material_ids_json`。

核心字段：

| 字段 | 语义 |
|---|---|
| `user_id` | 任务所属用户。 |
| `title` | 任务标题。 |
| `writing_type` | 写作类型：`outline`、`draft`、`polished`、`title`。 |
| `topic` | 写作主题。 |
| `requirement` | 写作要求。 |
| `input_content` | 润色类任务的原始内容。 |
| `status` | 任务状态：`pending`、`running`、`success`、`failed`。 |
| `started_at`、`finished_at` | 任务开始和结束时间。 |

不指定材料时，不插入任务材料关联记录；Java 调用 Python 时不传 `material_ids` 或传 `null`，表示按当前 `user_id` 的全部材料检索。空数组 `[]` 属于参数错误，Java 后端不得向 Python 传空数组。

### biz_writing_task_material

`biz_writing_task_material` 保存写作任务和材料的关联关系。它只保存关系，不保存材料展示快照。

核心字段：

| 字段 | 语义 |
|---|---|
| `task_id` | 对应 `biz_writing_task.id`。 |
| `material_id` | 对应 `biz_material.id`。 |
| `user_id` | 关系所属用户，用于权限过滤和查询。 |

写作结果的历史溯源展示优先使用 `biz_writing_result.source_segments_json`，而不是任务材料关联表。

### biz_writing_result

`biz_writing_result` 保存 AI 生成的写作结果，并预留多版本能力。任务状态表示一次任务是否执行成功，结果表保存具体产物。

核心字段：

| 字段 | 语义 |
|---|---|
| `task_id` | 对应 `biz_writing_task.id`。 |
| `user_id` | 结果所属用户。 |
| `version_no` | 结果版本号。版本连续性和未删除版本唯一性由 Service 层校验。 |
| `content` | 生成内容，使用 `LONGTEXT`。 |
| `source_segments_json` | 写作结果引用的来源片段快照 JSON 字符串。 |
| `model_name` | 生成使用的模型名称。 |
| `ai_call_log_id` | 对应 `biz_ai_call_log.id`，用于追溯 Python 调用。 |

`source_segments_json` 是结果快照，不参与权限判断，也不替代任务材料关联关系。

### biz_ai_call_log

`biz_ai_call_log` 保存 Java 调用 Python AI 服务的审计日志，覆盖材料处理、材料向量删除、写作和问答。

核心字段：

| 字段 | 语义 |
|---|---|
| `user_id` | 调用用户 ID。 |
| `business_type` | 业务类型：`material_process`、`material_vector_delete`、`writing`、`qa`。 |
| `business_id` | 业务 ID，如材料 ID、写作任务 ID 或问答会话 ID。 |
| `endpoint` | Python 服务接口路径。 |
| `model_name` | 调用模型名称。 |
| `material_ids_json` | 本次调用实际涉及的材料 ID 快照 JSON 字符串。 |
| `request_summary` | 请求摘要，不保存完整请求体。 |
| `response_summary` | 响应摘要，不保存完整响应体。 |
| `status` | 调用状态：`success`、`failed`。 |
| `http_status` | Python HTTP 状态码。 |
| `error_code` | Python 错误码。 |
| `error_message` | 错误信息。 |
| `source_count` | 来源片段数量。 |
| `trace_id` | 调用链追踪 ID。 |
| `started_at`、`finished_at`、`duration_ms` | 调用开始、结束和耗时。 |

第一版不保存完整请求体和完整响应体，避免超大内容、材料正文或敏感信息进入日志表。

## 表关系

```text
sys_file 1 -- n biz_material

biz_material 1 -- n biz_material_process_record

biz_qa_session 1 -- n biz_qa_message
biz_qa_session 1 -- n biz_qa_session_material
biz_material 1 -- n biz_qa_session_material
biz_ai_call_log 1 -- 0..1 biz_qa_message

biz_writing_task 1 -- n biz_writing_task_material
biz_material 1 -- n biz_writing_task_material
biz_writing_task 1 -- n biz_writing_result
biz_ai_call_log 1 -- 0..1 biz_writing_result
```

这些关系不通过数据库外键强制约束。Java Service 层负责：

- 校验 `user_id` 与业务记录归属一致。
- 校验材料状态可用后才进入问答或写作范围。
- 维护关联表的新增、逻辑删除和重复关系检查。
- 在写入问答回答或写作结果时关联对应 AI 调用日志。

## 状态流转

### 材料状态

```text
processing -> available
processing -> failed
failed -> processing -> available
available -> processing -> available
available -> processing -> failed
```

- 新建材料后进入 `processing`。
- 首次处理成功进入 `available`。
- 处理失败进入 `failed`，并写入失败原因。
- 重新处理时从 `failed` 或 `available` 回到 `processing`。
- 删除材料不改变 `status` 语义，只设置 `deleted=1`，并通过 Python 完成材料向量删除。

### 材料处理记录状态

```text
processing -> success
processing -> failed
```

材料处理记录只表达一次处理流水。材料向量删除不是材料处理记录，记录在 `biz_ai_call_log`。

### 问答会话状态

```text
active -> archived
archived -> active
```

`active` 表示正常会话，`archived` 表示归档会话。消息是否删除由消息表 `deleted` 控制。

### 写作任务状态

```text
pending -> running -> success
pending -> running -> failed
failed -> running -> success
failed -> running -> failed
```

`pending` 表示任务已创建但未开始调用 Python。`running` 表示 Java 已进入执行流程。`success` 表示至少成功保存一条写作结果。`failed` 表示调用、生成或保存失败。

### AI 调用日志状态

```text
success
failed
```

AI 调用日志记录一次 Java 到 Python 的调用结果。HTTP 错误、Python 业务错误和 Java 侧调用异常均应落为 `failed`，并尽量保存 `http_status`、`error_code`、`error_message` 和 `trace_id`。

## 索引策略

第一版索引以常用查询路径为主，不建立唯一索引约束业务规则。

| 表名 | 索引策略 |
|---|---|
| `biz_material` | 按 `user_id` 查询用户材料；按 `file_id` 追溯文件；按 `status` 查询处理状态；按 `create_time` 排序；按 `deleted` 过滤逻辑删除。 |
| `biz_material_process_record` | 按 `material_id` 查处理历史；按 `user_id` 做权限过滤；按 `file_id` 追溯文件；按 `status` 查失败或处理中记录；按 `create_time` 排序。 |
| `biz_qa_session` | 按 `user_id` 查询会话；按 `status` 区分活跃和归档；按 `update_time` 排序；按 `deleted` 过滤。 |
| `biz_qa_message` | 按 `session_id` 查询会话消息；按 `user_id` 做权限过滤；按 `role` 筛选展示或审计消息；按 `ai_call_log_id` 追溯调用；按 `create_time` 排序。 |
| `biz_qa_session_material` | 使用 `(session_id, material_id, deleted)` 支撑会话材料查询和重复校验；按 `material_id` 反查引用；按 `user_id` 做权限过滤。 |
| `biz_writing_task` | 按 `user_id` 查询用户任务；按 `writing_type` 分类；按 `status` 查询任务状态；按 `create_time` 排序。 |
| `biz_writing_task_material` | 使用 `(task_id, material_id, deleted)` 支撑任务材料查询和重复校验；按 `material_id` 反查引用；按 `user_id` 做权限过滤。 |
| `biz_writing_result` | 按 `task_id` 查询任务结果；按 `user_id` 做权限过滤；使用 `(task_id, version_no, deleted)` 支撑版本查询；按 `ai_call_log_id` 追溯调用；按 `create_time` 排序。 |
| `biz_ai_call_log` | 按 `user_id` 查询用户调用；按 `(business_type, business_id)` 追溯业务调用；按 `status` 查失败；按 `http_status` 排查 HTTP 异常；按 `trace_id` 串联链路；按 `create_time` 排序。 |

虽然部分组合索引包含 `deleted`，但它们都是普通索引，不是唯一索引。未删除关系或版本唯一性必须由 Java Service 层处理。

## source_segments_json 规范

`source_segments_json` 用于保存问答回答或写作结果引用的来源片段快照。该字段是历史展示快照，不是权限判断依据，也不是材料关系表。

推荐 JSON 结构为数组：

```json
[
  {
    "text": "来源片段正文",
    "materialId": 1001,
    "segmentIndex": 3,
    "score": 0.87,
    "materialTitle": "材料标题",
    "originalFilename": "example.pdf"
  }
]
```

字段说明：

| JSON 字段 | 必填 | 语义 |
|---|---|---|
| `text` | 是 | 来源片段正文。 |
| `materialId` | 是 | 来源材料 ID。 |
| `segmentIndex` | 是 | Java 业务领域中的片段序号。Python 返回 `chunk_index` 时，应在边界层转换为 `segmentIndex`。 |
| `score` | 是 | 相关度分数，用于排序、调试或低置信度提示。 |
| `materialTitle` | 否 | 材料标题快照，用于材料重命名或删除后的历史展示。 |
| `originalFilename` | 否 | 原始文件名快照，用于历史展示。 |

保存规则：

- 字段为空表示没有来源片段或本次生成未返回来源片段。
- 应用层负责序列化、反序列化和结构校验。
- 不保存 Python 接口中的 `chunk_index`、`chunk_count` 等字段到 Java 业务快照。
- 不把来源片段快照作为权限判断依据。访问问答或写作结果时，仍以记录 `user_id`、会话或任务归属进行校验。
- 来源片段快照可在材料被删除或重命名后继续用于历史结果展示。

## 不单独建立片段表的原因

第一版不建立 Java 业务库片段表，主要原因如下：

- 片段由 Python AI 服务解析和切分产生，并进入向量库；向量库中的片段才是检索事实源。
- Java 业务库如果同步保存完整片段，容易与 Python 向量库形成两套片段事实源，带来一致性维护成本。
- 当前 Java 后端的业务职责是材料状态、处理流水、写作任务、问答记录和调用审计，不承担核心切分算法和向量检索。
- 问答和写作需要展示的只是“本次结果命中的来源片段”，可通过 `source_segments_json` 保存快照满足溯源需求。
- 避免在第一版引入片段增删改查、重切分版本、向量同步状态等复杂模型。

后续如果需要前端浏览材料片段列表，可以新增“片段展示缓存表”。该表必须明确不是向量检索事实源，只用于展示缓存或调试视图，并需要定义与 Python 向量库的同步策略。

## Java 与 Python 边界

Java 后端职责：

- 管理用户、权限、材料主数据、文件记录、问答记录、写作任务和写作结果。
- 在调用 Python 前完成登录态校验、权限校验、材料范围控制和参数校验。
- 显式向 Python 传入 `user_id`、`material_id`、`material_ids`、写作任务参数等业务上下文。
- 保存材料处理状态、处理记录、业务结果和 AI 调用日志。
- 将 Python 边界字段转换为 Java 领域语言，例如将 `chunk_index` 转为 `segmentIndex`。

Python AI 服务职责：

- 解析材料、切分片段、维护 embedding 和向量数据。
- 根据 Java 传入的用户和材料上下文进行向量检索。
- 根据检索片段和任务参数生成写作内容或问答回答。
- 返回处理结果、来源片段、模型信息和错误信息。

边界约束：

- 前端只调用 Java 后端 API，不直接调用 Python AI 服务。
- Python 不直接访问 Java 业务数据库。
- Java 不是简单透传层，必须保存业务状态、审计记录和结果数据。
- Java 与 Python 的跨服务字段、枚举和错误语义以正式接口契约为准；建表文档只描述 Java 业务库如何持久化必要数据。
- Python 接口中存在的 `chunk_*` 字段只允许出现在边界 DTO 或兼容说明中，进入 Java 业务表、Service、VO、文档和前端展示后统一使用 `segment_*` 语义。
