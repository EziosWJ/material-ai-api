# Java 后端与 Python AI 服务对接边界

## Python AI 服务定位

Python AI 服务是无状态 AI 能力服务，供 Java 后端通过 HTTP 调用。它提供材料解析、片段切分、embedding、材料向量维护、向量检索、大模型生成等能力。

Python AI 服务不直接访问 Java 业务数据库，也不作为前端调用入口。

## Java 调用 Python 的基本原则

- Java 后端调用 Python 服务时必须携带 `user_id`。
- 材料相关调用必须携带 `material_id` 或 `material_ids`。
- Java 后端负责把业务上下文传给 Python AI 服务。
- Java 后端负责认证、权限、参数校验、材料范围控制、业务状态记录和调用审计。
- Java 后端负责保存 Python 返回后的业务记录、调用日志和展示所需数据。
- Python AI 服务只返回能力结果，不决定 Java 业务状态的最终持久化方式。
- Java 后端第一版新增业务表统一使用 `biz_` 前缀，例如 `biz_material`、`biz_writing_task`、`biz_qa_session`、`biz_ai_call_log`；Python 服务不感知 Java 表名前缀。
- Java 与 Python 的跨服务字段、枚举和错误语义，以 Python 服务提供的 `docs/java-integration-guide.md` 和 `/openapi.json` 为准；建表 prompt 只能作为 Java 侧数据模型建议。

## Python 服务不负责的事情

- 不管理用户、角色、权限。
- 不管理材料主数据。
- 不管理写作任务状态。
- 不保存 Java 后端审计日志。
- 不直接访问 Java 业务数据库。
- 不替代 Java 后端对前端提供统一 API。

## 推荐接口方向

具体接口路径以后续设计为准，但建议按能力边界组织：

| 能力 | 建议方向 | 说明 |
|---|---|---|
| 材料处理 | 解析材料、切分片段、写入或替换向量 | 同一材料重新处理时采用覆盖语义 |
| 材料删除 | 按 `user_id + material_id` 清理向量 | Java 删除材料或撤销材料时调用 |
| 写作生成 | 基于写作任务参数和材料范围生成内容 | 返回生成结果与来源片段 |
| 问答 | 基于问题和材料范围检索并生成回答 | 返回回答与来源片段 |
| 健康检查 | 检查 Python 服务和依赖状态 | 可用于 Java 侧运维诊断 |

## 参数传递原则

- Java 后端调用 Python 服务时必须携带 `user_id`。
- 材料相关调用必须携带 `material_id` 或 `material_ids`。
- 未指定 `material_ids` 时按用户范围检索。
- 指定非空 `material_ids` 时按 `user_id + material_ids` 精确检索。
- 空 `material_ids` 列表属于参数错误。
- 材料处理接口采用覆盖语义：同一材料重新处理时，先清理旧片段再写入新片段。
- 材料删除时，Java 调用 Python 端点按 `user_id + material_id` 清理向量。
- Python 生成和问答响应应返回 `sources`，即来源片段列表。
- `material_id` 不承诺全局唯一，Python 侧应结合 `user_id` 限定范围。
- Java 后端业务库中的材料主键仍优先使用 `BIGINT` 自增 ID；调用 Python 时可传该 ID 作为 `material_id`，Python 侧不得只依赖 `material_id` 做隔离，必须按 `user_id + material_id` 限定范围。
- Python 接口中已有的 `chunk_count`、`chunk_index` 等字段属于跨服务接口字段，Java Python Client 边界层可以原样接收；Java 业务库、业务 Service、VO、文档和前端展示统一使用 `segment_count`、`segment_index` 等领域命名。
- Java 后端业务库中的材料主键仍优先使用 `BIGINT` 自增 ID；调用 Python 时可传该 ID 作为 `material_id`，Python 侧不得只依赖 `material_id` 做隔离，必须按 `user_id + material_id` 限定范围。

## 来源片段返回建议

`sources` 建议至少包含以下信息：

| 字段 | 说明 |
|---|---|
| `material_id` | 材料 ID |
| `segment_index` | 片段序号 |
| `content` | 片段正文或摘要 |
| `score` | 相关度 |
| `metadata` | 可选元数据 |

缺失材料 ID 或相关度时属于溯源降级，不应导致整个写作任务或问答失败。Java 后端可记录降级信息，用于审计或排查。

Java 保存到业务表的 `source_segments_json` 是来源片段快照，应将 Python 的 `chunk_index` 映射为 Java 业务字段 `segmentIndex`。快照最小字段为 `text`、`materialId`、`segmentIndex`、`score`，可选保存 `materialTitle`、`originalFilename`，用于材料删除或重命名后的历史溯源展示。

## 错误处理原则

- Python 失败时，Java 应将材料处理状态或写作任务状态标记为失败，并记录错误原因。
- 材料首次处理或重新处理失败时，Java 应将材料主表置为 `status=failed`，`segment_count=0`，并记录失败原因；不要保留上一次成功的片段数量作为当前可用状态。
- Java 对前端返回友好错误信息，不直接泄露 Python 内部堆栈。
- Java 应保留 Python HTTP 状态、错误码、错误摘要、耗时和关联业务 ID。

推荐错误映射：

| Python HTTP 状态 | Java 处理建议 |
|---|---|
| 400 | 参数错误，Java 可返回参数校验失败 |
| 422 | 业务校验失败，Java 可返回业务异常 |
| 502 | 上游模型或向量库失败，Java 可记录 AI 服务异常 |
| 500 | Python 内部错误，Java 可记录系统异常并返回友好提示 |

## 调用审计建议

Java 后端应记录 AI 调用审计，建议包含：

- 调用用户 ID。
- 材料 ID 或材料 ID 列表。
- 写作任务 ID 或问答记录 ID。
- Python 接口能力类型。
- 请求参数摘要，避免记录敏感原文或超长内容。
- 响应状态。
- Python HTTP 状态。
- Python 错误码。
- 来源片段数量。
- 调用链追踪 ID。
- 调用耗时。
- 错误码和错误原因。
- 材料 ID 列表快照，建议使用 JSON 保存，避免反查时业务关系已变化。

第一版不建议保存完整请求体和完整响应体，避免超大内容、材料正文或敏感信息进入日志表；优先保存 `request_summary`、`response_summary`、`material_ids_json`、`http_status`、`error_code`、`source_count` 和 `trace_id`。

第一版 AI 调用日志的 `business_type` 使用固定值：

| business_type | 含义 |
|---|---|
| `material_process` | 材料解析、切分、向量写入或覆盖处理 |
| `material_vector_delete` | 删除材料向量 |
| `writing` | 写作生成 |
| `qa` | 材料问答 |

## 后续接口演进建议

- 先稳定 Java 与 Python 的请求、响应和错误模型，再扩展能力接口。
- 业务状态仍由 Java 后端维护，不随 Python 接口扩展而下沉。
- 新增 Python 能力时，优先在 Java 的 `ai` 模块统一封装调用。
- 对已有接口做不兼容调整时，应同步更新本文档、README 和相关接口文档。
