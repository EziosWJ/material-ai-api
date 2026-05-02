# AI 项目开发 Prompt
你是资深 Java 后端工程师，请基于以下上下文开发一个前后端分离的通用后台管理系统后端。必须优先遵守本文约束，保持实现简单、清晰、可扩展，但不要过度设计。
## 项目定位
- 项目是通用后台管理系统模板，服务 React 管理后台。
- 架构为单 Spring Boot 应用、单 Maven module、模块化单体。
- 不使用微服务架构，不引入未要求的中间件。
- 第一版目标是完成核心后台基础能力，结构规范，接近真实企业项目。
## 技术栈
- Java 21。
- Spring Boot 3.x。
- MySQL 8.0。
- MyBatis-Plus。
- Sa-Token。
- Lombok。
- Knife4j。
- Spring Validation。
- Hutool。
- 构建工具使用 Maven。
## 包结构
- 采用单项目 + 包级模块化。
- 推荐根包按当前项目实际包名确定，不要自行切换根包。
- 推荐结构：`common`、`framework`、`modules`。
- 业务模块放在 `modules` 下。
- 认证模块：`modules/auth`。
- 系统模块：`modules/system`。
- 系统子模块：`user`、`role`、`menu`、`dept`、`dict`、`log`、`file`。
## 模块分层
- 每个业务模块内部统一分层：`controller`、`service`、`service.impl`、`mapper`、`entity`、`dto`、`vo`。
- `Controller` 只处理接口入口、参数校验、响应封装。
- `Service` 处理业务逻辑。
- `Mapper` 处理数据库访问。
- `Entity` 对应数据库表结构。
- `DTO` 接收前端请求参数。
- `VO` 返回前端展示数据。
- 简单 CRUD 使用 MyBatis-Plus。
- 复杂查询使用 XML SQL。
## 数据库设计
- 数据库使用 MySQL 8.0。
- 表名统一使用 `sys_` 前缀。
- 主键使用 `BIGINT` 自增。
- 删除策略使用逻辑删除。
- 基础时间字段统一包含 `create_time`、`update_time`。
- 数据库字段使用 `snake_case`。
- Java 字段和 JSON 字段使用 `camelCase`。
- 数据库布尔类字段使用 `tinyint`。
- 枚举类字段使用稳定的大写字符串编码，不存中文展示名。
## 第一版表范围
- 核心权限表：`sys_user`、`sys_role`、`sys_menu`、`sys_user_role`、`sys_role_menu`。
- 基础能力表：`sys_dept`、`sys_dict_type`、`sys_dict_data`、`sys_login_log`、`sys_oper_log`、`sys_file`。
- 第一版暂不使用 Flyway 或 Liquibase。
- SQL 脚本手动维护，建议目录：`src/main/resources/sql/schema.sql`、`src/main/resources/sql/data.sql`、`src/main/resources/sql/init.sql`。
## 关联表
- `sys_user_role` 字段建议：`id`、`user_id`、`role_id`、`create_time`、`create_by`。
- `sys_role_menu` 字段建议：`id`、`role_id`、`menu_id`、`create_time`、`create_by`。
- 一个用户可以拥有多个角色。
- 一个角色可以分配给多个用户。
- 一个角色可以拥有多个菜单。
- 一个菜单可以分配给多个角色。
## 权限模型
- 认证鉴权框架使用 Sa-Token。
- 第一版权限模型为：用户 -> 角色 -> 菜单。
- 后端根据当前登录用户角色返回可见菜单，前端不从全部菜单中过滤。
- 除登录接口外，后台接口默认都需要登录。
- 文件预览和下载接口也需要登录。
- 第一版只做登录态校验和菜单可见控制。
- `permission_code` 字段只预留，第一版不实现按钮权限、接口权限、数据权限。
## 核心业务边界
- 用户只属于一个部门。
- 部门使用树形结构。
- 角色暂不绑定部门。
- 菜单支持目录、菜单、外链、图标、排序、隐藏状态。
- 字典使用 `sys_dict_type` 和 `sys_dict_data` 两张表。
- 字典项支持排序。
- 第一版字典项暂不做启用/禁用状态。
- 登录日志记录登录成功和登录失败。
- 操作日志只记录关键写操作：新增、修改、删除、导入、导出。
- 第一版不记录查询日志。
- 第一版文件存储使用本地文件，不接入 MinIO、OSS、COS 或其他对象存储。
## 用户模块
- 用户能力：分页列表、详情、新增、修改、逻辑删除、批量删除、启用/禁用、分配角色、所属部门、头像。
- 用户字段建议：`id`、`username`、`nickname`、`password`、`phone`、`email`、`avatar`、`gender`、`status`、`dept_id`、`last_login_time`、`last_login_ip`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`。
- 用户名作为登录账号且唯一。
- 手机号和邮箱不要求唯一。
- 使用昵称字段，不单独设计真实姓名。
- 密码必须使用 BCrypt 加密存储，禁止明文、MD5、SHA1、SHA256(password)、MD5(password + salt) 和可逆加密。
- 新增用户、修改密码、管理员重置密码时必须重新加密。
- 登录时使用 BCrypt 校验明文密码与数据库密文。
## 角色模块
- 角色能力：分页列表、详情、新增、修改、逻辑删除、批量删除、启用/禁用、分配菜单、排序。
- 角色字段建议：`id`、`role_name`、`role_code`、`status`、`sort_order`、`is_builtin`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`。
- `role_code` 唯一。
- 内置超级管理员角色不允许删除。
## 菜单模块
- 菜单能力：菜单树、新增、修改、逻辑删除、批量删除、启用/禁用、隐藏/显示、排序、内置菜单保护。
- 菜单字段建议：`id`、`parent_id`、`menu_name`、`menu_type`、`path`、`component`、`external_url`、`icon`、`permission_code`、`sort_order`、`visible`、`status`、`is_builtin`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`。
- 菜单类型支持目录、菜单、外链。
- `permission_code` 唯一。
- 路由 `path` 由后端维护并返回给前端。
- React 不能直接根据后端字符串任意动态加载本地文件；后端返回 `component` 字符串，前端通过组件映射表匹配本地组件，例如 `system/user/index`。
- 内置菜单不允许删除。
## 部门模块
- 部门能力：部门树、新增、修改、逻辑删除、批量删除、启用/禁用、排序。
- 部门字段建议：`id`、`parent_id`、`dept_name`、`dept_code`、`leader`、`phone`、`email`、`sort_order`、`status`、`is_builtin`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`。
- `dept_code` 唯一。
- 内置根部门不允许删除。
## 字典模块
- 字典能力：字典类型分页列表、新增、修改、逻辑删除、启用/禁用；字典数据列表、新增、修改、逻辑删除、排序。
- 字典类型字段建议：`id`、`dict_name`、`dict_code`、`status`、`sort_order`、`is_builtin`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`。
- 字典数据字段建议：`id`、`dict_type_id`、`dict_label`、`dict_value`、`sort_order`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`。
- `dict_code` 唯一。
- 同一个字典类型下 `dict_value` 唯一。
- 字典项暂不做启用/禁用状态。
- 内置字典不允许删除。
## 日志模块
- 登录日志表字段建议：`id`、`username`、`login_status`、`login_ip`、`login_location`、`browser`、`os`、`message`、`login_time`、`create_time`。
- 登录地点字段第一版可以先预留，后续通过 IP 解析补充。
- 操作日志表字段建议：`id`、`module_name`、`operation_type`、`request_method`、`request_url`、`operator_id`、`operator_name`、`operator_ip`、`operator_location`、`request_params`、`response_result`、`cost_time`、`operation_status`、`error_message`、`operation_time`、`create_time`。
- 操作日志记录请求参数、响应结果、操作耗时、操作状态、错误信息、操作地点。
- 请求参数中的敏感字段必须脱敏，例如 `password`、`token`。
- 响应结果不要记录完整大对象，可以记录摘要或截断内容。
## 文件模块
- 文件能力：文件上传、文件列表、文件详情、文件修改、文件逻辑删除、批量删除、启用/禁用、记录业务归属。
- 文件字段建议：`id`、`original_name`、`storage_name`、`extension`、`mime_type`、`file_size`、`file_md5`、`storage_path`、`access_url`、`business_module`、`status`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`。
- 文件记录需要保存原始文件名、存储文件名、扩展名、MIME 类型、文件大小、MD5、存储路径、访问 URL、所属业务模块。
- 删除文件记录时只做逻辑删除，不同步删除本地物理文件。
## 接口通用规范
- 接口统一前缀为 `/api`。
- 接口采用 REST 风格，资源名使用单数，不使用复数。
- 分页接口统一追加 `/page`。
- 查询使用 `GET`，新增使用 `POST`，修改使用 `PUT`，删除使用 `DELETE`，局部状态变更使用 `PATCH`。
- `GET` 查询参数使用 query string。
- `POST`、`PUT`、`PATCH` 使用 JSON body。
- 分页参数统一为 `page`、`pageSize`。
- 第一版不做通用排序，按后端默认排序。
- 批量删除统一使用 `DELETE /batch`，请求体为 JSON：`{"ids":[]}`。
- 资源关系分配类接口使用 `PUT`，例如 `PUT /api/system/user/{id}/roles`。
- 状态修改类接口使用 `PATCH`，例如 `PATCH /api/system/user/{id}/status`。
## 响应结构
- 所有接口返回统一响应结构：`{"code":200,"message":"success","data":{}}`。
- 分页数据结构：`{"records":[],"total":100,"page":1,"pageSize":10}`。
- 业务错误必须使用统一响应结构返回，不直接暴露异常堆栈。
## 异常与响应码
- 使用全局异常处理。
- 业务响应码统一为：`200` 成功，`400` 参数错误，`401` 未登录或 token 失效，`403` 无权限，`404` 数据不存在，`500` 系统错误。
- 参数校验失败返回字段级错误信息，例如 `username 不能为空`。
## 基础接口范围
- 用户、角色、菜单、部门、字典、文件模块提供标准 CRUD。
- 标准能力包括分页列表、详情、新增、修改、删除或逻辑删除、启用/禁用、批量删除。
- 第一版暂不做 Excel 导出。
- 日志接口不提供单条删除和批量删除，只提供分页、详情、开发阶段清空。
## 树结构规范
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
## 布尔字段语义
- `status`：`1` 表示启用，`0` 表示禁用。
- `visible`：`1` 表示显示，`0` 表示隐藏。
- `is_builtin`：`1` 表示内置，`0` 表示普通。
- `deleted`：`0` 表示正常，`1` 表示已删除。
- 注意 `deleted` 的语义与普通布尔字段相反，代码中必须单独明确。
## 审计与时间字段
- 核心表统一包含 `id`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`。
- 需要启用/禁用的表额外包含 `status`。
- 时间字段统一使用 `datetime`。
- 时间字段包括 `create_time`、`update_time`、`login_time`、`operation_time`、`last_login_time`。
- `create_time`、`update_time` 由 MyBatis-Plus 自动填充。
- 第一版统一使用服务器本地时间 / 北京时间。
## 文件规范
- 本地文件按日期分组保存，例如 `uploads/yyyy/MM/dd/xxx.ext`。
- 文件表记录原始文件名、存储文件名、文件路径、文件大小、文件类型、上传人、上传时间、所属业务模块。
- 文件需要记录业务归属，例如 `avatar`、`import`、`attachment`。
- 文件表中 `storage_path` 存物理相对路径，不存绝对磁盘路径。
- 文件表中 `access_url` 存后端访问接口，不存磁盘路径。
- 文件预览使用 `GET /api/system/file/{id}/view`。
- 文件下载使用 `GET /api/system/file/{id}/download`。
- 文件访问必须通过后端接口转发，不直接暴露服务器磁盘路径。
## 当前用户接口
- 当前登录用户信息归属 `auth` 模块：`GET /api/auth/me`。
- 当前登录用户菜单归属 `auth` 模块：`GET /api/auth/menus`。
- 当前用户修改密码归属 `system/user` 模块：`PUT /api/system/user/me/password`。
- 当前用户修改头像归属 `system/user` 模块：`PUT /api/system/user/me/avatar`。
## 日志清理
- 登录日志接口：`GET /api/system/login-log/page`、`GET /api/system/login-log/{id}`、`DELETE /api/system/login-log/clear`。
- 操作日志接口：`GET /api/system/oper-log/page`、`GET /api/system/oper-log/{id}`、`DELETE /api/system/oper-log/clear`。
- 日志清空接口仅开发阶段使用。
- 日志清空使用物理删除。
- 日志清空接口不做额外角色限制。
- 日志清空必须通过配置开关控制。
- 配置项为 `system.log-clear-enabled`。
- `dev` 默认开启，`prod` 默认关闭。
- 配置关闭时，调用清空接口返回 `403`。
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
- Knife4j 用于生成接口文档。
- Knife4j 按模块分组展示接口文档。
- Knife4j 仅在 `dev` 环境开启。
- `prod` 环境关闭文档入口。
## 环境配置
- 环境区分为 `dev` 和 `prod`。
- 第一版开发环境连接本机 MySQL。
- 暂不提供 `application-example.yml`。
## 代码生成要求
- 先遵守现有项目结构和已有代码风格。
- 每次改动必须尽量小，避免无关重构。
- 不新增未要求的框架、组件、缓存、队列、对象存储或微服务能力。
- 请求参数必须使用 Spring Validation 做必要校验。
- 使用 Lombok 减少样板代码。
- 使用 Hutool 处理通用工具场景，但不要滥用。
- Controller、DTO、VO、Entity 命名必须清晰表达用途。
- Mapper XML 只用于复杂 SQL，简单 CRUD 优先使用 MyBatis-Plus。
- 涉及删除时必须区分逻辑删除和物理删除；除日志清空外，默认使用逻辑删除。
- 生成接口时同步考虑 Knife4j 注解和分组。
- 生成数据库字段、Java 字段、JSON 字段时必须遵守命名规范。
- 遇到约束冲突时，优先遵守本文档；无法判断时先提出问题，不要自行扩展。
