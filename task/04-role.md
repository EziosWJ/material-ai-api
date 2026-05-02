# 任务 Prompt：角色模块 system/role
## 任务目标
实现角色管理模块：分页、详情、新增、修改、逻辑删除、批量删除、启用禁用、排序、分配菜单。本文已包含所需接口与表字段，不依赖 `tmp` 目录。
## 开始前阅读
- `doc/development-constraints.md`
- `doc/ai-project-prompt.md`
## 模块目录
- `modules/system/role/controller`
- `modules/system/role/service`
- `modules/system/role/service/impl`
- `modules/system/role/mapper`
- `modules/system/role/entity`
- `modules/system/role/dto`
- `modules/system/role/vo`
## 接口
- `GET /api/system/role/page`
- `GET /api/system/role/{id}`
- `POST /api/system/role`
- `PUT /api/system/role/{id}`
- `DELETE /api/system/role/{id}`
- `DELETE /api/system/role/batch`
- `PATCH /api/system/role/{id}/status`
- `PUT /api/system/role/{id}/menus`
## 请求参数
- 分页查询：`roleName`、`roleCode`、`status`、`page`、`pageSize`。
- 批量删除：`{"ids":[1,2,3]}`。
- 修改状态：`{"status":1}`。
- 分配菜单：`{"menuIds":[1,2,3]}`。
## DDL 摘要
- `sys_role.id BIGINT AUTO_INCREMENT PRIMARY KEY`。
- `role_name VARCHAR(50) NOT NULL`。
- `role_code VARCHAR(50) NOT NULL`，唯一索引 `uk_role_code(role_code)`。
- `status TINYINT NOT NULL DEFAULT 1`。
- `sort_order INT NOT NULL DEFAULT 0`。
- `is_builtin TINYINT NOT NULL DEFAULT 0`。
- `remark VARCHAR(500) NULL`。
- `create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`。
- `update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`。
- `create_by BIGINT NULL`，`update_by BIGINT NULL`。
- `deleted TINYINT NOT NULL DEFAULT 0`，索引 `idx_deleted_status_sort(deleted,status,sort_order)`。
- `sys_user_role`：`id`、`user_id`、`role_id`、`create_time`、`create_by`；用于删除前检查用户绑定。
- `sys_role_menu`：`id`、`role_id`、`menu_id`、`create_time`、`create_by`；唯一索引 `uk_role_menu(role_id, menu_id)`。
- 关联读取：`sys_menu.id`、`status`、`deleted`。
## 业务规则
- `role_code` 必填且唯一。
- 角色支持启用/禁用和排序。
- `is_builtin=1` 的内置角色禁止删除。
- 内置角色禁止修改 `role_code`。
- 删除和批量删除前检查是否已有用户绑定。
- 删除执行逻辑删除。
- 分配菜单为覆盖式保存：先清理旧关系，再写入新 `menuIds`。
- 用户分配角色时可复用角色分页查询，不需要额外“全部启用角色”接口。
## 返回要求
- 详情返回角色基础信息和已分配菜单 ID 列表，便于编辑回显。
- 分页按 `sort_order`、`id` 稳定排序。
## 验收标准
- 角色 CRUD、状态修改、批量删除、菜单分配接口可用。
- 角色编码重复、内置角色删除、被用户绑定角色删除返回统一业务错误。
- 角色菜单关系保存后可被认证菜单查询使用。
