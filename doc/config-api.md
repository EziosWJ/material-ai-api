# 系统配置管理 - 前端对接文档

本文档面向前端开发人员，描述系统配置管理模块的接口、数据结构和对接要点。

---

## 一、模块概述

系统配置模块用于管理系统级和自定义配置项。每个配置项由唯一的 `configKey` 标识，前端可通过按配置键查询接口获取配置值，用于功能开关、参数控制等场景。

### 字段说明

| 字段         | 类型     | 说明                                            |
| ------------ | -------- | ----------------------------------------------- |
| id           | Long     | 主键                                            |
| configName   | String   | 配置名称                                        |
| configKey    | String   | 配置键（唯一）                                  |
| configValue  | String   | 配置值                                          |
| configType   | String   | 配置类型：`SYSTEM`=系统配置，`CUSTOM`=自定义配置 |
| valueType    | String   | 值类型：`TEXT`=文本，`NUMBER`=数字，`BOOLEAN`=布尔 |
| status       | Integer  | 状态：`1`=启用，`0`=禁用                        |
| isBuiltin    | Integer  | 是否内置：`1`=内置，`0`=普通                    |
| remark       | String   | 备注                                            |
| createTime   | String   | 创建时间                                        |
| updateTime   | String   | 更新时间                                        |

### 枚举字典

以下枚举已作为字典数据初始化，前端可通过字典接口获取选项列表：

- **配置类型**（dictCode: `CONFIG_TYPE`）：`SYSTEM`（系统配置）、`CUSTOM`（自定义配置）
- **配置值类型**（dictCode: `CONFIG_VALUE_TYPE`）：`TEXT`（文本）、`NUMBER`（数字）、`BOOLEAN`（布尔）

---

## 二、接口列表

### 2.1 配置分页

```
GET /api/system/config/page?page=1&pageSize=10&configName=&configKey=&configType=&status=
```

**请求参数（Query）：**

| 参数        | 类型    | 说明                 |
| ----------- | ------- | -------------------- |
| page        | int     | 页码，默认 1         |
| pageSize    | int     | 每页条数，默认 10    |
| configName  | String  | 配置名称（模糊查询） |
| configKey   | String  | 配置键（模糊查询）   |
| configType  | String  | 配置类型（精确匹配） |
| status      | Integer | 状态（精确匹配）     |

**响应 data.records[]：**

```json
{
  "id": 1,
  "configName": "日志清空开关",
  "configKey": "system.log-clear-enabled",
  "configValue": "true",
  "configType": "SYSTEM",
  "valueType": "BOOLEAN",
  "status": 1,
  "isBuiltin": 1,
  "remark": "控制日志清空接口是否可用",
  "createTime": "2026-01-01 00:00:00",
  "updateTime": "2026-01-01 00:00:00"
}
```

### 2.2 配置详情

```
GET /api/system/config/{id}
```

**响应 data：** 字段同分页记录。

### 2.3 按配置键查询（前端常用）

```
GET /api/system/config/key/{configKey}
```

```
GET /api/system/config/key/{configKey}
```

**响应 data：**

```json
{
  "configKey": "system.log-clear-enabled",
  "configValue": "true",
  "valueType": "BOOLEAN",
  "configName": "日志清空开关"
}
```

**前端对接要点：**
- 只返回启用（`status=1`）且未删除的配置
- 配置不存在或已禁用返回 `404`
- `configKey` 作为路径参数，如包含 `/` 等特殊字符，前端需使用 `encodeURIComponent(configKey)` 编码
- 前端可按 `configKey` 缓存配置值，减少重复请求
- `valueType` 指示 `configValue` 的数据类型，前端需自行转换：
  - `TEXT`：直接使用字符串
  - `NUMBER`：转为数字类型
  - `BOOLEAN`：`"true"` / `"false"` 字符串，转为布尔值

### 2.4 新增配置

```
POST /api/system/config
```

**请求：**

```json
{
  "configName": "系统名称",
  "configKey": "system.name",
  "configValue": "后台管理系统",
  "configType": "SYSTEM",
  "valueType": "TEXT",
  "status": 1,
  "remark": "显示在登录页标题"
}
```

**字段校验：**

| 字段        | 必填 | 校验规则                        |
| ----------- | ---- | ------------------------------- |
| configName  | 是   | 长度不超过 100                  |
| configKey   | 是   | 长度不超过 100，全局唯一        |
| configValue | 否   | 长度不超过 500                  |
| configType  | 否   | 默认 `SYSTEM`                   |
| valueType   | 否   | 默认 `TEXT`                     |
| status      | 否   | 默认 `1`（启用）                |
| remark      | 否   | -                               |

**前端对接要点：**
- `configKey` 重复时返回 `400`，提示"配置键已存在"
- 包括已逻辑删除的同 key 配置也会触发此错误（数据库唯一索引层面约束）
- 新增的配置默认为普通配置（`isBuiltin=0`）
- 后端不对 `configValue` 与 `valueType` 做匹配校验，即 `valueType=NUMBER` 时也可以存入非数字字符串；前端应自行在表单提交前校验

### 2.5 修改配置

```
PUT /api/system/config/{id}
```

**请求：** 字段同新增接口。

**前端对接要点：**
- `isBuiltin=1` 的内置配置项禁止修改，返回 `400`，提示"内置配置项禁止修改"
- `configKey` 重复时返回 `400`，提示"配置键已存在"
- 修改时 `configKey` 必须提交（后端会做唯一性校验），前端可将输入框置灰但仍需在请求体中携带原值
- `valueType` 校验规则同新增，后端不校验 `configValue` 与 `valueType` 的匹配性

### 2.6 删除配置

```
DELETE /api/system/config/{id}
```

**前端对接要点：**
- `isBuiltin=1` 的内置配置项禁止删除，返回 `400`，提示"内置配置项禁止删除"

### 2.7 批量删除

```
POST /api/system/config/batch-delete
Body: {"ids": [1, 2, 3]}
```

**前端对接要点：**
- 内置配置项会被静默跳过（不报错），非内置配置项正常删除
- 前端无需为内置项做特殊过滤，但建议在 UI 上标记内置项

### 2.8 启用/禁用

```
PATCH /api/system/config/{id}/status
Body: {"status": 1}
```

**前端对接要点：**
- 当前后端未对内置配置项（`isBuiltin=1`）限制状态修改，内置配置也可启用/禁用
- 前端建议在 UI 上对内置配置项隐藏或禁用状态开关，避免误操作

---

## 三、前端对接建议

### 3.1 配置值获取

获取系统配置的推荐方式：

```
1. 业务页面需要某个配置时，调用 GET /api/system/config/key/{configKey}
2. 根据 valueType 转换 configValue 的类型
3. 前端可缓存配置值，避免重复请求
```

### 3.2 内置数据保护

前端应对 `isBuiltin=1` 的配置项做 UI 限制：
- 内置配置项：禁止修改、禁止删除
- 批量删除时内置项会被后端自动跳过，但前端可选择禁用内置项的勾选框

### 3.3 配置类型与值类型

- `configType` 用于区分配置归属，`SYSTEM` 为系统级配置，`CUSTOM` 为业务自定义配置
- `valueType` 告知前端如何解析 `configValue`，前端表单编辑时应根据 `valueType` 展示不同的输入控件：
  - `TEXT`：文本输入框
  - `NUMBER`：数字输入框
  - `BOOLEAN`：开关或单选框
- 后端不校验 `configValue` 是否符合 `valueType`（例如 `valueType=NUMBER` 时后端允许存入非数字字符串），**前端应在提交前自行校验**
- `BOOLEAN` 类型的 `configValue` 以字符串 `"true"` / `"false"` 存储，后端不接受 `"1"` / `"0"` 或布尔值

### 3.4 表单建议

新增/编辑配置的表单建议：
- `configName`：文本输入框（必填）
- `configKey`：文本输入框（必填，编辑时置灰不可修改）
- `configValue`：根据 `valueType` 动态切换输入控件
- `configType`：下拉选择（从字典 `CONFIG_TYPE` 获取选项）
- `valueType`：下拉选择（从字典 `CONFIG_VALUE_TYPE` 获取选项）
- `status`：开关
- `remark`：文本域

---

## 四、接口速查表

| 方法   | 路径                                | 说明             |
| ------ | ----------------------------------- | ---------------- |
| GET    | /api/system/config/page             | 配置分页         |
| GET    | /api/system/config/{id}             | 配置详情         |
| GET    | /api/system/config/key/{configKey}  | 按配置键查询     |
| POST   | /api/system/config                  | 新增配置         |
| PUT    | /api/system/config/{id}             | 修改配置         |
| DELETE | /api/system/config/{id}             | 删除配置         |
| POST   | /api/system/config/batch-delete     | 批量删除         |
| PATCH  | /api/system/config/{id}/status      | 启用/禁用        |
