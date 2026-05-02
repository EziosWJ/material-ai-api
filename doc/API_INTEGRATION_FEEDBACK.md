# API 对接反馈

> 针对前端提出的对接问题，以下是后端的处理结果和说明。

---

## 已解决事项

### 1. 批量删除接口兼容性

**问题**：`DELETE` 携带 JSON body 在部分代理链路中可能被丢弃。

**处理**：所有模块的批量删除接口已统一从 `DELETE /batch` 改为 `POST /batch-delete`。

| 模块 | 旧路径 | 新路径 |
|------|--------|--------|
| 用户 | `DELETE /api/system/user/batch` | `POST /api/system/user/batch-delete` |
| 角色 | `DELETE /api/system/role/batch` | `POST /api/system/role/batch-delete` |
| 菜单 | `DELETE /api/system/menu/batch` | `POST /api/system/menu/batch-delete` |
| 部门 | `DELETE /api/system/dept/batch` | `POST /api/system/dept/batch-delete` |
| 字典类型 | `DELETE /api/system/dict-type/batch` | `POST /api/system/dict-type/batch-delete` |
| 字典数据 | `DELETE /api/system/dict-data/batch` | `POST /api/system/dict-data/batch-delete` |
| 文件 | `DELETE /api/system/file/batch` | `POST /api/system/file/batch-delete` |

请求体不变：`{"ids": [1, 2, 3]}`

### 2. 角色选择列表接口

**问题**：用户分配角色时使用分页接口加载角色列表不合适。

**处理**：新增 `GET /api/system/role/options`，只返回启用角色，按 `sortOrder` 排序。字段与分页记录一致（不含 `menuIds`）。

前端在用户分配角色的下拉选择中应使用此接口替代分页接口。

### 3. 文件下载异常返回格式

**问题**：文件下载失败时后端是否统一返回 JSON 错误。

**处理**：下载/预览接口在文件不存在或无权限时，由全局异常处理返回统一 JSON 格式：

```json
{"code": 404, "message": "数据不存在", "data": null}
```

响应 `Content-Type` 为 `application/json`，前端可正常解析错误信息。

### 4. 顶级部门 parentId 约定

**问题**：新增或编辑顶级部门时 `parentId` 应传 `0` 还是省略。

**说明**：后端约定顶级节点 `parentId = 0`（数据库字段 `NOT NULL DEFAULT 0`）。前端提交 `parentId: 0` 是正确的，无需省略。

### 5. 菜单详情响应字段

**问题**：`GET /api/system/menu/{id}` 文档未展开响应字段。

**说明**：菜单详情返回完整字段，包含 `externalUrl`、`visible`、`isBuiltin`、`remark`、`status`、`createTime`、`updateTime` 等。已补充到 `frontend-api-guide.md` 的 5.3 节。

### 6. 文件分页/详情响应字段

**问题**：`GET /api/system/file/page` 和 `GET /api/system/file/{id}` 文档未展开完整响应字段。

**说明**：文件分页和详情均稳定返回 `status`、`businessModule`、`remark`、`createTime` 字段。已补充到 `frontend-api-guide.md` 的 8.3 和 8.4 节。

### 7. 操作日志详情字段名

**问题**：`GET /api/system/oper-log/{id}` 的请求参数摘要和响应结果摘要字段名不明确。

**说明**：
- 请求参数摘要字段名：**`requestParams`**
- 响应结果摘要字段名：**`responseResult`**

前端无需再兼容其他字段名（`requestParam`、`requestBody`、`responseData` 等），直接使用上述字段名即可。已补充到 `frontend-api-guide.md` 的 9.5 节。

### 8. 静态路由路径

**问题**：后端菜单树是否统一下发单数路径。

**说明**：后端所有 Controller 路径均为单数形式（`/system/user`、`/system/role`、`/system/menu` 等），不存在旧的复数路径。前端菜单树配置应与后端一致。

---

## 待协商事项

### 1. 系统配置模块

后端当前未开发系统配置模块。前端 `system-configs` 页面暂用 mock 数据是合理的，待后端补充正式接口后再迁移。前端无需自行约定接口路径。

### 2. 开发环境后端地址

开发环境后端地址固定为 `http://localhost:8080`。前端可通过以下方式之一对接：
- 配置 Vite proxy 将 `/api` 代理到 `http://localhost:8080`
- 直接设置 `VITE_API_BASE_URL=http://localhost:8080`

---

## 更新的文档

- `doc/frontend-api-guide.md` — 接口详情和速查表已全面更新
- `doc/development-constraints.md` — 批量删除规范已更新
