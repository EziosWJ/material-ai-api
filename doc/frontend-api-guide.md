# 前端对接指南

本文档面向前端开发人员，描述后端已完成的所有接口、数据结构和对接要点。

---

## 一、全局约定

### 1.1 接口前缀

所有接口统一前缀：`/api`

### 1.2 统一响应格式

```json
// 成功
{"code": 200, "message": "success", "data": {...}}

// 失败
{"code": 400, "message": "错误信息", "data": {...}}
```

### 1.3 统一分页格式

**请求参数（Query）：**

| 参数     | 类型  | 说明   |
| -------- | ----- | ------ |
| page     | int   | 页码   |
| pageSize | int   | 每页条数 |

**响应 data：**

```json
{
  "records": [],
  "total": 100,
  "page": 1,
  "pageSize": 10
}
```

### 1.4 认证方式

- 请求头：`Authorization: Bearer <token>`
- Token 有效期 2 小时，滑动续期
- 允许同一账号多端同时登录
- 未登录或 Token 失效返回 `401`

### 1.5 状态码约定

| code | 含义             |
| ---- | ---------------- |
| 200  | 成功             |
| 400  | 参数校验失败     |
| 401  | 未登录 / Token失效 |
| 403  | 无权限           |
| 404  | 数据不存在       |
| 500  | 系统错误         |

### 1.6 字段命名

- 所有 JSON 字段使用 `camelCase`（驼峰）
- 通用状态字段：`status`，`1=启用`，`0=禁用`
- 通用内置标识：`isBuiltin`，`1=内置`，`0=普通`

### 1.7 参数校验失败响应

```json
{"code": 400, "message": "参数校验失败", "data": {"username": "用户名不能为空"}}
```

---

## 二、认证模块

### 2.1 登录

```
POST /api/auth/login
```

**请求：**

```json
{"username": "admin", "password": "admin123"}
```

**响应 data：**

```json
{
  "tokenName": "Authorization",
  "tokenValue": "Bearer eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 7200
}
```

**前端对接要点：**
- 登录成功后，将 `tokenValue` 存入本地（localStorage/cookie），后续请求放入 `Authorization` 请求头
- `expiresIn` 为秒数，前端可据此做 Token 过期提示
- 不需要验证码（第一版）

### 2.2 退出登录

```
POST /api/auth/logout
```

**前端对接要点：**
- 退出后清除本地 Token
- 只退出当前设备，不影响其他端

### 2.3 获取当前用户信息

```
GET /api/auth/me
```

**响应 data：**

```json
{
  "id": 1,
  "username": "admin",
  "nickname": "超级管理员",
  "avatar": "/api/system/file/1/view",
  "phone": "13800000000",
  "email": "admin@example.com",
  "dept": {
    "id": 1,
    "deptName": "总公司"
  },
  "roles": [
    {"id": 1, "roleName": "超级管理员", "roleCode": "admin"}
  ],
  "lastLoginTime": "2026-05-01 10:00:00",
  "lastLoginIp": "127.0.0.1"
}
```

**前端对接要点：**
- 登录后立即调用，获取用户头像、昵称、角色等用于页面展示
- 需登录后访问

### 2.4 获取当前用户菜单

```
GET /api/auth/menus
```

**响应 data（树形结构）：**

```json
[
  {
    "id": 1,
    "parentId": 0,
    "menuName": "系统管理",
    "menuType": "DIR",
    "path": "/system",
    "component": null,
    "icon": "setting",
    "permissionCode": null,
    "sortOrder": 1,
    "visible": 1,
    "children": [
      {
        "id": 2,
        "parentId": 1,
        "menuName": "用户管理",
        "menuType": "MENU",
        "path": "/system/user",
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
```

**前端对接要点：**
- 根据返回的菜单树动态生成侧边栏路由和导航
- `menuType`：`DIR`=目录、`MENU`=菜单、`LINK`=外链
- `component` 字段为前端本地组件路径字符串，如 `system/user/index`，前端需自行映射到实际组件
- `path` 由后端维护，前端直接用于路由配置
- `visible=1` 才显示在菜单栏
- 根节点 `parentId=0`
- 按 `sortOrder` 排序

---

## 三、用户管理

### 3.1 用户分页

```
GET /api/system/user/page?page=1&pageSize=10&username=&nickname=&phone=&email=&status=&deptId=
```

**响应 data.records[]：**

```json
{
  "id": 1,
  "username": "admin",
  "nickname": "超级管理员",
  "phone": "13800000000",
  "email": "admin@example.com",
  "avatar": "...",
  "gender": "MALE",
  "status": 1,
  "deptId": 1,
  "deptName": "总公司",
  "roles": [{"id": 1, "roleName": "超级管理员"}],
  "lastLoginTime": "2026-05-01 10:00:00",
  "createTime": "2026-01-01 00:00:00"
}
```

**前端对接要点：**
- 不会返回密码字段
- 列表展示角色名和部门名

### 3.2 用户详情

```
GET /api/system/user/{id}
```

### 3.3 新增用户

```
POST /api/system/user
```

**请求：**

```json
{
  "username": "zhangsan",
  "nickname": "张三",
  "phone": "13800000001",
  "email": "zhangsan@example.com",
  "gender": "MALE",
  "deptId": 2,
  "status": 1,
  "remark": "备注"
}
```

**前端对接要点：**
- 新增用户默认密码为 `admin123`，无需前端传入
- 部门选择使用部门选择树（见部门模块 `options` 接口）

### 3.4 修改用户

```
PUT /api/system/user/{id}
```

### 3.5 删除用户

```
DELETE /api/system/user/{id}
```

### 3.6 批量删除

```
DELETE /api/system/user/batch
Body: {"ids": [1, 2, 3]}
```

### 3.7 启用/禁用

```
PATCH /api/system/user/{id}/status
Body: {"status": 1}
```

### 3.8 分配角色

```
PUT /api/system/user/{id}/roles
Body: {"roleIds": [1, 2]}
```

**前端对接要点：**
- 覆盖式保存，即每次传入的 `roleIds` 会完全替换原有角色
- 回显时需先查询用户详情获取当前角色列表

### 3.9 重置密码

```
PUT /api/system/user/{id}/reset-password
```

**响应 data：** 返回新密码明文（默认 `admin123`）

**前端对接要点：**
- 重置后将新密码告知管理员，由管理员线下通知用户

### 3.10 修改当前用户密码

```
PUT /api/system/user/me/password
Body: {"oldPassword": "old123", "newPassword": "new123"}
```

### 3.11 修改当前用户头像

```
PUT /api/system/user/me/avatar
Body: {"avatar": "/api/system/file/1/view"}
```

**前端对接要点：**
- 头像 URL 来自文件上传接口返回的 `accessUrl`
- 先上传文件获取 URL，再调用此接口保存

---

## 四、角色管理

### 4.1 角色分页

```
GET /api/system/role/page?page=1&pageSize=10&roleName=&roleCode=&status=
```

**响应 data.records[]：**

```json
{
  "id": 1,
  "roleName": "超级管理员",
  "roleCode": "admin",
  "status": 1,
  "sortOrder": 1,
  "isBuiltin": 1,
  "remark": "系统内置角色",
  "createTime": "2026-01-01 00:00:00"
}
```

### 4.2 角色详情

```
GET /api/system/role/{id}
```

**响应 data：** 包含角色基础信息和已分配菜单 ID 列表（`menuIds`），用于编辑回显

### 4.3 新增角色

```
POST /api/system/role
Body: {"roleName": "测试角色", "roleCode": "test", "status": 1, "sortOrder": 0, "remark": ""}
```

### 4.4 修改角色

```
PUT /api/system/role/{id}
```

**前端对接要点：**
- `isBuiltin=1` 的内置角色禁止修改 `roleCode`

### 4.5 删除角色

```
DELETE /api/system/role/{id}
```

**前端对接要点：**
- `isBuiltin=1` 的内置角色禁止删除
- 已绑定用户的角色禁止删除

### 4.6 批量删除

```
DELETE /api/system/role/batch
Body: {"ids": [1, 2, 3]}
```

### 4.7 启用/禁用

```
PATCH /api/system/role/{id}/status
Body: {"status": 1}
```

### 4.8 分配菜单

```
PUT /api/system/role/{id}/menus
Body: {"menuIds": [1, 2, 3]}
```

**前端对接要点：**
- 覆盖式保存
- 菜单选择使用菜单树接口
- 回显时从角色详情获取 `menuIds`

---

## 五、菜单管理

### 5.1 菜单树

```
GET /api/system/menu/tree
```

**响应 data：** 树形菜单数组，字段同 `/api/auth/menus`

### 5.2 菜单分页

```
GET /api/system/menu/page?page=1&pageSize=10&menuName=&menuType=&status=&visible=
```

### 5.3 菜单详情

```
GET /api/system/menu/{id}
```

### 5.4 新增菜单

```
POST /api/system/menu
```

**请求：**

```json
{
  "parentId": 1,
  "menuName": "用户管理",
  "menuType": "MENU",
  "path": "/system/user",
  "component": "system/user/index",
  "icon": "user",
  "permissionCode": "system:user:list",
  "sortOrder": 1,
  "visible": 1,
  "status": 1,
  "remark": ""
}
```

**前端对接要点：**
- `menuType` 取值：`DIR`（目录）、`MENU`（菜单）、`LINK`（外链）
- `parentId=0` 为根节点
- 外链菜单填 `externalUrl`，`component` 可为空
- `permissionCode` 非空时必须唯一

### 5.5 修改菜单

```
PUT /api/system/menu/{id}
```

**前端对接要点：**
- `isBuiltin=1` 的内置菜单不建议修改关键标识字段

### 5.6 删除菜单

```
DELETE /api/system/menu/{id}
```

**前端对接要点：**
- `isBuiltin=1` 的内置菜单禁止删除
- 有子菜单的禁止删除
- 已被角色绑定的禁止删除

### 5.7 批量删除

```
DELETE /api/system/menu/batch
Body: {"ids": [1, 2, 3]}
```

### 5.8 启用/禁用

```
PATCH /api/system/menu/{id}/status
Body: {"status": 1}
```

---

## 六、部门管理

### 6.1 部门树

```
GET /api/system/dept/tree
```

**响应 data：** 树形部门数组，字段包含 `id`、`parentId`、`deptName`、`deptCode`、`leader`、`phone`、`email`、`sortOrder`、`status`、`children`

### 6.2 部门选择树（下拉用）

```
GET /api/system/dept/options
```

**响应 data：** 只返回启用且未删除的部门树

**前端对接要点：**
- 用户维护页面的部门下拉选择使用此接口
- 返回简化数据，适合做树形选择器

### 6.3 部门分页

```
GET /api/system/dept/page?page=1&pageSize=10&deptName=&deptCode=&status=
```

### 6.4 部门详情

```
GET /api/system/dept/{id}
```

### 6.5 新增部门

```
POST /api/system/dept
Body: {
  "parentId": 1,
  "deptName": "研发部",
  "deptCode": "RD",
  "leader": "张三",
  "phone": "13800000001",
  "email": "rd@example.com",
  "sortOrder": 1,
  "status": 1,
  "remark": ""
}
```

### 6.6 修改部门

```
PUT /api/system/dept/{id}
```

**前端对接要点：**
- `isBuiltin=1` 的内置根部门不建议修改编码

### 6.7 删除部门

```
DELETE /api/system/dept/{id}
```

**前端对接要点：**
- `isBuiltin=1` 的内置根部门禁止删除
- 有子部门的禁止删除
- 有用户归属的部门禁止删除

### 6.8 批量删除

```
DELETE /api/system/dept/batch
Body: {"ids": [1, 2, 3]}
```

### 6.9 启用/禁用

```
PATCH /api/system/dept/{id}/status
Body: {"status": 1}
```

---

## 七、字典管理

### 7.1 字典类型分页

```
GET /api/system/dict-type/page?page=1&pageSize=10&dictName=&dictCode=&status=
```

**响应 data.records[]：**

```json
{
  "id": 1,
  "dictName": "性别",
  "dictCode": "gender",
  "status": 1,
  "sortOrder": 1,
  "isBuiltin": 1,
  "remark": "",
  "createTime": "2026-01-01 00:00:00"
}
```

### 7.2 字典类型详情

```
GET /api/system/dict-type/{id}
```

### 7.3 新增字典类型

```
POST /api/system/dict-type
Body: {"dictName": "测试字典", "dictCode": "test", "status": 1, "sortOrder": 0, "remark": ""}
```

### 7.4 修改字典类型

```
PUT /api/system/dict-type/{id}
```

**前端对接要点：**
- `isBuiltin=1` 的字典类型禁止修改编码

### 7.5 删除字典类型

```
DELETE /api/system/dict-type/{id}
```

**前端对接要点：**
- `isBuiltin=1` 的字典类型禁止删除
- 下有字典数据的禁止删除

### 7.6 字典类型批量删除

```
DELETE /api/system/dict-type/batch
Body: {"ids": [1, 2, 3]}
```

### 7.7 字典类型启用/禁用

```
PATCH /api/system/dict-type/{id}/status
Body: {"status": 1}
```

### 7.8 字典数据分页

```
GET /api/system/dict-data/page?page=1&pageSize=10&dictTypeId=1&dictCode=&dictLabel=&dictValue=
```

**响应 data.records[]：**

```json
{
  "id": 1,
  "dictTypeId": 1,
  "dictLabel": "男",
  "dictValue": "MALE",
  "sortOrder": 1,
  "remark": "",
  "createTime": "2026-01-01 00:00:00"
}
```

### 7.9 字典数据详情

```
GET /api/system/dict-data/{id}
```

### 7.10 新增字典数据

```
POST /api/system/dict-data
Body: {"dictTypeId": 1, "dictLabel": "男", "dictValue": "MALE", "sortOrder": 1, "remark": ""}
```

### 7.11 修改字典数据

```
PUT /api/system/dict-data/{id}
```

### 7.12 删除字典数据

```
DELETE /api/system/dict-data/{id}
```

### 7.13 字典数据批量删除

```
DELETE /api/system/dict-data/batch
Body: {"ids": [1, 2, 3]}
```

### 7.14 按字典编码查询字典项（前端常用）

```
GET /api/system/dict/{dictCode}/items
```

**响应 data：**

```json
[
  {"label": "男", "value": "MALE", "sortOrder": 1},
  {"label": "女", "value": "FEMALE", "sortOrder": 2}
]
```

**前端对接要点：**
- 这是前端最常用的字典接口，用于下拉框、单选框等表单控件
- 只返回启用字典类型下的未删除数据
- 前端可按 `dictCode` 缓存字典项，减少重复请求

---

## 八、文件管理

### 8.1 单文件上传

```
POST /api/system/file/upload
Content-Type: multipart/form-data
```

| 参数            | 类型   | 说明               |
| --------------- | ------ | ------------------ |
| file            | File   | 文件（必填）       |
| businessModule  | String | 业务模块标识（可选） |
| remark          | String | 备注（可选）       |

**响应 data：**

```json
{
  "id": 1,
  "originalName": "avatar.png",
  "storageName": "20260502_abc123.png",
  "extension": "png",
  "mimeType": "image/png",
  "fileSize": 102400,
  "accessUrl": "/api/system/file/1/view",
  "businessModule": "user",
  "remark": "",
  "createTime": "2026-05-02 10:00:00"
}
```

### 8.2 批量文件上传

```
POST /api/system/file/upload-batch
Content-Type: multipart/form-data
```

| 参数           | 类型    | 说明               |
| -------------- | ------- | ------------------ |
| files          | File[]  | 多个文件（必填）   |
| businessModule | String  | 业务模块标识（可选） |
| remark         | String  | 备注（可选）       |

### 8.3 文件分页

```
GET /api/system/file/page?page=1&pageSize=10&originalName=&businessModule=&mimeType=&status=
```

### 8.4 文件详情

```
GET /api/system/file/{id}
```

### 8.5 修改文件信息

```
PUT /api/system/file/{id}
Body: {"businessModule": "user", "remark": "新备注"}
```

### 8.6 删除文件

```
DELETE /api/system/file/{id}
```

### 8.7 批量删除

```
DELETE /api/system/file/batch
Body: {"ids": [1, 2, 3]}
```

### 8.8 文件启用/禁用

```
PATCH /api/system/file/{id}/status
Body: {"status": 1}
```

### 8.9 文件下载

```
GET /api/system/file/{id}/download
```

**前端对接要点：**
- 返回文件流，前端需处理下载
- Content-Disposition 设置了文件名

### 8.10 文件预览

```
GET /api/system/file/{id}/view
```

**前端对接要点：**
- 适用于图片、PDF 等浏览器可直接预览的文件
- 用户头像、图片等场景直接使用此 URL 即可
- 单文件最大 50MB

---

## 九、日志管理

### 9.1 登录日志分页

```
GET /api/system/login-log/page?page=1&pageSize=10&username=&loginStatus=&loginIp=
```

**响应 data.records[]：**

```json
{
  "id": 1,
  "username": "admin",
  "loginStatus": "SUCCESS",
  "loginIp": "127.0.0.1",
  "browser": "Chrome",
  "os": "Windows",
  "message": "登录成功",
  "loginTime": "2026-05-02 10:00:00"
}
```

### 9.2 登录日志详情

```
GET /api/system/login-log/{id}
```

### 9.3 清空登录日志

```
DELETE /api/system/login-log/clear
```

**前端对接要点：**
- 仅开发环境可用，生产环境返回 `403`
- 前端应根据环境决定是否展示此按钮

### 9.4 操作日志分页

```
GET /api/system/oper-log/page?page=1&pageSize=10&moduleName=&operationType=&operatorName=&operationStatus=
```

**响应 data.records[]：**

```json
{
  "id": 1,
  "moduleName": "用户管理",
  "operationType": "CREATE",
  "requestMethod": "POST",
  "requestUrl": "/api/system/user",
  "operatorName": "admin",
  "operatorIp": "127.0.0.1",
  "operationStatus": "SUCCESS",
  "costTime": 120,
  "operationTime": "2026-05-02 10:00:00"
}
```

### 9.5 操作日志详情

```
GET /api/system/oper-log/{id}
```

**响应 data：** 包含请求参数摘要和响应结果摘要（敏感字段已脱敏）

### 9.6 清空操作日志

```
DELETE /api/system/oper-log/clear
```

**前端对接要点：**
- 同登录日志清空，仅开发环境可用

---

## 十、前端对接建议

### 10.1 登录流程

```
1. 用户输入账号密码 → POST /api/auth/login
2. 保存 tokenValue 到 localStorage
3. 请求拦截器自动添加 Authorization 头
4. 调用 GET /api/auth/me 获取用户信息
5. 调用 GET /api/auth/menus 获取菜单树，动态生成路由
6. 401 响应时跳转登录页
```

### 10.2 Token 刷新

- 后端采用滑动续期，每次有效请求自动续期
- 前端无需主动刷新 Token
- 仅需处理 401 响应做登出跳转

### 10.3 字典使用

```
1. 启动时或首次需要时调用 GET /api/system/dict/{dictCode}/items
2. 前端缓存字典项数据
3. 表单的下拉框、单选框等从缓存读取
4. 列表展示时用 value→label 映射
```

### 10.4 文件上传

```
1. 上传文件 → POST /api/system/file/upload
2. 获取返回的 accessUrl
3. 将 accessUrl 存入业务字段（如用户头像、图片等）
4. 展示时直接使用 accessUrl 作为 img src 或下载链接
```

### 10.5 内置数据保护

前端应对 `isBuiltin=1` 的数据做 UI 限制：
- 内置用户：禁止删除
- 内置角色：禁止删除、禁止修改 roleCode
- 内置菜单：禁止删除
- 内置部门：禁止删除、禁止修改编码
- 内置字典类型：禁止删除、禁止修改编码

### 10.6 树形数据

菜单和部门均为树形结构，统一用 `children` 字段。前端可使用通用树组件处理：
- 菜单选择器（角色分配菜单）
- 部门选择器（用户选择部门）
- 部门树管理
- 菜单树管理

### 10.7 CORS

开发环境前端地址：`http://localhost:5173`，已配置 CORS 允许跨域。

### 10.8 API 文档

开发环境可通过 Knife4j 查看完整 API 文档，地址通常为：`http://localhost:8080/doc.html`

---

## 十一、初始化账号

| 账号     | 密码      | 说明         |
| -------- | --------- | ------------ |
| admin    | admin123  | 超级管理员   |

---

## 十二、接口速查表

| 模块   | 方法   | 路径                                  | 说明             |
| ------ | ------ | ------------------------------------- | ---------------- |
| 认证   | POST   | /api/auth/login                       | 登录             |
| 认证   | POST   | /api/auth/logout                      | 退出             |
| 认证   | GET    | /api/auth/me                          | 当前用户信息     |
| 认证   | GET    | /api/auth/menus                       | 当前用户菜单     |
| 用户   | GET    | /api/system/user/page                 | 用户分页         |
| 用户   | GET    | /api/system/user/{id}                 | 用户详情         |
| 用户   | POST   | /api/system/user                      | 新增用户         |
| 用户   | PUT    | /api/system/user/{id}                 | 修改用户         |
| 用户   | DELETE | /api/system/user/{id}                 | 删除用户         |
| 用户   | DELETE | /api/system/user/batch                | 批量删除         |
| 用户   | PATCH  | /api/system/user/{id}/status          | 启用/禁用        |
| 用户   | PUT    | /api/system/user/{id}/roles           | 分配角色         |
| 用户   | PUT    | /api/system/user/{id}/reset-password  | 重置密码         |
| 用户   | PUT    | /api/system/user/me/password          | 修改密码         |
| 用户   | PUT    | /api/system/user/me/avatar            | 修改头像         |
| 角色   | GET    | /api/system/role/page                 | 角色分页         |
| 角色   | GET    | /api/system/role/{id}                 | 角色详情         |
| 角色   | POST   | /api/system/role                      | 新增角色         |
| 角色   | PUT    | /api/system/role/{id}                 | 修改角色         |
| 角色   | DELETE | /api/system/role/{id}                 | 删除角色         |
| 角色   | DELETE | /api/system/role/batch                | 批量删除         |
| 角色   | PATCH  | /api/system/role/{id}/status          | 启用/禁用        |
| 角色   | PUT    | /api/system/role/{id}/menus           | 分配菜单         |
| 菜单   | GET    | /api/system/menu/tree                 | 菜单树           |
| 菜单   | GET    | /api/system/menu/page                 | 菜单分页         |
| 菜单   | GET    | /api/system/menu/{id}                 | 菜单详情         |
| 菜单   | POST   | /api/system/menu                      | 新增菜单         |
| 菜单   | PUT    | /api/system/menu/{id}                 | 修改菜单         |
| 菜单   | DELETE | /api/system/menu/{id}                 | 删除菜单         |
| 菜单   | DELETE | /api/system/menu/batch                | 批量删除         |
| 菜单   | PATCH  | /api/system/menu/{id}/status          | 启用/禁用        |
| 部门   | GET    | /api/system/dept/tree                 | 部门树           |
| 部门   | GET    | /api/system/dept/page                 | 部门分页         |
| 部门   | GET    | /api/system/dept/options              | 部门选择树       |
| 部门   | GET    | /api/system/dept/{id}                 | 部门详情         |
| 部门   | POST   | /api/system/dept                      | 新增部门         |
| 部门   | PUT    | /api/system/dept/{id}                 | 修改部门         |
| 部门   | DELETE | /api/system/dept/{id}                 | 删除部门         |
| 部门   | DELETE | /api/system/dept/batch                | 批量删除         |
| 部门   | PATCH  | /api/system/dept/{id}/status          | 启用/禁用        |
| 字典   | GET    | /api/system/dict-type/page            | 字典类型分页     |
| 字典   | GET    | /api/system/dict-type/{id}            | 字典类型详情     |
| 字典   | POST   | /api/system/dict-type                 | 新增字典类型     |
| 字典   | PUT    | /api/system/dict-type/{id}            | 修改字典类型     |
| 字典   | DELETE | /api/system/dict-type/{id}            | 删除字典类型     |
| 字典   | DELETE | /api/system/dict-type/batch           | 字典类型批量删除 |
| 字典   | PATCH  | /api/system/dict-type/{id}/status     | 字典类型启禁用   |
| 字典   | GET    | /api/system/dict-data/page            | 字典数据分页     |
| 字典   | GET    | /api/system/dict-data/{id}            | 字典数据详情     |
| 字典   | POST   | /api/system/dict-data                 | 新增字典数据     |
| 字典   | PUT    | /api/system/dict-data/{id}            | 修改字典数据     |
| 字典   | DELETE | /api/system/dict-data/{id}            | 删除字典数据     |
| 字典   | DELETE | /api/system/dict-data/batch           | 字典数据批量删除 |
| 字典   | GET    | /api/system/dict/{dictCode}/items     | 字典项查询       |
| 文件   | POST   | /api/system/file/upload               | 单文件上传       |
| 文件   | POST   | /api/system/file/upload-batch         | 批量文件上传     |
| 文件   | GET    | /api/system/file/page                 | 文件分页         |
| 文件   | GET    | /api/system/file/{id}                 | 文件详情         |
| 文件   | PUT    | /api/system/file/{id}                 | 修改文件信息     |
| 文件   | DELETE | /api/system/file/{id}                 | 删除文件         |
| 文件   | DELETE | /api/system/file/batch                | 批量删除         |
| 文件   | PATCH  | /api/system/file/{id}/status          | 文件启禁用       |
| 文件   | GET    | /api/system/file/{id}/download        | 文件下载         |
| 文件   | GET    | /api/system/file/{id}/view            | 文件预览         |
| 日志   | GET    | /api/system/login-log/page            | 登录日志分页     |
| 日志   | GET    | /api/system/login-log/{id}            | 登录日志详情     |
| 日志   | DELETE | /api/system/login-log/clear           | 清空登录日志     |
| 日志   | GET    | /api/system/oper-log/page             | 操作日志分页     |
| 日志   | GET    | /api/system/oper-log/{id}             | 操作日志详情     |
| 日志   | DELETE | /api/system/oper-log/clear            | 清空操作日志     |
