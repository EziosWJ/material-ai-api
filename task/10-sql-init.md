# 任务 Prompt：SQL 脚本与初始化数据
## 任务目标
维护第一版数据库脚本和初始化数据，保证系统具备最小可登录、可显示菜单、可维护基础模块的数据库基础。本文已包含 DDL 摘要，不依赖 `tmp` 目录。
## 开始前阅读
- `doc/development-constraints.md`
- `doc/ai-project-prompt.md`
## 文件范围
- `src/main/resources/sql/schema.sql`
- `src/main/resources/sql/data.sql`
- `src/main/resources/sql/init.sql` 可选
## 全局 DDL 规则
- 不使用数据库外键。
- 使用 `BIGINT` 自增主键。
- 表名使用 `sys_` 前缀。
- 数据库字段使用 `snake_case`。
- 逻辑删除字段统一为 `deleted`，`0=正常`，`1=删除`。
- 启用禁用字段统一为 `status`，`1=启用`，`0=禁用`。
- 内置数据字段统一为 `is_builtin`，`1=内置`，`0=普通`。
- 关联字段保留普通索引，删除约束由 Service 层控制。
- 日志表不设计 `deleted` 字段。
## 表结构摘要
- `sys_user`：`id`、`username`、`nickname`、`password`、`phone`、`email`、`avatar`、`gender`、`dept_id`、`status`、`is_builtin`、`last_login_time`、`last_login_ip`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`；唯一索引 `uk_username(username)`；索引 `idx_dept_id(dept_id)`、`idx_deleted_status(deleted,status)`。
- `sys_role`：`id`、`role_name`、`role_code`、`status`、`sort_order`、`is_builtin`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`；唯一索引 `uk_role_code(role_code)`；索引 `idx_deleted_status_sort(deleted,status,sort_order)`。
- `sys_menu`：`id`、`parent_id`、`menu_name`、`menu_type`、`path`、`component`、`external_url`、`icon`、`permission_code`、`sort_order`、`visible`、`status`、`is_builtin`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`；唯一索引 `uk_permission_code(permission_code)`；索引 `idx_parent_id(parent_id)`、`idx_deleted_status_sort(deleted,status,sort_order)`、`idx_menu_type(menu_type)`。
- `sys_dept`：`id`、`parent_id`、`dept_name`、`dept_code`、`leader`、`phone`、`email`、`sort_order`、`status`、`is_builtin`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`；唯一索引 `uk_dept_code(dept_code)`；索引 `idx_parent_id(parent_id)`、`idx_deleted_status_sort(deleted,status,sort_order)`。
- `sys_user_role`：`id`、`user_id`、`role_id`、`create_time`、`create_by`；唯一索引 `uk_user_role(user_id,role_id)`；索引 `idx_user_id(user_id)`、`idx_role_id(role_id)`。
- `sys_role_menu`：`id`、`role_id`、`menu_id`、`create_time`、`create_by`；唯一索引 `uk_role_menu(role_id,menu_id)`；索引 `idx_role_id(role_id)`、`idx_menu_id(menu_id)`。
- `sys_dict_type`：`id`、`dict_name`、`dict_code`、`status`、`sort_order`、`is_builtin`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`；唯一索引 `uk_dict_code(dict_code)`；索引 `idx_deleted_status_sort(deleted,status,sort_order)`。
- `sys_dict_data`：`id`、`dict_type_id`、`dict_label`、`dict_value`、`sort_order`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`；唯一索引 `uk_type_value(dict_type_id,dict_value)`；索引 `idx_dict_type_id(dict_type_id)`、`idx_deleted_sort(deleted,sort_order)`。
- `sys_login_log`：`id`、`username`、`login_status`、`login_ip`、`login_location`、`browser`、`os`、`user_agent`、`message`、`login_time`、`create_time`；索引 `idx_username(username)`、`idx_login_status(login_status)`、`idx_login_time(login_time)`。
- `sys_oper_log`：`id`、`module_name`、`operation_type`、`request_method`、`request_url`、`operator_id`、`operator_name`、`operator_ip`、`operator_location`、`request_params`、`response_result`、`cost_time`、`operation_status`、`error_message`、`operation_time`、`create_time`；索引 `idx_module_name(module_name)`、`idx_operation_type(operation_type)`、`idx_operator_id(operator_id)`、`idx_operation_status(operation_status)`、`idx_operation_time(operation_time)`。
- `sys_file`：`id`、`original_name`、`storage_name`、`extension`、`mime_type`、`file_size`、`file_md5`、`storage_path`、`access_url`、`business_module`、`status`、`remark`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`；索引 `idx_file_md5(file_md5)`、`idx_business_module(business_module)`、`idx_deleted_status(deleted,status)`、`idx_create_time(create_time)`。
## 初始化数据
- 初始化 admin 用户，`username=admin`，默认明文密码为配置项默认值 `admin123`，SQL 中必须写 BCrypt 密文。
- 初始化 admin 超级管理员角色，设置 `is_builtin=1`。
- 初始化根部门，设置 `is_builtin=1`。
- 初始化系统管理、日志管理、文件管理等基础菜单，设置 `is_builtin=1`。
- 建立 admin 用户与 admin 角色关系。
- 建立 admin 角色与所有初始化菜单关系。
- 初始化基础字典类型和字典项：用户状态、性别、菜单类型、通用状态、操作类型、登录状态、文件业务模块。
## 字典编码建议
- 性别：`UNSPECIFIED`、`MALE`、`FEMALE`。
- 菜单类型：`DIR`、`MENU`、`LINK`。
- 登录状态：`SUCCESS`、`FAIL`。
- 操作类型：`CREATE`、`UPDATE`、`DELETE`、`IMPORT`、`EXPORT`。
## 验收标准
- `schema.sql` 能创建全部第一版表。
- `data.sql` 能插入最小可用初始化数据。
- 初始化后可使用 admin 账号登录，并能返回可见菜单。
- 初始化数据中的内置记录与业务保护规则一致。
