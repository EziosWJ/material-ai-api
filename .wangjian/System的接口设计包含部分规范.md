# 后端接口设计总结

## 1. 接口设计目标

本项目后端接口用于支撑前后端分离的通用后台管理系统模板。

接口设计目标：

- 统一接口前缀
- 统一响应格式
- 统一分页结构
- 统一异常处理
- 支持 Sa-Token 登录认证
- 支持用户、角色、菜单、部门、字典、文件等基础后台模块
- 第一版以可用、规范、易扩展为主
- 暂不做复杂按钮权限、接口权限、数据权限
- 暂不做验证码
- 暂不做 Excel 导出

---

## 2. 接口统一规范

### 2.1 接口前缀

所有接口统一使用：

```text
/api
```

示例：

```text
/api/auth/login
/api/system/users/page
/api/system/roles/page
/api/system/menus/tree
```

---

### 2.2 统一响应结构

所有接口统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

字段说明：

```text
code：业务响应码
message：响应消息
data：响应数据
```

---

### 2.3 统一分页结构

分页接口统一返回：

```json
{
  "records": [],
  "total": 100,
  "page": 1,
  "pageSize": 10
}
```

字段说明：

```text
records：当前页数据列表
total：总记录数
page：当前页码
pageSize：每页数量
```

---

### 2.4 响应码规范

统一业务响应码：

```text
200 成功
400 参数错误
401 未登录或 token 失效
403 无权限
404 数据不存在
500 系统错误
```

---

### 2.5 异常处理规范

接口统一使用全局异常处理。

参数校验失败时，需要返回字段级错误信息。

示例：

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

---

## 3. 认证接口设计

### 3.1 认证接口列表

认证模块提供以下接口：

```text
POST /api/auth/login
POST /api/auth/logout
GET  /api/auth/me
GET  /api/auth/menus
```

---

### 3.2 登录接口

接口：

```text
POST /api/auth/login
```

请求参数：

```json
{
  "username": "admin",
  "password": "admin123"
}
```

说明：

- 第一版只使用用户名和密码登录
- 暂不做验证码
- 用户名作为登录账号
- 用户名唯一
- 密码使用 BCrypt 加密存储
- 密码错误时记录登录失败日志
- 登录成功时记录登录成功日志
- 登录成功后更新用户最后登录时间和最后登录 IP

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "tokenName": "Authorization",
    "tokenValue": "Bearer xxxxxx",
    "expiresIn": 7200
  }
}
```

---

### 3.3 退出登录接口

接口：

```text
POST /api/auth/logout
```

说明：

- 退出当前 token
- 不影响同一账号其他设备登录状态

返回示例：

```json
{
  "code": 200,
  "message": "退出成功",
  "data": null
}
```

---

### 3.4 当前用户信息接口

接口：

```text
GET /api/auth/me
```

说明：

用于获取当前登录用户信息。

返回内容建议包含：

```text
用户 ID
用户名
昵称
头像
手机号
邮箱
部门信息
角色信息
最后登录时间
最后登录 IP
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "管理员",
    "avatar": "",
    "phone": "",
    "email": "",
    "deptId": 1,
    "deptName": "总部",
    "roles": [
      {
        "id": 1,
        "roleName": "超级管理员",
        "roleCode": "admin"
      }
    ],
    "lastLoginTime": "2026-05-02 10:00:00",
    "lastLoginIp": "127.0.0.1"
  }
}
```

---

### 3.5 当前用户菜单接口

接口：

```text
GET /api/auth/menus
```

说明：

- 后端根据当前用户角色查询可见菜单
- 前端只渲染后端返回的菜单
- 菜单包含目录、菜单、外链
- 支持图标、排序、隐藏状态、路由路径、组件路径

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "parentId": 0,
      "menuName": "系统管理",
      "menuType": "DIR",
      "path": "/system",
      "component": null,
      "icon": "settings",
      "permissionCode": "system",
      "sortOrder": 1,
      "visible": 1,
      "children": [
        {
          "id": 2,
          "parentId": 1,
          "menuName": "用户管理",
          "menuType": "MENU",
          "path": "/system/users",
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

## 4. Token 设计

### 4.1 Token 传递方式

Token 通过请求头传递。

统一使用：

```http
Authorization: Bearer <token>
```

---

### 4.2 Token 有效期

Token 有效期：

```text
2 小时
```

---

### 4.3 Token 刷新策略

采用滑动续期：

- 用户活跃时自动延长有效期
- 用户无操作超过 2 小时后需要重新登录

---

### 4.4 多端登录策略

允许同一账号多端同时登录。

退出登录时：

- 只退出当前 token
- 不影响其他设备

---

## 5. 用户接口设计

### 5.1 用户接口列表

```text
GET    /api/system/users/page
GET    /api/system/users/{id}
POST   /api/system/users
PUT    /api/system/users/{id}
DELETE /api/system/users/{id}
DELETE /api/system/users/batch
PATCH  /api/system/users/{id}/status
PUT    /api/system/users/{id}/roles
PUT    /api/system/users/{id}/reset-password
PUT    /api/system/users/me/password
PUT    /api/system/users/me/avatar
```

---

### 5.2 用户分页接口

接口：

```text
GET /api/system/users/page
```

用途：

- 用户管理页分页查询
- 支持条件查询

查询条件建议：

```text
username
nickname
phone
email
status
deptId
page
pageSize
```

---

### 5.3 用户详情接口

接口：

```text
GET /api/system/users/{id}
```

用途：

- 获取用户详情
- 编辑用户前回显数据

---

### 5.4 新增用户接口

接口：

```text
POST /api/system/users
```

规则：

- 用户名必填
- 用户名唯一
- 新增用户时密码使用默认密码
- 默认密码为配置项，不硬编码
- 默认密码初始值为 `admin123`
- 密码入库前使用 BCrypt 加密
- 手机号、邮箱不要求唯一
- 用户可以绑定部门
- 用户可以分配角色

---

### 5.5 修改用户接口

接口：

```text
PUT /api/system/users/{id}
```

规则：

- 用户名不建议随意修改
- 手机号、邮箱可以修改
- 昵称可以修改
- 头像可以修改
- 部门可以修改
- 状态可以修改
- 角色分配可通过单独接口处理

---

### 5.6 删除用户接口

接口：

```text
DELETE /api/system/users/{id}
```

规则：

- 执行逻辑删除
- 不物理删除数据
- 内置管理员用户建议禁止删除

---

### 5.7 批量删除用户接口

接口：

```text
DELETE /api/system/users/batch
```

请求示例：

```json
{
  "ids": [1, 2, 3]
}
```

规则：

- 执行逻辑删除
- 内置管理员用户禁止删除

---

### 5.8 修改用户状态接口

接口：

```text
PATCH /api/system/users/{id}/status
```

请求示例：

```json
{
  "status": 1
}
```

规则：

```text
1 = 启用
0 = 禁用
```

账号禁用后禁止登录。

---

### 5.9 分配用户角色接口

接口：

```text
PUT /api/system/users/{id}/roles
```

请求示例：

```json
{
  "roleIds": [1, 2]
}
```

说明：

- 一个用户可以拥有多个角色
- 覆盖式保存用户角色关系

---

### 5.10 重置密码接口

接口：

```text
PUT /api/system/users/{id}/reset-password
```

规则：

- 重置为默认密码
- 默认密码从配置文件读取
- 默认值为 `admin123`
- 重置后返回新密码给前端
- 入库前使用 BCrypt 加密

---

### 5.11 修改当前用户密码接口

接口：

```text
PUT /api/system/users/me/password
```

请求示例：

```json
{
  "oldPassword": "old123",
  "newPassword": "new123"
}
```

规则：

- 需要校验旧密码
- 新密码使用 BCrypt 加密后保存

---

### 5.12 修改当前用户头像接口

接口：

```text
PUT /api/system/users/me/avatar
```

说明：

- 用于当前用户修改自己的头像
- 头像文件建议通过文件上传接口上传
- 此接口只更新用户头像地址

---

## 6. 角色接口设计

### 6.1 角色接口列表

```text
GET    /api/system/roles/page
GET    /api/system/roles/{id}
POST   /api/system/roles
PUT    /api/system/roles/{id}
DELETE /api/system/roles/{id}
DELETE /api/system/roles/batch
PATCH  /api/system/roles/{id}/status
PUT    /api/system/roles/{id}/menus
```

---

### 6.2 角色分页接口

接口：

```text
GET /api/system/roles/page
```

用途：

- 角色管理页分页查询
- 用户分配角色时，也可以通过分页条件查询角色
- 不单独提供所有启用角色列表接口

查询条件建议：

```text
roleName
roleCode
status
page
pageSize
```

---

### 6.3 角色详情接口

接口：

```text
GET /api/system/roles/{id}
```

---

### 6.4 新增角色接口

接口：

```text
POST /api/system/roles
```

规则：

- 角色编码唯一
- 角色支持启用 / 禁用
- 角色支持排序

---

### 6.5 修改角色接口

接口：

```text
PUT /api/system/roles/{id}
```

规则：

- 内置角色禁止修改角色编码

---

### 6.6 删除角色接口

接口：

```text
DELETE /api/system/roles/{id}
```

规则：

- 删除前检查是否已有用户绑定
- 内置角色禁止删除
- 执行逻辑删除

---

### 6.7 批量删除角色接口

接口：

```text
DELETE /api/system/roles/batch
```

规则：

- 删除前检查是否已有用户绑定
- 内置角色禁止删除
- 执行逻辑删除

---

### 6.8 修改角色状态接口

接口：

```text
PATCH /api/system/roles/{id}/status
```

规则：

```text
1 = 启用
0 = 禁用
```

---

### 6.9 分配角色菜单接口

接口：

```text
PUT /api/system/roles/{id}/menus
```

请求示例：

```json
{
  "menuIds": [1, 2, 3]
}
```

说明：

- 一个角色可以拥有多个菜单
- 覆盖式保存角色菜单关系

---

## 7. 菜单接口设计

### 7.1 菜单接口列表

```text
GET    /api/system/menus/tree
GET    /api/system/menus/page
GET    /api/system/menus/{id}
POST   /api/system/menus
PUT    /api/system/menus/{id}
DELETE /api/system/menus/{id}
DELETE /api/system/menus/batch
PATCH  /api/system/menus/{id}/status
```

---

### 7.2 菜单树接口

接口：

```text
GET /api/system/menus/tree
```

用途：

- 菜单管理树
- 角色分配菜单时选择菜单
- 前端构建菜单树

---

### 7.3 菜单分页接口

接口：

```text
GET /api/system/menus/page
```

用途：

- 菜单管理分页查询
- 支持条件筛选

查询条件建议：

```text
menuName
menuType
status
visible
page
pageSize
```

---

### 7.4 菜单详情接口

接口：

```text
GET /api/system/menus/{id}
```

---

### 7.5 新增菜单接口

接口：

```text
POST /api/system/menus
```

规则：

- 支持目录、菜单、外链
- 支持图标
- 支持排序
- 支持隐藏状态
- 支持启用 / 禁用
- 支持 component 字段
- 支持 path 字段
- 支持 permissionCode 字段
- permissionCode 唯一

---

### 7.6 修改菜单接口

接口：

```text
PUT /api/system/menus/{id}
```

规则：

- 内置菜单需要保护
- 内置菜单不建议修改关键标识字段

---

### 7.7 删除菜单接口

接口：

```text
DELETE /api/system/menus/{id}
```

规则：

- 删除前检查是否存在子菜单
- 删除前检查是否已被角色绑定
- 内置菜单不允许删除
- 执行逻辑删除

---

### 7.8 批量删除菜单接口

接口：

```text
DELETE /api/system/menus/batch
```

规则：

- 删除前检查是否存在子菜单
- 删除前检查是否已被角色绑定
- 内置菜单不允许删除
- 执行逻辑删除

---

### 7.9 修改菜单状态接口

接口：

```text
PATCH /api/system/menus/{id}/status
```

规则：

```text
1 = 启用
0 = 禁用
```

---

## 8. 部门接口设计

### 8.1 部门接口列表

```text
GET    /api/system/depts/tree
GET    /api/system/depts/page
GET    /api/system/depts/options
GET    /api/system/depts/{id}
POST   /api/system/depts
PUT    /api/system/depts/{id}
DELETE /api/system/depts/{id}
DELETE /api/system/depts/batch
PATCH  /api/system/depts/{id}/status
```

---

### 8.2 部门树接口

接口：

```text
GET /api/system/depts/tree
```

用途：

- 部门管理树
- 展示完整部门层级

---

### 8.3 部门分页接口

接口：

```text
GET /api/system/depts/page
```

用途：

- 部门管理分页查询
- 支持条件筛选

查询条件建议：

```text
deptName
deptCode
status
page
pageSize
```

---

### 8.4 部门选择接口

接口：

```text
GET /api/system/depts/options
```

用途：

- 用户维护时选择部门
- 返回启用部门树
- 适合前端下拉树组件

---

### 8.5 部门详情接口

接口：

```text
GET /api/system/depts/{id}
```

---

### 8.6 新增部门接口

接口：

```text
POST /api/system/depts
```

规则：

- 部门编码唯一
- 部门支持树形结构
- 部门支持启用 / 禁用
- 部门支持排序
- 部门包含负责人、联系电话、邮箱

---

### 8.7 修改部门接口

接口：

```text
PUT /api/system/depts/{id}
```

规则：

- 内置根部门需要保护
- 内置根部门不建议修改编码

---

### 8.8 删除部门接口

接口：

```text
DELETE /api/system/depts/{id}
```

规则：

- 删除前检查是否存在子部门
- 删除前检查是否已有用户归属该部门
- 内置根部门不允许删除
- 执行逻辑删除

---

### 8.9 批量删除部门接口

接口：

```text
DELETE /api/system/depts/batch
```

规则：

- 删除前检查是否存在子部门
- 删除前检查是否已有用户归属该部门
- 内置根部门不允许删除
- 执行逻辑删除

---

### 8.10 修改部门状态接口

接口：

```text
PATCH /api/system/depts/{id}/status
```

规则：

```text
1 = 启用
0 = 禁用
```

---

## 9. 字典接口设计

## 9.1 字典类型接口列表

```text
GET    /api/system/dict-types/page
GET    /api/system/dict-types/{id}
POST   /api/system/dict-types
PUT    /api/system/dict-types/{id}
DELETE /api/system/dict-types/{id}
DELETE /api/system/dict-types/batch
PATCH  /api/system/dict-types/{id}/status
```

---

### 9.2 字典数据接口列表

```text
GET    /api/system/dict-data/page
GET    /api/system/dict-data/{id}
POST   /api/system/dict-data
PUT    /api/system/dict-data/{id}
DELETE /api/system/dict-data/{id}
DELETE /api/system/dict-data/batch
GET    /api/system/dicts/{dictCode}/items
```

---

### 9.3 字典类型分页接口

接口：

```text
GET /api/system/dict-types/page
```

查询条件建议：

```text
dictName
dictCode
status
page
pageSize
```

---

### 9.4 新增字典类型接口

接口：

```text
POST /api/system/dict-types
```

规则：

- 字典编码唯一
- 字典类型支持启用 / 禁用
- 支持排序
- 支持内置字典标识

---

### 9.5 修改字典类型接口

接口：

```text
PUT /api/system/dict-types/{id}
```

规则：

- 内置字典类型禁止修改编码

---

### 9.6 删除字典类型接口

接口：

```text
DELETE /api/system/dict-types/{id}
```

规则：

- 删除字典类型前检查是否存在字典数据
- 内置字典类型禁止删除
- 执行逻辑删除

---

### 9.7 字典数据分页接口

接口：

```text
GET /api/system/dict-data/page
```

查询条件建议：

```text
dictTypeId
dictCode
dictLabel
dictValue
page
pageSize
```

---

### 9.8 新增字典数据接口

接口：

```text
POST /api/system/dict-data
```

规则：

- 同一个字典类型下，字典值唯一
- 字典项支持排序
- 字典项暂不做启用 / 禁用状态

---

### 9.9 修改字典数据接口

接口：

```text
PUT /api/system/dict-data/{id}
```

---

### 9.10 删除字典数据接口

接口：

```text
DELETE /api/system/dict-data/{id}
```

规则：

- 执行逻辑删除

---

### 9.11 通过字典编码查询字典项接口

接口：

```text
GET /api/system/dicts/{dictCode}/items
```

用途：

- 前端通过字典编码获取字典项
- 例如用户状态、性别、菜单类型、操作类型

示例：

```text
GET /api/system/dicts/user_status/items
```

返回示例：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "label": "启用",
      "value": "1",
      "sortOrder": 1
    },
    {
      "label": "禁用",
      "value": "0",
      "sortOrder": 2
    }
  ]
}
```

---

## 10. 文件接口设计

### 10.1 文件接口列表

```text
POST   /api/system/files/upload
POST   /api/system/files/upload-batch
GET    /api/system/files/page
GET    /api/system/files/{id}
PUT    /api/system/files/{id}
DELETE /api/system/files/{id}
DELETE /api/system/files/batch
PATCH  /api/system/files/{id}/status
GET    /api/system/files/{id}/download
GET    /api/system/files/{id}/view
```

---

### 10.2 单文件上传接口

接口：

```text
POST /api/system/files/upload
```

请求类型：

```text
multipart/form-data
```

参数建议：

```text
file
businessModule
remark
```

规则：

- 单文件最大 50MB
- 第一版不限制文件类型
- 本地文件存储
- 上传目录按日期分组
- 记录文件 MD5
- 记录文件元信息
- 不直接暴露服务器磁盘路径

---

### 10.3 多文件上传接口

接口：

```text
POST /api/system/files/upload-batch
```

请求类型：

```text
multipart/form-data
```

参数建议：

```text
files
businessModule
remark
```

规则：

- 支持多文件上传
- 单文件最大 50MB
- 第一版不限制文件类型
- 每个文件都记录独立文件元信息

---

### 10.4 文件分页接口

接口：

```text
GET /api/system/files/page
```

查询条件建议：

```text
originalName
businessModule
mimeType
status
page
pageSize
```

---

### 10.5 文件详情接口

接口：

```text
GET /api/system/files/{id}
```

---

### 10.6 修改文件信息接口

接口：

```text
PUT /api/system/files/{id}
```

说明：

- 修改文件备注
- 修改业务模块
- 不修改物理文件内容

---

### 10.7 删除文件接口

接口：

```text
DELETE /api/system/files/{id}
```

规则：

- 只做逻辑删除
- 不同步删除本地物理文件

---

### 10.8 批量删除文件接口

接口：

```text
DELETE /api/system/files/batch
```

规则：

- 只做逻辑删除
- 不同步删除本地物理文件

---

### 10.9 修改文件状态接口

接口：

```text
PATCH /api/system/files/{id}/status
```

规则：

```text
1 = 启用
0 = 禁用
```

---

### 10.10 文件下载接口

接口：

```text
GET /api/system/files/{id}/download
```

说明：

- 通过后端接口转发下载
- 不直接暴露磁盘路径
- 文件不存在时返回 404

---

### 10.11 文件预览接口

接口：

```text
GET /api/system/files/{id}/view
```

说明：

- 用于图片、PDF 等可浏览器预览文件
- 通过后端接口转发
- 不直接暴露磁盘路径

---

## 11. 日志接口与记录方式

### 11.1 登录日志

登录日志不走操作日志注解。

在登录逻辑中单独记录：

- 登录成功
- 登录失败
- 用户名
- 登录 IP
- 登录地点
- 浏览器
- 操作系统
- 提示信息
- 登录时间

建议查询接口：

```text
GET /api/system/login-logs/page
GET /api/system/login-logs/{id}
DELETE /api/system/login-logs/{id}
DELETE /api/system/login-logs/batch
```

---

### 11.2 操作日志

操作日志使用注解 + AOP 记录。

示例：

```java
@OperLog(title = "用户管理", type = "新增")
```

规则：

- 只在需要记录的方法上加注解
- 只记录关键写操作
- 暂不记录查询日志

记录操作类型：

```text
新增
修改
删除
导入
导出
```

建议查询接口：

```text
GET /api/system/oper-logs/page
GET /api/system/oper-logs/{id}
DELETE /api/system/oper-logs/{id}
DELETE /api/system/oper-logs/batch
```

---

### 11.3 操作日志内容

操作日志记录：

```text
模块名称
操作类型
请求方法
请求 URL
操作人
操作 IP
操作地点
请求参数
响应结果
耗时
操作状态
错误信息
操作时间
```

---

### 11.4 日志安全规则

日志记录需要注意：

```text
password、token 等敏感字段必须脱敏
响应结果不要记录完整大对象
响应结果建议记录摘要或截断内容
登录地点字段第一版可以先预留
```



---

## 12. 第一版暂不实现内容

第一版暂不实现：

```text
验证码
按钮权限
接口权限
数据权限
Excel 导出
对象存储
OAuth2
第三方登录
多租户
强制首次登录修改密码
连续密码错误锁定
完整审计追踪
```
