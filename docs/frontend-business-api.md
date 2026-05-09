# 前端业务接口文档

本文档涵盖材料管理、写作任务、材料问答三个业务模块的 API 接口。基础约定、认证、文件、系统管理等接口参见 [frontend-integration-guide.md](frontend-integration-guide.md)。

---

## 认证与基础

所有接口需在请求头携带令牌：

```
Authorization: Bearer <token>
```

统一响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

分页查询使用 Query Parameters，响应结构：

```json
{
  "records": [],
  "total": 100,
  "page": 1,
  "pageSize": 10
}
```

---

## 一、材料管理

> 基础路径：`/api/material`

### 1.1 创建材料

```
POST /api/material
```

**请求体：**

```json
{
  "userId": 1,
  "title": "2024年宣传材料",
  "originalFilename": "宣传材料.docx",
  "fileId": 1,
  "fileType": "docx",
  "fileSize": 102400,
  "fileMd5": "d41d8cd98f00b204e9800998ecf8427e",
  "storagePath": "uploads/2024/01/15/a1b2c3d4.docx",
  "status": "processing",
  "remark": "第一批材料"
}
```

| 字段 | 类型 | 必填 | 校验 | 说明 |
|------|------|------|------|------|
| `userId` | `Long` | 是 | — | 所属用户 ID |
| `title` | `String` | 是 | 最长 200 | 材料标题 |
| `originalFilename` | `String` | 是 | 最长 255 | 原始文件名 |
| `fileId` | `Long` | 是 | — | 文件记录 ID（来自文件上传接口） |
| `fileType` | `String` | 否 | 最长 50 | 文件类型 |
| `fileSize` | `Long` | 是 | — | 文件大小（字节） |
| `fileMd5` | `String` | 否 | 最长 32 | 文件 MD5 |
| `storagePath` | `String` | 是 | 最长 500 | 文件存储路径（来自文件上传接口） |
| `status` | `String` | 否 | 最长 32 | 初始状态，默认 `"processing"` |
| `remark` | `String` | 否 | 最长 500 | 备注 |

**响应：** `MaterialVO`

### 1.2 材料分页查询

```
GET /api/material/page?page=1&pageSize=10
```

| 参数 | 类型 | 说明 |
|------|------|------|
| `page` | `long` | 页码，默认 1 |
| `pageSize` | `long` | 每页条数，默认 10 |
| `userId` | `Long` | 按用户筛选 |
| `title` | `String` | 按标题模糊匹配 |
| `fileId` | `Long` | 按文件 ID 筛选 |
| `fileType` | `String` | 按文件类型筛选 |
| `status` | `String` | 按状态筛选 |

**响应：** `PageResult<MaterialVO>`

### 1.3 材料详情

```
GET /api/material/{id}
```

**响应：** `MaterialVO`

### 1.4 更新材料

```
PUT /api/material/{id}
```

```json
{
  "title": "新标题",
  "remark": "更新备注"
}
```

| 字段 | 类型 | 校验 | 说明 |
|------|------|------|------|
| `title` | `String` | 最长 200 | 标题 |
| `status` | `String` | 最长 32 | 状态 |
| `segmentCount` | `Integer` | — | 片段数量 |
| `errorMessage` | `String` | — | 错误信息 |
| `remark` | `String` | 最长 500 | 备注 |

**响应：** `Void`

### 1.5 触发材料处理

```
POST /api/material/{id}/process
```

请求体可选：

```json
{
  "processType": "reprocess"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `processType` | `String` | `"initial"`（首次）或 `"reprocess"`（重新处理），不传时后端自动判断 |

**响应：** `MaterialVO`

> 此接口调用 Python AI 服务进行解析、切片、向量化，耗时较长。

### 1.6 删除材料向量

```
POST /api/material/{id}/vector-delete
```

无需请求体。删除该材料在向量数据库中的全部片段。

**响应：** `Void`

### 1.7 删除材料

```
DELETE /api/material/{id}
```

### 1.8 批量删除材料

```
POST /api/material/batch-delete
```

```json
{ "ids": [1, 2, 3] }
```

### MaterialVO

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 材料 ID |
| `userId` | `Long` | 所属用户 ID |
| `title` | `String` | 标题 |
| `originalFilename` | `String` | 原始文件名 |
| `fileId` | `Long` | 文件 ID |
| `fileType` | `String` | 文件类型 |
| `fileSize` | `Long` | 文件大小 |
| `fileMd5` | `String` | 文件 MD5 |
| `storagePath` | `String` | 存储路径 |
| `status` | `String` | `processing` / `available` / `failed` |
| `segmentCount` | `Integer` | 片段数量 |
| `lastProcessTime` | `LocalDateTime` | 最近处理时间 |
| `errorMessage` | `String` | 失败原因 |
| `remark` | `String` | 备注 |
| `createTime` | `LocalDateTime` | 创建时间 |
| `updateTime` | `LocalDateTime` | 更新时间 |

---

## 二、材料处理记录

> 基础路径：`/api/material/process-record`

### 2.1 处理记录分页查询

```
GET /api/material/process-record/page?page=1&pageSize=10
```

| 参数 | 类型 | 说明 |
|------|------|------|
| `page` | `long` | 页码，默认 1 |
| `pageSize` | `long` | 每页条数，默认 10 |
| `materialId` | `Long` | 按材料 ID 筛选 |
| `userId` | `Long` | 按用户 ID 筛选 |
| `fileId` | `Long` | 按文件 ID 筛选 |
| `processType` | `String` | `initial` / `reprocess` |
| `status` | `String` | `success` / `failed` |

**响应：** `PageResult<MaterialProcessRecordVO>`

### 2.2 处理记录详情

```
GET /api/material/process-record/{id}
```

### MaterialProcessRecordVO

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 记录 ID |
| `materialId` | `Long` | 材料 ID |
| `userId` | `Long` | 用户 ID |
| `fileId` | `Long` | 文件 ID |
| `fileMd5` | `String` | 文件 MD5 |
| `originalFilename` | `String` | 原始文件名 |
| `processType` | `String` | `initial` / `reprocess` |
| `status` | `String` | `success` / `failed` |
| `deletedCount` | `Integer` | 删除的旧向量数 |
| `segmentCount` | `Integer` | 生成的片段数 |
| `errorMessage` | `String` | 失败原因 |
| `startedAt` | `LocalDateTime` | 开始时间 |
| `finishedAt` | `LocalDateTime` | 结束时间 |
| `durationMs` | `Long` | 耗时（毫秒） |
| `createTime` | `LocalDateTime` | 创建时间 |
| `updateTime` | `LocalDateTime` | 更新时间 |

---

## 三、写作任务

> 基础路径：`/api/writing/task`

### 写作类型

| writingType | 说明 | 特殊要求 |
|-------------|------|----------|
| `outline` | 大纲 | — |
| `draft` | 初稿 | — |
| `polished` | 润色 | `inputContent` 必填 |
| `title` | 标题 | — |

### 任务状态流转

```
pending → running → success
                  → failed
```

### 3.1 创建写作任务

```
POST /api/writing/task
```

**请求体：**

```json
{
  "title": "2024年工作总结",
  "writingType": "draft",
  "topic": "2024年度工作总结报告",
  "requirement": "语言正式，结构清晰",
  "inputContent": null,
  "materialIds": [1, 2, 3],
  "topK": 5
}
```

| 字段 | 类型 | 必填 | 校验 | 说明 |
|------|------|------|------|------|
| `title` | `String` | 是 | 最长 200 | 任务标题 |
| `writingType` | `String` | 是 | — | 写作类型 |
| `topic` | `String` | 是 | 最长 500 | 写作主题 |
| `requirement` | `String` | 否 | — | 写作要求 |
| `inputContent` | `String` | 否 | 润色时必填 | 待润色原文 |
| `materialIds` | `List<Long>` | 否 | — | 参考材料 ID 列表 |
| `topK` | `Integer` | 否 | 1-20 | 检索片段数量 |

**响应：** `WritingTaskVO`

> 同步调用 AI，返回时任务已完成（成功或失败）。

### 3.2 写作任务分页查询

```
GET /api/writing/task/page?page=1&pageSize=10
```

| 参数 | 类型 | 说明 |
|------|------|------|
| `page` | `long` | 页码 |
| `pageSize` | `long` | 每页条数 |
| `writingType` | `String` | 按写作类型筛选 |
| `status` | `String` | 按状态筛选 |
| `title` | `String` | 按标题模糊匹配 |

**响应：** `PageResult<WritingTaskVO>`

### 3.3 写作任务详情

```
GET /api/writing/task/{id}
```

**响应：** `WritingTaskVO`

### WritingTaskVO

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 任务 ID |
| `userId` | `Long` | 用户 ID |
| `title` | `String` | 标题 |
| `writingType` | `String` | 写作类型 |
| `topic` | `String` | 主题 |
| `requirement` | `String` | 要求 |
| `inputContent` | `String` | 润色原文 |
| `status` | `String` | 状态 |
| `errorMessage` | `String` | 失败原因 |
| `materialIds` | `List<Long>` | 关联材料 ID |
| `result` | `WritingResultVO` | 生成结果（成功时有值） |
| `startedAt` | `LocalDateTime` | 开始时间 |
| `finishedAt` | `LocalDateTime` | 结束时间 |
| `createTime` | `LocalDateTime` | 创建时间 |
| `updateTime` | `LocalDateTime` | 更新时间 |

### WritingResultVO

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 结果 ID |
| `taskId` | `Long` | 任务 ID |
| `userId` | `Long` | 用户 ID |
| `versionNo` | `Integer` | 版本号 |
| `content` | `String` | 生成内容 |
| `sourceSegmentsJson` | `String` | 来源片段 JSON（原始） |
| `sourceSegments` | `List<SourceSegmentVO>` | 来源片段列表 |
| `modelName` | `String` | AI 模型名称 |
| `aiCallLogId` | `Long` | 调用日志 ID |
| `createTime` | `LocalDateTime` | 创建时间 |
| `updateTime` | `LocalDateTime` | 更新时间 |

### SourceSegmentVO（来源片段）

| 字段 | 类型 | 说明 |
|------|------|------|
| `text` | `String` | 片段文本 |
| `materialId` | `Long` | 来源材料 ID |
| `materialTitle` | `String` | 来源材料标题 |
| `originalFilename` | `String` | 来源文件名 |
| `segmentIndex` | `Integer` | 片段序号 |
| `score` | `BigDecimal` | 相似度得分 |

---

## 四、材料问答

> 基础路径：`/api/qa/session`

### 4.1 创建问答会话

```
POST /api/qa/session
```

```json
{
  "title": "关于宣传材料的问答",
  "materialIds": [1, 2]
}
```

| 字段 | 类型 | 必填 | 校验 | 说明 |
|------|------|------|------|------|
| `title` | `String` | 否 | 最长 200 | 会话标题，默认 `"新问答会话"` |
| `materialIds` | `List<Long>` | 否 | — | 关联材料 ID 列表 |

**响应：** `QaSessionVO`

### 4.2 问答会话分页查询

```
GET /api/qa/session/page?page=1&pageSize=10
```

| 参数 | 类型 | 说明 |
|------|------|------|
| `page` | `long` | 页码 |
| `pageSize` | `long` | 每页条数 |
| `status` | `String` | 按状态筛选 |

**响应：** `PageResult<QaSessionVO>`

### 4.3 问答会话详情

```
GET /api/qa/session/{id}
```

**响应：** `QaSessionVO`

### 4.4 更新会话关联材料

```
PUT /api/qa/session/{id}/material
```

```json
{ "materialIds": [1, 3, 5] }
```

> 替换全部关联材料（非追加）。

**响应：** `List<QaMaterialVO>`

### 4.5 查询会话消息

```
GET /api/qa/session/{id}/message?includeSystem=false
```

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `includeSystem` | `boolean` | `false` | 是否包含系统消息 |

**响应：** `List<QaMessageVO>`，按时间正序。

### 4.6 发送提问

```
POST /api/qa/session/{id}/ask
```

```json
{
  "question": "这份材料的主要内容是什么？",
  "topK": 5
}
```

| 字段 | 类型 | 必填 | 校验 | 说明 |
|------|------|------|------|------|
| `question` | `String` | 是 | — | 用户问题 |
| `topK` | `Integer` | 否 | 1-20 | 检索片段数量 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userMessage": {
      "id": 10,
      "sessionId": 1,
      "role": "user",
      "content": "这份材料的主要内容是什么？",
      "sourceSegments": null,
      "modelName": null,
      "aiCallLogId": null,
      "createTime": "2024-01-15T11:00:00"
    },
    "assistantMessage": {
      "id": 11,
      "sessionId": 1,
      "role": "assistant",
      "content": "根据材料分析，主要内容包括...",
      "sourceSegments": [
        {
          "text": "原文片段内容...",
          "materialId": 1,
          "segmentIndex": 5,
          "score": 0.85,
          "materialTitle": "2024年宣传材料",
          "originalFilename": "宣传材料.docx"
        }
      ],
      "modelName": "gpt-4",
      "aiCallLogId": 42,
      "createTime": "2024-01-15T11:00:01"
    }
  }
}
```

> 同步返回用户消息和助手消息，非流式接口。

### QaSessionVO

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会话 ID |
| `title` | `String` | 标题 |
| `status` | `String` | 状态 |
| `lastMessageTime` | `LocalDateTime` | 最后消息时间 |
| `messageCount` | `Integer` | 消息总数 |
| `materials` | `List<QaMaterialVO>` | 关联材料列表 |
| `createTime` | `LocalDateTime` | 创建时间 |
| `updateTime` | `LocalDateTime` | 更新时间 |

### QaMaterialVO

| 字段 | 类型 | 说明 |
|------|------|------|
| `materialId` | `Long` | 材料 ID |
| `title` | `String` | 材料标题 |
| `originalFilename` | `String` | 原始文件名 |

### QaMessageVO

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 消息 ID |
| `sessionId` | `Long` | 会话 ID |
| `role` | `String` | `user` / `assistant` / `system` |
| `content` | `String` | 消息内容 |
| `sourceSegments` | `List<QaSourceSegmentVO>` | 来源片段 |
| `modelName` | `String` | AI 模型名称 |
| `aiCallLogId` | `Long` | 调用日志 ID |
| `createTime` | `LocalDateTime` | 创建时间 |

### QaSourceSegmentVO

| 字段 | 类型 | 说明 |
|------|------|------|
| `text` | `String` | 片段文本 |
| `materialId` | `Long` | 材料 ID |
| `segmentIndex` | `Integer` | 片段序号 |
| `score` | `BigDecimal` | 相似度得分 |
| `materialTitle` | `String` | 材料标题 |
| `originalFilename` | `String` | 来源文件名 |

---

## 附录：状态枚举

### 材料状态

| 值 | 说明 |
|----|------|
| `processing` | 处理中 |
| `available` | 可用 |
| `failed` | 处理失败 |

### 写作任务状态

| 值 | 说明 |
|----|------|
| `pending` | 待处理 |
| `running` | 运行中 |
| `success` | 成功 |
| `failed` | 失败 |

### 问答消息角色

| 值 | 说明 |
|----|------|
| `user` | 用户 |
| `assistant` | 助手 |
| `system` | 系统 |
