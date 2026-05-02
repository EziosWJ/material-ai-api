# 开发约束与规范
## 接口风格
- 后台接口采用 REST 风格，资源名使用单数，不使用复数。
- 分页接口统一追加 `/page`。
- 查询使用 `GET`，新增使用 `POST`，修改使用 `PUT`，删除使用 `DELETE`，局部状态变更使用 `PATCH`。
- 批量删除统一使用 `DELETE /batch`，请求体为 JSON：`{"ids":[]}`。
- 资源关系分配类接口使用 `PUT`，例如 `PUT /api/system/user/{id}/roles`。
- 状态修改类接口使用 `PATCH`，例如 `PATCH /api/system/user/{id}/status`。
## 请求参数
- `GET` 查询参数使用 query string。
- `POST`、`PUT`、`PATCH` 使用 JSON body。
- 分页参数统一为 `page`、`pageSize`。
- 第一版不做通用排序，按后端默认排序。
## 命名规范
- 数据库字段使用 `snake_case`，例如 `user_name`、`create_time`。
- Java 字段使用 `camelCase`，例如 `userName`、`createTime`。
- JSON 字段使用 `camelCase`，例如 `userName`、`createTime`。
- 数据库表名统一使用 `sys_` 前缀。
- 数据库布尔类字段统一使用 `tinyint`。
## 树结构
- 树结构统一字段：`parent_id`、`sort_order`、`children`。
- 根节点 `parent_id = 0`。
- 排序字段统一为 `sort_order`。
- 返回给前端的子节点字段统一为 `children`。
## 内置数据保护
- 需要保护的内置数据统一使用 `is_builtin tinyint` 标识。
- `is_builtin = 1` 表示内置数据，`is_builtin = 0` 表示普通数据。
- 内置数据禁止删除。
- 内置数据允许修改名称等展示信息。
- 内置数据禁止修改编码、权限码、用户名等关键标识字段。
## 布尔与删除字段
- `status`：`1` 表示启用，`0` 表示禁用。
- `visible`：`1` 表示显示，`0` 表示隐藏。
- `is_builtin`：`1` 表示内置，`0` 表示普通。
- `deleted`：`0` 表示正常，`1` 表示已删除。
- 注意 `deleted` 的语义与普通布尔字段相反，代码中必须单独明确。
## 审计与时间字段
- 核心表统一包含 `id`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`。
- 需要启用 / 禁用的表额外包含 `status`。
- 时间字段统一使用 `datetime`。
- `create_time`、`update_time` 由 MyBatis-Plus 自动填充。
- 第一版统一使用服务器本地时间 / 北京时间。
## 枚举字段
- 枚举类字段统一使用字符串编码存储，便于前端和字典匹配。
- 枚举编码使用稳定的大写英文标识。
- 不在数据库中存储枚举中文展示名。
## 密码安全
- 密码必须加密存储，推荐使用 BCrypt。
- 新增用户、修改密码、重置密码时必须重新加密。
- 登录时使用 BCrypt 校验明文密码与数据库密文。
- 禁止明文保存密码。
- 禁止使用 MD5、SHA1、SHA256(password)、MD5(password + salt) 或可逆加密保存密码。
## 文件访问
- 本地文件按日期分组保存，例如 `uploads/yyyy/MM/dd/xxx.ext`。
- 文件表中 `storage_path` 存物理相对路径，不存绝对磁盘路径。
- 文件表中 `access_url` 存后端访问接口，不存磁盘路径。
- 文件预览使用 `GET /api/system/file/{id}/view`。
- 文件下载使用 `GET /api/system/file/{id}/download`。
- 文件访问必须通过后端接口转发，不直接暴露服务器磁盘路径。
- 删除文件记录时只做逻辑删除。
- 删除文件记录时不同步删除本地物理文件。
## 鉴权
- 除登录接口外，后台接口默认都需要登录。
- 文件预览和下载接口也需要登录。
- 第一版只做登录态校验和菜单可见控制。
- `permission_code` 字段先预留，第一版不校验按钮权限或接口权限。
## 当前用户接口归属
- 当前登录用户信息归属 `auth` 模块：`GET /api/auth/me`。
- 当前登录用户菜单归属 `auth` 模块：`GET /api/auth/menus`。
- 当前用户修改密码归属 `system/user` 模块：`PUT /api/system/user/me/password`。
- 当前用户修改头像归属 `system/user` 模块：`PUT /api/system/user/me/avatar`。
## 日志清理
- 日志接口不提供单条删除和批量删除。
- 日志清空接口仅开发阶段使用。
- 日志清空使用物理删除。
- 日志清空接口不做额外角色限制。
- 日志清空必须通过配置开关控制。
- 配置项为 `system.log-clear-enabled`。
- `dev` 默认开启，`prod` 默认关闭。
- 配置关闭时，调用清空接口返回 `403`。
## 日志安全
- 操作日志需要记录关键写操作，不记录普通查询日志。
- 请求参数中的敏感字段必须脱敏，例如 `password`、`token`。
- 响应结果不要记录完整大对象。
- 响应结果可以记录摘要或截断内容。
## 异常与响应码
- 使用全局异常处理。
- 业务响应码统一为：`200` 成功，`400` 参数错误，`401` 未登录或 token 失效，`403` 无权限，`404` 数据不存在，`500` 系统错误。
- 参数校验失败返回字段级错误信息，例如 `username 不能为空`。
- 业务错误必须使用统一响应结构返回，不直接暴露异常堆栈。
## 缓存
- 第一版不引入 Redis。
- 字典、菜单等基础查询直接查数据库。
- 后续出现明确性能需求后，再考虑本地缓存或 Redis。
## CORS
- 前后端分离开发由后端统一配置 CORS。
- `dev` 允许来源：`http://localhost:5173`。
- 允许请求头：`Authorization`、`Content-Type`。
- 允许方法：`GET`、`POST`、`PUT`、`DELETE`、`PATCH`、`OPTIONS`。
- `prod` 必须按实际域名配置，不使用宽泛来源。
## Knife4j
- Knife4j 按模块分组展示接口文档。
- Knife4j 仅在 `dev` 环境开启。
- `prod` 环境关闭文档入口。
