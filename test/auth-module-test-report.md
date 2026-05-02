# Auth 模块接口测试记录

- 测试日期：2026-05-02
- 测试范围：`auth` 模块接口
- 测试环境：本地 Spring Boot 服务 + 已配置 MySQL
- 启动方式：`./mvnw spring-boot:run`

## 测试目标

验证认证模块的核心接口是否满足基础契约：

- 未登录访问是否返回统一 `401`
- 登录是否可用
- 当前用户信息是否可用
- 当前用户菜单树是否可用
- 退出登录是否仅影响当前 token
- 参数校验和登录失败是否返回统一错误结构
- 登录日志是否正常写入

## 测试用例与结果

### 1. 未登录访问当前用户接口

- 请求：`GET /api/auth/me`
- 结果：通过
- 返回：`401`
- 响应示例：`{"code":401,"message":"未登录或 token 已失效","data":null}`

### 2. 用户名密码登录

- 请求：`POST /api/auth/login`
- 请求体：`{"username":"admin","password":"admin123"}`
- 结果：通过
- 返回：`200`
- 响应包含：
  - `tokenName=Authorization`
  - `tokenValue=Bearer <token>`
  - `expiresIn`

### 3. 获取当前用户信息

- 请求：`GET /api/auth/me`
- 认证：使用登录返回的 token
- 结果：通过
- 返回内容包含：
  - 用户 ID、用户名、昵称、头像、手机号、邮箱
  - 部门信息
  - 角色信息
  - 最后登录时间、最后登录 IP

### 4. 获取当前用户可见菜单

- 请求：`GET /api/auth/menus`
- 认证：使用登录返回的 token
- 结果：通过
- 返回内容：
  - 菜单树结构正常
  - `children` 字段正常
  - 菜单排序正常

### 5. 密码错误登录

- 请求：`POST /api/auth/login`
- 请求体：`{"username":"admin","password":"wrong-password"}`
- 结果：通过
- 返回：统一业务错误
- 响应示例：`{"code":400,"message":"用户名或密码错误","data":null}`

### 6. 用户不存在登录

- 请求：`POST /api/auth/login`
- 请求体：`{"username":"not-exists","password":"admin123"}`
- 结果：通过
- 返回：统一业务错误
- 响应示例：`{"code":400,"message":"用户名或密码错误","data":null}`

### 7. 登录参数校验

- 请求：`POST /api/auth/login`
- 请求体：`{}`
- 结果：通过
- 返回：`400`
- 响应包含字段级错误：
  - `username: 用户名不能为空`
  - `password: 密码不能为空`

### 8. 退出登录

- 请求：`POST /api/auth/logout`
- 认证：使用登录返回的 token
- 结果：通过
- 退出后再次访问 `GET /api/auth/me` 返回 `401`

### 9. 登录日志检查

- 请求：`GET /api/system/login-log/page?page=1&pageSize=5`
- 认证：使用登录返回的 token
- 结果：通过
- 观察到成功和失败日志均已写入，包括：
  - `SUCCESS`
  - `FAIL`
  - `密码错误`
  - `用户不存在`

## 结论

`auth` 模块基础接口可用，当前实现满足登录、退出、当前用户信息、当前用户菜单和统一异常响应的基础契约。

