# 前端对接指南

本文档供前端开发人员对接本地化智能材料写作平台 Java 后端 API 使用。涵盖认证、材料管理、写作任务、材料问答、文件上传等全部业务接口。

## 目录

- [基础约定](#基础约定)
- [认证模块](#认证模块)
- [文件模块](#文件模块)
- [材料模块](#材料模块)
- [材料处理记录模块](#材料处理记录模块)
- [写作任务模块](#写作任务模块)
- [问答模块](#问答模块)
- [附录：状态枚举](#附录状态枚举)

---

## 基础约定

### 服务地址

- 开发环境：`http://localhost:8080`
- API 前缀：所有接口统一使用 `/api` 前缀
- 接口文档（Knife4j）：`http://localhost:8080/doc.html`

### 认证方式

采用 Sa-Token 框架，通过 HTTP Header 传递令牌：

```
Authorization: Bearer <token>
```

- 令牌在登录接口返回
- 有效期 7200 秒（2 小时），支持自动续期
- 除登录接口外，所有 `/api/**` 接口均需携带令牌

### 统一响应结构

所有接口（文件下载/预览除外）返回统一 JSON 格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | `int` | 状态码，200=成功 |
| `message` | `String` | 提示信息 |
| `data` | `T` | 业务数据，无数据时为 `null` |

### 错误码

| code | 含义 | 触发场景 |
|------|------|----------|
| `200` | 成功 | 正常响应 |
| `400` | 参数错误 | 校验失败、请求格式错误 |
| `401` | 未登录 | 未携带 token 或 token 已失效 |
| `403` | 无权限 | 已登录但无对应权限 |
| `404` | 数据不存在 | 资源 ID 不存在 |
| `500` | 系统错误 | 服务端异常 |

**400 参数校验错误响应体示例：**

```json
{
  "code": 400,
  "message": "参数错误",
  "data": {
    "username": "用户名不能为空",
    "password": "密码不能为空"
  }
}
```

### 分页结构

分页查询使用 Query Parameters（非 Request Body），默认 `page=1`，`pageSize=10`，最大 `pageSize=500`。

分页响应结构：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "page": 1,
    "pageSize": 10
  }
}
```

### 日期时间格式

所有 `LocalDateTime` 字段使用 ISO-8601 格式：`"2024-01-15T10:30:00"`。

---

## 认证模块

> 基础路径：`/api/auth`

### 登录

```
POST /api/auth/login
```

**请求体：**

```json
{
  "username": "admin",
  "password": "admin123"
}
```

| 字段 | 类型 | 必填 | 校验 |
|------|------|------|------|
| `username` | `String` | 是 | 不能为空 |
| `password` | `String` | 是 | 不能为空 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "tokenName": "Authorization",
    "tokenValue": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 7200
  }
}
```

前端需将 `tokenValue` 存储后，在后续请求的 `Authorization` Header 中携带。

### 退出登录

```
POST /api/auth/logout
```

无需请求体。响应 `data` 为 `null`。

### 获取当前用户信息

```
GET /api/auth/me
```

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "avatar": "https://...",
    "phone": "13800000000",
    "email": "admin@example.com",
    "dept": {
      "id": 1,
      "deptName": "总公司",
      "deptCode": "HQ"
    },
    "roles": [
      {
        "id": 1,
        "roleName": "超级管理员",
        "roleCode": "admin"
      }
    ],
    "lastLoginTime": "2024-01-15T10:30:00",
    "lastLoginIp": "127.0.0.1"
  }
}
```

### 获取当前用户可见菜单

```
GET /api/auth/menus
```

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "parentId": 0,
      "menuName": "系统管理",
      "menuType": "M",
      "path": "/system",
      "component": "",
      "icon": "setting",
      "permissionCode": "",
      "sortOrder": 1,
      "visible": 1,
      "children": [
        {
          "id": 2,
          "parentId": 1,
          "menuName": "用户管理",
          "menuType": "C",
          "path": "user",
          "component": "system/user/index",
          "icon": "user",
          "permissionCode": "system:user:list",
          "sortOrder": 1,
          "visible": 1,
          "children": []
        }
      ]
    }
  ]
}
```

---

## 文件模块

> 基础路径：`/api/system/file`

文件上传是材料处理流程的第一步。前端需先上传文件获取 `FileVO`，再用其中的字段创建材料记录。

### 上传单个文件

```
POST /api/system/file/upload
Content-Type: multipart/form-data
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `file` | `MultipartFile` | 是 | 文件内容 |
| `businessModule` | `String` | 否 | 业务模块标识 |
| `remark` | `String` | 否 | 备注 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "originalName": "宣传材料.docx",
    "storageName": "a1b2c3d4.docx",
    "extension": ".docx",
    "mimeType": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "fileSize": 102400,
    "fileMd5": "d41d8cd98f00b204e9800998ecf8427e",
    "storagePath": "uploads/2024/01/15/a1b2c3d4.docx",
    "accessUrl": "/api/system/file/1/view",
    "businessModule": "material",
    "status": 1,
    "remark": null,
    "createTime": "2024-01-15T10:30:00",
    "updateTime": "2024-01-15T10:30:00"
  }
}
```

上传成功后，需记录 `id`、`originalName`、`fileMd5`、`storagePath`、`fileSize` 等字段，用于后续创建材料。

### 批量上传文件

```
POST /api/system/file/upload-batch
Content-Type: multipart/form-data
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `files` | `MultipartFile[]` | 是 | 文件数组 |
| `businessModule` | `String` | 否 | 业务模块标识 |
| `remark` | `String` | 否 | 备注 |

**响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "succeeded": [ { ... FileVO } ],
    "failed": [
      {
        "fileName": "bad-file.txt",
        "message": "不支持的文件类型"
      }
    ]
  }
}
```

### 文件分页查询

```
GET /api/system/file/page?page=1&pageSize=10
```

支持 Query Parameters：`originalName`、`businessModule`、`mimeType`、`status`。

### 文件详情

```
GET /api/system/file/{id}
```

### 文件下载

```
GET /api/system/file/{id}/download
```

> **注意**：此接口返回原始二进制流，不走统一 `ApiResponse` 包装。前端需以 Blob 方式处理响应。

### 文件预览

```
GET /api/system/file/{id}/view
```

> **注意**：同下载接口，返回原始二进制流，`Content-Type` 为文件实际 MIME 类型，可直接用于 `<img>` 或 `<iframe>` 的 `src`。

### 更新文件元数据

```
PUT /api/system/file/{id}
```

```json
{
  "businessModule": "material",
  "remark": "更新备注"
}
```

### 删除文件

```
DELETE /api/system/file/{id}
```

### 批量删除文件

```
POST /api/system/file/batch-delete
```

```json
{
  "ids": [1, 2, 3]
}
```

### 更新文件状态

```
PATCH /api/system/file/{id}/status
```

```json
{
  "status": 0
}
```

`status` 只允许 `0`（禁用）或 `1`（启用）。

---

## 材料模块

> 基础路径：`/api/material`

材料是用户上传的宣传文档素材。典型流程：上传文件 → 创建材料 → 触发处理（解析/切片/向量化）→ 处理完成后可用于写作或问答。

### 材料状态流转

```
processing（处理中）
    ├──→ available（可用）  处理成功
    └──→ failed（失败）    处理失败，errorMessage 记录原因
```

### 创建材料

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
| `userId` | `Long` | 是 | 不能为空 | 所属用户 ID |
| `title` | `String` | 是 | 不能为空，最长 200 | 材料标题 |
| `originalFilename` | `String` | 是 | 不能为空，最长 255 | 原始文件名 |
| `fileId` | `Long` | 是 | 不能为空 | 关联的文件记录 ID（来自文件上传接口） |
| `fileType` | `String` | 否 | 最长 50 | 文件类型，如 `"docx"`、`"pdf"` |
| `fileSize` | `Long` | 是 | 不能为空 | 文件大小（字节） |
| `fileMd5` | `String` | 否 | 最长 32 | 文件 MD5 |
| `storagePath` | `String` | 是 | 不能为空，最长 500 | 文件存储路径（来自文件上传接口） |
| `status` | `String` | 否 | 最长 32 | 初始状态，默认 `"processing"` |
| `remark` | `String` | 否 | 最长 500 | 备注 |

**响应：** 返回 `MaterialVO`（见下方字段说明）。

### 材料分页查询

```
GET /api/material/page?page=1&pageSize=10
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `page` | `long` | 否 | 页码，默认 1 |
| `pageSize` | `long` | 否 | 每页条数，默认 10 |
| `userId` | `Long` | 否 | 按用户筛选 |
| `title` | `String` | 否 | 按标题模糊匹配 |
| `fileId` | `Long` | 否 | 按文件 ID 筛选 |
| `fileType` | `String` | 否 | 按文件类型筛选 |
| `status` | `String` | 否 | 按状态筛选 |

**响应：** `PageResult<MaterialVO>`。

### 材料详情

```
GET /api/material/{id}
```

### 更新材料

```
PUT /api/material/{id}
```

```json
{
  "title": "新标题",
  "status": "available",
  "remark": "更新备注"
}
```

### 触发材料处理

```
POST /api/material/{id}/process
```

请求体可选：

```json
{
  "processType": "initial"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `processType` | `String` | 否 | `"initial"`（首次处理）或 `"reprocess"`（重新处理）。不传时由后端自动判断 |

**响应：** 返回处理后的 `MaterialVO`。

> 此接口会调用 Python AI 服务进行材料解析、片段切分和向量化，耗时较长。前端应做好 loading 状态处理。

### 删除材料向量

```
POST /api/material/{id}/vector-delete
```

无需请求体。删除该材料在向量数据库中的所有片段向量。

### 删除材料

```
DELETE /api/material/{id}
```

### 批量删除材料

```
POST /api/material/batch-delete
```

```json
{
  "ids": [1, 2, 3]
}
```

### MaterialVO 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 材料 ID |
| `userId` | `Long` | 所属用户 ID |
| `title` | `String` | 材料标题 |
| `originalFilename` | `String` | 原始文件名 |
| `fileId` | `Long` | 关联文件 ID |
| `fileType` | `String` | 文件类型 |
| `fileSize` | `Long` | 文件大小（字节） |
| `fileMd5` | `String` | 文件 MD5 |
| `storagePath` | `String` | 存储路径 |
| `status` | `String` | 状态：`processing` / `available` / `failed` |
| `segmentCount` | `Integer` | 片段数量（处理成功后有值） |
| `lastProcessTime` | `LocalDateTime` | 最近一次处理时间 |
| `errorMessage` | `String` | 处理失败时的错误信息 |
| `remark` | `String` | 备注 |
| `createTime` | `LocalDateTime` | 创建时间 |
| `updateTime` | `LocalDateTime` | 更新时间 |

---

## 材料处理记录模块

> 基础路径：`/api/material/process-record`

每次材料处理（初始处理或重新处理）都会生成一条处理记录，用于审计和排障。

### 处理记录分页查询

```
GET /api/material/process-record/page?page=1&pageSize=10
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `page` | `long` | 否 | 页码，默认 1 |
| `pageSize` | `long` | 否 | 每页条数，默认 10 |
| `materialId` | `Long` | 否 | 按材料 ID 筛选 |
| `userId` | `Long` | 否 | 按用户 ID 筛选 |
| `fileId` | `Long` | 否 | 按文件 ID 筛选 |
| `processType` | `String` | 否 | 按处理类型筛选：`initial` / `reprocess` |
| `status` | `String` | 否 | 按状态筛选：`success` / `failed` |

### 处理记录详情

```
GET /api/material/process-record/{id}
```

### MaterialProcessRecordVO 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 记录 ID |
| `materialId` | `Long` | 关联材料 ID |
| `userId` | `Long` | 用户 ID |
| `fileId` | `Long` | 文件 ID |
| `fileMd5` | `String` | 文件 MD5（快照） |
| `originalFilename` | `String` | 原始文件名（快照） |
| `processType` | `String` | 处理类型：`initial` / `reprocess` |
| `status` | `String` | 处理状态：`success` / `failed` |
| `deletedCount` | `Integer` | 删除的旧向量数 |
| `segmentCount` | `Integer` | 生成的片段数 |
| `errorMessage` | `String` | 失败原因 |
| `startedAt` | `LocalDateTime` | 开始时间 |
| `finishedAt` | `LocalDateTime` | 结束时间 |
| `durationMs` | `Long` | 耗时（毫秒） |
| `createTime` | `LocalDateTime` | 创建时间 |
| `updateTime` | `LocalDateTime` | 更新时间 |

---

## 写作任务模块

> 基础路径：`/api/writing/task`

用户基于已处理的材料发起内容生成请求。典型流程：选择材料 → 创建写作任务 → 后端调用 AI 生成 → 返回生成内容和来源片段。

### 写作类型

| writingType | 说明 | 特殊要求 |
|-------------|------|----------|
| `outline` | 大纲 | 无 |
| `draft` | 初稿 | 无 |
| `polished` | 润色 | `inputContent` 必填 |
| `title` | 标题 | 无 |

### 写作任务状态流转

```
pending（待处理）
  └──→ running（运行中）
        ├──→ success（成功）  result 中包含生成内容
        └──→ failed（失败）   errorMessage 记录原因
```

### 创建写作任务

```
POST /api/writing/task
```

**请求体：**

```json
{
  "title": "2024年工作总结",
  "writingType": "draft",
  "topic": "2024年度工作总结报告",
  "requirement": "语言正式，结构清晰，分点陈述",
  "inputContent": null,
  "materialIds": [1, 2, 3],
  "topK": 5
}
```

| 字段 | 类型 | 必填 | 校验 | 说明 |
|------|------|------|------|------|
| `title` | `String` | 是 | 不能为空，最长 200 | 任务标题 |
| `writingType` | `String` | 是 | 不能为空 | 写作类型：`outline` / `draft` / `polished` / `title` |
| `topic` | `String` | 是 | 不能为空，最长 500 | 写作主题 |
| `requirement` | `String` | 否 | — | 写作要求 |
| `inputContent` | `String` | 否 | 润色时必填 | 待润色的原文内容 |
| `materialIds` | `List<Long>` | 否 | — | 参考材料 ID 列表，不传则不限定材料范围 |
| `topK` | `Integer` | 否 | 1-20 | 向量检索返回的片段数量 |

**响应：** 返回 `WritingTaskVO`。

> 此接口会同步调用 Python AI 服务生成内容，返回时任务已完成（成功或失败）。耗时取决于内容长度和 AI 模型响应速度。

### 写作任务分页查询

```
GET /api/writing/task/page?page=1&pageSize=10
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `page` | `long` | 否 | 页码，默认 1 |
| `pageSize` | `long` | 否 | 每页条数，默认 10 |
| `writingType` | `String` | 否 | 按写作类型筛选 |
| `status` | `String` | 否 | 按状态筛选 |
| `title` | `String` | 否 | 按标题模糊匹配 |

### 写作任务详情

```
GET /api/writing/task/{id}
```

### WritingTaskVO 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 任务 ID |
| `userId` | `Long` | 用户 ID |
| `title` | `String` | 任务标题 |
| `writingType` | `String` | 写作类型 |
| `topic` | `String` | 写作主题 |
| `requirement` | `String` | 写作要求 |
| `inputContent` | `String` | 润色原文 |
| `status` | `String` | 任务状态 |
| `errorMessage` | `String` | 失败原因 |
| `materialIds` | `List<Long>` | 关联的材料 ID 列表 |
| `result` | `WritingResultVO` | 生成结果（成功时有值） |
| `startedAt` | `LocalDateTime` | 开始时间 |
| `finishedAt` | `LocalDateTime` | 结束时间 |
| `createTime` | `LocalDateTime` | 创建时间 |
| `updateTime` | `LocalDateTime` | 更新时间 |

### WritingResultVO 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 结果 ID |
| `taskId` | `Long` | 关联任务 ID |
| `userId` | `Long` | 用户 ID |
| `versionNo` | `Integer` | 版本号 |
| `content` | `String` | 生成的文本内容 |
| `sourceSegmentsJson` | `String` | 来源片段 JSON 字符串（原始存储） |
| `sourceSegments` | `List<SourceSegmentVO>` | 来源片段列表（已解析） |
| `modelName` | `String` | AI 模型名称 |
| `aiCallLogId` | `Long` | AI 调用日志 ID |
| `createTime` | `LocalDateTime` | 创建时间 |
| `updateTime` | `LocalDateTime` | 更新时间 |

### SourceSegmentVO 字段说明（来源片段）

| 字段 | 类型 | 说明 |
|------|------|------|
| `text` | `String` | 片段文本内容 |
| `materialId` | `Long` | 来源材料 ID |
| `materialTitle` | `String` | 来源材料标题 |
| `originalFilename` | `String` | 来源文件名 |
| `segmentIndex` | `Integer` | 片段在材料中的序号 |
| `score` | `BigDecimal` | 向量检索相似度得分 |

---

## 问答模块

> 基础路径：`/api/qa/session`

用户基于材料进行问答交互。典型流程：创建会话（选择材料）→ 提问 → 后端调用 AI 回答 → 返回回答和来源片段。可多次追问。

### 问答会话状态

| status | 说明 |
|--------|------|
| `active` | 活跃状态 |

### 创建问答会话

```
POST /api/qa/session
```

**请求体：**

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

**响应：** 返回 `QaSessionVO`。

### 问答会话分页查询

```
GET /api/qa/session/page?page=1&pageSize=10
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `page` | `long` | 否 | 页码，默认 1 |
| `pageSize` | `long` | 否 | 每页条数，默认 10 |
| `status` | `String` | 否 | 按状态筛选 |

### 问答会话详情

```
GET /api/qa/session/{id}
```

### 更新会话关联材料

```
PUT /api/qa/session/{id}/material
```

```json
{
  "materialIds": [1, 3, 5]
}
```

**响应：** 返回更新后的 `List<QaMaterialVO>`。

> 此操作会替换会话的全部关联材料（非追加）。

### 查询会话消息列表

```
GET /api/qa/session/{id}/message?includeSystem=false
```

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `includeSystem` | `boolean` | 否 | `false` | 是否包含系统消息 |

**响应：** `List<QaMessageVO>`，按时间正序排列。

### 发送提问

```
POST /api/qa/session/{id}/ask
```

**请求体：**

```json
{
  "question": "这份材料的主要内容是什么？",
  "topK": 5
}
```

| 字段 | 类型 | 必填 | 校验 | 说明 |
|------|------|------|------|------|
| `question` | `String` | 是 | 不能为空 | 用户问题 |
| `topK` | `Integer` | 否 | 1-20 | 向量检索返回的片段数量 |

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

> 此接口同步返回用户消息和助手消息，不是流式接口。

### QaSessionVO 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 会话 ID |
| `title` | `String` | 会话标题 |
| `status` | `String` | 会话状态 |
| `lastMessageTime` | `LocalDateTime` | 最后消息时间 |
| `messageCount` | `Integer` | 消息总数 |
| `materials` | `List<QaMaterialVO>` | 关联材料列表 |
| `createTime` | `LocalDateTime` | 创建时间 |
| `updateTime` | `LocalDateTime` | 更新时间 |

### QaMaterialVO 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `materialId` | `Long` | 材料 ID |
| `title` | `String` | 材料标题 |
| `originalFilename` | `String` | 原始文件名 |

### QaMessageVO 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `Long` | 消息 ID |
| `sessionId` | `Long` | 所属会话 ID |
| `role` | `String` | 角色：`user` / `assistant` / `system` |
| `content` | `String` | 消息内容 |
| `sourceSegments` | `List<QaSourceSegmentVO>` | 来源片段（仅 assistant 消息有值） |
| `modelName` | `String` | AI 模型名称 |
| `aiCallLogId` | `Long` | AI 调用日志 ID |
| `createTime` | `LocalDateTime` | 创建时间 |

### QaSourceSegmentVO 字段说明

与写作模块的 `SourceSegmentVO` 结构相同：

| 字段 | 类型 | 说明 |
|------|------|------|
| `text` | `String` | 片段文本内容 |
| `materialId` | `Long` | 来源材料 ID |
| `segmentIndex` | `Integer` | 片段序号 |
| `score` | `BigDecimal` | 相似度得分 |
| `materialTitle` | `String` | 来源材料标题 |
| `originalFilename` | `String` | 来源文件名 |

---

## 附录：状态枚举

### 材料状态（`biz_material.status`）

| 值 | 说明 |
|----|------|
| `processing` | 处理中 |
| `available` | 可用 |
| `failed` | 处理失败 |

### 材料处理类型（`biz_material_process_record.process_type`）

| 值 | 说明 |
|----|------|
| `initial` | 首次处理 |
| `reprocess` | 重新处理 |

### 写作任务状态（`biz_writing_task.status`）

| 值 | 说明 |
|----|------|
| `pending` | 待处理 |
| `running` | 运行中 |
| `success` | 成功 |
| `failed` | 失败 |

### 写作类型（`biz_writing_task.writing_type`）

| 值 | 说明 |
|----|------|
| `outline` | 大纲 |
| `draft` | 初稿 |
| `polished` | 润色 |
| `title` | 标题 |

### 问答会话状态（`biz_qa_session.status`）

| 值 | 说明 |
|----|------|
| `active` | 活跃 |

### 问答消息角色（`biz_qa_message.role`）

| 值 | 说明 |
|----|------|
| `user` | 用户 |
| `assistant` | 助手 |
| `system` | 系统 |

### AI 调用日志业务类型（`biz_ai_call_log.business_type`）

| 值 | 说明 |
|----|------|
| `material_process` | 材料处理 |
| `material_vector_delete` | 材料向量删除 |
| `writing` | 写作生成 |
| `qa` | 问答 |

### 文件状态（`sys_file.status`）

| 值 | 说明 |
|----|------|
| `0` | 禁用 |
| `1` | 启用 |
