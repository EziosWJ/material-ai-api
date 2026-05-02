# 任务 Prompt：部门模块 system/dept
## 任务目标
实现部门管理模块：部门树、分页、选择树、详情、新增、修改、逻辑删除、批量删除、启用禁用、排序、内置根部门保护。本文已包含所需接口与表字段，不依赖 `tmp` 目录。
## 开始前阅读
- `doc/development-constraints.md`
- `doc/ai-project-prompt.md`
## 模块目录
- `modules/system/dept/controller`
- `modules/system/dept/service`
- `modules/system/dept/service/impl`
- `modules/system/dept/mapper`
- `modules/system/dept/entity`
- `modules/system/dept/dto`
- `modules/system/dept/vo`
## 接口
- `GET /api/system/dept/tree`
- `GET /api/system/dept/page`
- `GET /api/system/dept/options`
- `GET /api/system/dept/{id}`
- `POST /api/system/dept`
- `PUT /api/system/dept/{id}`
- `DELETE /api/system/dept/{id}`
- `DELETE /api/system/dept/batch`
- `PATCH /api/system/dept/{id}/status`
## 请求参数
- 分页查询：`deptName`、`deptCode`、`status`、`page`、`pageSize`。
- 批量删除：`{"ids":[1,2,3]}`。
- 修改状态：`{"status":1}`。
## DDL 摘要
- `sys_dept.id BIGINT AUTO_INCREMENT PRIMARY KEY`。
- `parent_id BIGINT NOT NULL DEFAULT 0`。
- `dept_name VARCHAR(100) NOT NULL`。
- `dept_code VARCHAR(50) NOT NULL`，唯一索引 `uk_dept_code(dept_code)`。
- `leader VARCHAR(50) NULL`。
- `phone VARCHAR(20) NULL`。
- `email VARCHAR(100) NULL`。
- `sort_order INT NOT NULL DEFAULT 0`。
- `status TINYINT NOT NULL DEFAULT 1`。
- `is_builtin TINYINT NOT NULL DEFAULT 0`。
- `remark VARCHAR(500) NULL`。
- `create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`。
- `update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`。
- `create_by BIGINT NULL`，`update_by BIGINT NULL`。
- `deleted TINYINT NOT NULL DEFAULT 0`。
- 索引：`idx_parent_id(parent_id)`、`idx_deleted_status_sort(deleted,status,sort_order)`。
- 关联检查：`sys_user.dept_id`，删除部门前检查是否已有用户归属。
## 业务规则
- 部门使用树形结构，根节点 `parent_id=0`。
- 用户只属于一个部门。
- 角色暂不绑定部门。
- `dept_code` 必填且唯一。
- 部门包含负责人、联系电话、邮箱。
- 部门支持启用/禁用和排序。
- 部门选择接口返回启用部门树，供用户维护下拉树使用。
- `is_builtin=1` 的内置根部门不允许删除。
- 内置根部门不建议修改编码。
- 删除和批量删除前检查是否存在子部门。
- 删除和批量删除前检查是否已有用户归属该部门。
- 删除执行逻辑删除。
## 返回要求
- `tree` 和 `options` 返回 `children`。
- `options` 只返回 `deleted=0`、`status=1` 的部门。
## 验收标准
- 部门树、分页、选择树、详情、CRUD、状态修改可用。
- 删除有子部门、有关联用户、内置根部门时返回统一业务错误。
