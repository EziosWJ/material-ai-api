# 配置管理模块测试结果

> 测试时间：2026-05-02
> 测试类：`cn.ezios.baseapi.admin.ConfigModuleIntegrationTest`
> 测试框架：Spring Boot Test + MockMvc
> 数据库：本地 MySQL (localhost/base_api)

---

## 测试汇总

| 测试项 | 状态 |
|--------|------|
| 配置分页接口 | PASS |
| 配置分页字段完整性 | PASS |
| 配置分页按类型过滤 | PASS |
| 配置详情 - 内置配置 | PASS |
| 配置详情 - 不存在返回 404 | PASS |
| 按配置键查询 - 内置配置 | PASS |
| 按配置键查询 - 仅返回四个字段 | PASS |
| 按配置键查询 - 不存在返回 404 | PASS |
| 新增配置 | PASS |
| 新增配置 - 重复键返回 400 | PASS |
| 新增配置 - 默认值验证 | PASS |
| 新增配置 - 必填字段校验 | PASS |
| 修改配置 | PASS |
| 修改配置 - 内置禁止修改 | PASS |
| 修改配置 - 重复键返回 400 | PASS |
| 删除配置 | PASS |
| 删除配置 - 内置禁止删除 | PASS |
| 删除配置 - 不存在返回 404 | PASS |
| 批量删除 | PASS |
| 批量删除 - 内置项静默跳过 | PASS |
| 启用/禁用 | PASS |

**结果：21/21 通过，0 失败，0 错误**

---

## 详细测试说明

### 1. 分页查询 `GET /api/system/config/page`

- 验证接口可达，返回 code=200
- 返回 `records`（数组）、`total`、`page`、`pageSize` 字段
- 记录包含完整字段：`id`、`configName`、`configKey`、`configValue`、`configType`、`valueType`、`status`、`isBuiltin`、`createTime`、`updateTime`
- 按 `configType=SYSTEM` 过滤可正常返回

### 2. 配置详情 `GET /api/system/config/{id}`

- 内置配置（id=1，`system.log-clear-enabled`）可正常查询，`isBuiltin=1`
- 不存在的 ID 返回 `{"code": 404, "message": "数据不存在"}`

### 3. 按配置键查询 `GET /api/system/config/key/{configKey}`

- 内置配置按键查询返回 `code=200`
- 仅返回四个字段：`configKey`、`configValue`、`valueType`、`configName`
- 不返回 `id`、`status`、`isBuiltin` 等管理字段
- 不存在的键返回 `{"code": 404, "message": "数据不存在"}`

### 4. 新增配置 `POST /api/system/config`

- 正常新增返回 code=200，查询确认数据正确
- `configKey` 重复返回 `{"code": 400, "message": "配置键已存在"}`
- 仅传 `configName` 和 `configKey` 时，`configType` 默认 `SYSTEM`，`valueType` 默认 `TEXT`，`status` 默认 `1`，`isBuiltin` 为 `0`
- 缺少 `configName` 返回 HTTP 400 + `{"data": {"configName": "配置名称不能为空"}}`
- 缺少 `configKey` 返回 HTTP 400 + `{"data": {"configKey": "配置键不能为空"}}`

### 5. 修改配置 `PUT /api/system/config/{id}`

- 正常修改返回 code=200，详情验证修改生效
- `isBuiltin=1` 的内置配置返回 `{"code": 400, "message": "内置配置项禁止修改"}`
- `configKey` 与已有配置冲突返回 `{"code": 400, "message": "配置键已存在"}`

### 6. 删除配置 `DELETE /api/system/config/{id}`

- 正常删除返回 code=200，再次查询详情返回 404（逻辑删除生效）
- `isBuiltin=1` 的内置配置返回 `{"code": 400, "message": "内置配置项禁止删除"}`
- 不存在的 ID 返回 `{"code": 404}`

### 7. 批量删除 `POST /api/system/config/batch-delete`

- 批量删除多个普通配置返回 code=200
- 包含内置配置（id=1）和普通配置时，内置项被静默跳过，普通项正常删除
- 删除后内置配置仍可通过详情接口查询到（`isBuiltin=1`）

### 8. 启用/禁用 `PATCH /api/system/config/{id}/status`

- 禁用后详情查询 `status=0`
- 禁用后按配置键查询返回 404（仅返回 `status=1` 的配置）
- 重新启用后按配置键查询返回 200

---

## 测试发现的问题及修复

### 问题：逻辑删除后唯一索引冲突导致 500 错误

**现象：** 创建配置 -> 逻辑删除 -> 再次创建同 key 的配置，接口返回 500（`DuplicateKeyException`）

**原因：** 数据库唯一索引 `uk_config_key` 不受 MyBatis-Plus `@TableLogic` 影响。`ensureConfigKeyUnique` 方法的 `selectCount` 受逻辑删除过滤，不计算已删除行，但数据库层面唯一索引仍阻止插入。

**修复：** 在 `ConfigServiceImpl.create` 方法中捕获 `DuplicateKeyException`，转换为业务异常返回 `{"code": 400, "message": "配置键已存在"}`。

---

## 测试配置

测试使用 `src/test/resources/application.yml` 覆盖数据库连接为本地：

```yaml
spring:
  datasource:
    url: jdbc:mysql:///base_api?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: root123456
```

## 已知问题

- SaToken 拦截器在 MockMvc 环境下未完全生效（既有问题，非本次引入）
