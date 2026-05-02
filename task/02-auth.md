# 任务 Prompt：认证模块 auth
## 任务目标
实现登录、退出、当前用户信息、当前用户可见菜单接口。本文已包含所需接口与表字段，不依赖 `tmp` 目录。
## 开始前阅读
- `doc/development-constraints.md`
- `doc/ai-project-prompt.md`
## 模块目录
- `modules/auth/controller`
- `modules/auth/service`
- `modules/auth/dto`
- `modules/auth/vo`
- 可复用或最小补齐 `system/user`、`role`、`menu`、`log` 相关 Mapper。
## 接口
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/auth/me`
- `GET /api/auth/menus`
## 请求与响应
- 登录请求：`{"username":"admin","password":"admin123"}`。
- 登录成功 data：`tokenName`、`tokenValue`、`expiresIn`。
- `tokenName` 为 `Authorization`。
- `tokenValue` 建议为 `Bearer <token>`。
- `/api/auth/me` 返回用户 ID、用户名、昵称、头像、手机号、邮箱、部门信息、角色信息、最后登录时间、最后登录 IP。
- `/api/auth/menus` 返回当前用户可见菜单树，字段包含 `id`、`parentId`、`menuName`、`menuType`、`path`、`component`、`icon`、`permissionCode`、`sortOrder`、`visible`、`children`。
## DDL 摘要
- `sys_user`：`id`、`username`、`nickname`、`password`、`phone`、`email`、`avatar`、`gender`、`dept_id`、`status`、`is_builtin`、`last_login_time`、`last_login_ip`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`；唯一索引 `uk_username(username)`。
- `sys_role`：`id`、`role_name`、`role_code`、`status`、`sort_order`、`is_builtin`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`；唯一索引 `uk_role_code(role_code)`。
- `sys_menu`：`id`、`parent_id`、`menu_name`、`menu_type`、`path`、`component`、`external_url`、`icon`、`permission_code`、`sort_order`、`visible`、`status`、`is_builtin`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`；唯一索引 `uk_permission_code(permission_code)`。
- `sys_user_role`：`id`、`user_id`、`role_id`、`create_time`、`create_by`；唯一索引 `uk_user_role(user_id, role_id)`。
- `sys_role_menu`：`id`、`role_id`、`menu_id`、`create_time`、`create_by`；唯一索引 `uk_role_menu(role_id, menu_id)`。
- `sys_login_log`：`id`、`username`、`login_status`、`login_ip`、`login_location`、`browser`、`os`、`user_agent`、`message`、`login_time`、`create_time`。
## 业务规则
- 第一版只支持用户名密码登录，不做验证码。
- 用户名唯一，`status=0` 的用户禁止登录。
- 密码使用 BCrypt 校验。
- 登录成功写入登录成功日志，更新 `last_login_time`、`last_login_ip`。
- 登录失败写入登录失败日志。
- 退出登录只退出当前 token，不影响其他设备。
- 当前用户菜单按用户 -> 角色 -> 菜单查询，只返回 `deleted=0`、`status=1`、`visible=1` 的菜单。
- 菜单按 `sort_order` 排序，根节点 `parent_id=0`，树字段为 `children`。
## 不实现
- 验证码、按钮权限、接口权限、数据权限、连续密码错误锁定、第三方登录。
## 验收标准
- 登录成功、密码错误、用户不存在、用户禁用均返回统一响应。
- 登录成功和失败均有登录日志。
- `/api/auth/me` 和 `/api/auth/menus` 必须登录后访问。
- 菜单树能正确按角色过滤并排序。
