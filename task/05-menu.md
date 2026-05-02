# 任务 Prompt：菜单模块 system/menu
## 任务目标
实现菜单管理模块：菜单树、分页、详情、新增、修改、逻辑删除、批量删除、启用禁用、隐藏显示、排序、内置菜单保护。本文已包含所需接口与表字段，不依赖 `tmp` 目录。
## 开始前阅读
- `doc/development-constraints.md`
- `doc/ai-project-prompt.md`
## 模块目录
- `modules/system/menu/controller`
- `modules/system/menu/service`
- `modules/system/menu/service/impl`
- `modules/system/menu/mapper`
- `modules/system/menu/entity`
- `modules/system/menu/dto`
- `modules/system/menu/vo`
## 接口
- `GET /api/system/menu/tree`
- `GET /api/system/menu/page`
- `GET /api/system/menu/{id}`
- `POST /api/system/menu`
- `PUT /api/system/menu/{id}`
- `DELETE /api/system/menu/{id}`
- `DELETE /api/system/menu/batch`
- `PATCH /api/system/menu/{id}/status`
## 请求参数
- 分页查询：`menuName`、`menuType`、`status`、`visible`、`page`、`pageSize`。
- 批量删除：`{"ids":[1,2,3]}`。
- 修改状态：`{"status":1}`。
## DDL 摘要
- `sys_menu.id BIGINT AUTO_INCREMENT PRIMARY KEY`。
- `parent_id BIGINT NOT NULL DEFAULT 0`。
- `menu_name VARCHAR(50) NOT NULL`。
- `menu_type VARCHAR(20) NOT NULL`，取值 `DIR/MENU/LINK`。
- `path VARCHAR(255) NULL`。
- `component VARCHAR(255) NULL`。
- `external_url VARCHAR(500) NULL`。
- `icon VARCHAR(100) NULL`。
- `permission_code VARCHAR(100) NULL`，唯一索引 `uk_permission_code(permission_code)`，允许多个 NULL。
- `sort_order INT NOT NULL DEFAULT 0`。
- `visible TINYINT NOT NULL DEFAULT 1`。
- `status TINYINT NOT NULL DEFAULT 1`。
- `is_builtin TINYINT NOT NULL DEFAULT 0`。
- `remark VARCHAR(500) NULL`。
- `create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`。
- `update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`。
- `create_by BIGINT NULL`，`update_by BIGINT NULL`。
- `deleted TINYINT NOT NULL DEFAULT 0`。
- 索引：`idx_parent_id(parent_id)`、`idx_deleted_status_sort(deleted,status,sort_order)`、`idx_menu_type(menu_type)`。
- `sys_role_menu`：`id`、`role_id`、`menu_id`、`create_time`、`create_by`；用于删除前检查角色绑定。
## 业务规则
- 根节点 `parent_id=0`。
- 树形返回字段统一为 `children`。
- 菜单类型支持目录、菜单、外链。
- 菜单支持图标、排序、隐藏状态、启用/禁用、`component`、`path`、`permissionCode`。
- 路由 `path` 由后端维护并返回前端。
- React 前端通过 `component` 字符串映射本地组件，例如 `system/user/index`；后端不要假设前端可任意动态加载本地文件。
- `permission_code` 非空时必须唯一。
- `is_builtin=1` 的内置菜单不允许删除。
- 内置菜单不建议修改关键标识字段。
- 删除和批量删除前检查是否存在子菜单。
- 删除和批量删除前检查是否已被角色绑定。
- 删除执行逻辑删除。
## 返回要求
- 树接口用于菜单管理、角色分配菜单、前端构建菜单树。
- 树和当前用户菜单均按 `sort_order` 排序。
## 验收标准
- 菜单树、分页、详情、CRUD、状态修改可用。
- 删除有子菜单、被角色绑定、内置菜单时返回统一业务错误。
