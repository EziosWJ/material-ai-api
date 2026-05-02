# 接口集成测试结果

> 测试时间：2026-05-02
> 测试类：`cn.ezios.baseapi.admin.SystemApiIntegrationTest`
> 测试框架：Spring Boot Test + MockMvc
> 数据库：本地 MySQL (localhost/base_api)

---

## 测试汇总

| 测试项 | 状态 |
|--------|------|
| 角色选择列表接口 | PASS |
| 角色选择列表字段验证 | PASS |
| 批量删除 POST - 角色 | PASS |
| 批量删除 POST - 用户 | PASS |
| 批量删除 POST - 菜单 | PASS |
| 批量删除 POST - 部门 | PASS |
| 批量删除 POST - 文件 | PASS |
| 批量删除 POST - 字典类型 | PASS |
| 批量删除 POST - 字典数据 | PASS |
| 旧 DELETE /batch 路径不可达 | PASS |
| 文件下载异常返回 JSON | PASS |
| 文件预览异常返回 JSON | PASS |
| 操作日志详情字段名验证 | PASS |
| 菜单详情完整字段验证 | PASS |
| 文件分页接口验证 | PASS |

**结果：15/15 通过，0 失败，0 错误**

---

## 详细测试说明

### 1. 角色选择列表 `GET /api/system/role/options`

- 验证接口可达，返回 code=200
- 返回数组格式
- 包含 `id`、`roleName`、`roleCode`、`status` 等必要字段

### 2. 批量删除 `POST /api/system/xxx/batch-delete`

所有 7 个模块的批量删除接口均已验证：
- 接受 POST 方法 + JSON body `{"ids": [...]}`
- 使用不存在的 ID 时返回 `{"code": 404, "message": "数据不存在"}`
- 接口可达且响应格式正确

### 3. 旧路径 `DELETE /batch` 不可达

- `DELETE /api/system/role/batch` 不再匹配路由
- 返回非 200 状态码（证明旧路径已失效）

### 4. 文件下载/预览异常返回 JSON

- `GET /api/system/file/99999/download`（不存在的文件）返回：
  ```json
  {"code": 404, "message": "数据不存在", "data": null}
  ```
- `GET /api/system/file/99999/view` 同上
- 响应 Content-Type 为 application/json（非文件流）

### 5. 操作日志详情字段名

- 请求参数摘要字段名确认为 `requestParams`（非 `requestParam` 或 `requestBody`）
- 不存在错误的字段名

### 6. 菜单详情完整字段

- 包含 `id`、`parentId`、`menuName`、`menuType`、`path`、`visible`、`status`、`isBuiltin`、`createTime` 等字段

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

- SaToken 拦截器在 MockMvc 环境下未完全生效（这是预存问题，非本次变更引入）
- 现有测试 `BaseApiAdminApplicationTests.protectedApiShouldReturnUnifiedUnauthorizedResponse` 同样存在此问题
