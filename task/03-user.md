# 任务 Prompt：用户模块 system/user
## 任务目标
实现用户管理模块：分页、详情、新增、修改、逻辑删除、批量删除、启用禁用、分配角色、重置密码、当前用户改密码、当前用户改头像。本文已包含所需接口与表字段，不依赖 `tmp` 目录。
## 开始前阅读
- `doc/development-constraints.md`
- `doc/ai-project-prompt.md`
## 模块目录
- `modules/system/user/controller`
- `modules/system/user/service`
- `modules/system/user/service/impl`
- `modules/system/user/mapper`
- `modules/system/user/entity`
- `modules/system/user/dto`
- `modules/system/user/vo`
## 接口
- `GET /api/system/user/page`
- `GET /api/system/user/{id}`
- `POST /api/system/user`
- `PUT /api/system/user/{id}`
- `DELETE /api/system/user/{id}`
- `DELETE /api/system/user/batch`
- `PATCH /api/system/user/{id}/status`
- `PUT /api/system/user/{id}/roles`
- `PUT /api/system/user/{id}/reset-password`
- `PUT /api/system/user/me/password`
- `PUT /api/system/user/me/avatar`
## 请求参数
- 分页查询：`username`、`nickname`、`phone`、`email`、`status`、`deptId`、`page`、`pageSize`。
- 批量删除：`{"ids":[1,2,3]}`。
- 修改状态：`{"status":1}`。
- 分配角色：`{"roleIds":[1,2]}`。
- 修改当前用户密码：`{"oldPassword":"old123","newPassword":"new123"}`。
- 修改当前用户头像：`{"avatar":"访问URL"}`。
## DDL 摘要
- `sys_user.id BIGINT AUTO_INCREMENT PRIMARY KEY`。
- `username VARCHAR(50) NOT NULL`，唯一索引 `uk_username(username)`。
- `nickname VARCHAR(50) NOT NULL`。
- `password VARCHAR(100) NOT NULL`。
- `phone VARCHAR(20) NULL`，不唯一。
- `email VARCHAR(100) NULL`，不唯一。
- `avatar VARCHAR(255) NULL`。
- `gender VARCHAR(20) NOT NULL DEFAULT 'UNSPECIFIED'`。
- `dept_id BIGINT NULL`，索引 `idx_dept_id(dept_id)`。
- `status TINYINT NOT NULL DEFAULT 1`。
- `is_builtin TINYINT NOT NULL DEFAULT 0`。
- `last_login_time DATETIME NULL`。
- `last_login_ip VARCHAR(45) NULL`。
- `remark VARCHAR(500) NULL`。
- `create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`。
- `update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`。
- `create_by BIGINT NULL`，`update_by BIGINT NULL`。
- `deleted TINYINT NOT NULL DEFAULT 0`，索引 `idx_deleted_status(deleted,status)`。
- `sys_user_role`：`id`、`user_id`、`role_id`、`create_time`、`create_by`；唯一索引 `uk_user_role(user_id, role_id)`。
- 关联读取：`sys_role.id`、`role_name`、`role_code`、`status`、`deleted`；`sys_dept.id`、`dept_name`、`dept_code`、`status`、`deleted`。
## 业务规则
- 用户名作为登录账号，必填且唯一。
- 新增用户使用配置项默认密码，默认值 `admin123`，业务代码不得硬编码。
- 密码入库前使用 BCrypt 加密。
- 手机号、邮箱不要求唯一。
- 用户只属于一个部门。
- 一个用户可以拥有多个角色。
- 分配角色为覆盖式保存：先清理旧关系，再写入新 `roleIds`。
- 删除和批量删除执行逻辑删除。
- `is_builtin=1` 的内置用户禁止删除。
- 账号禁用后禁止登录。
- 重置密码使用配置项默认密码，入库前 BCrypt 加密，接口返回新密码给前端。
- 当前用户修改密码必须校验旧密码。
- 当前用户修改头像只更新头像 URL，不处理文件上传。
## 返回要求
- 分页返回统一分页结构。
- 详情返回用户基础信息、部门信息、角色列表，不能返回密码密文。
- 列表和详情均不能暴露密码字段。
## 验收标准
- 用户 CRUD、状态修改、批量删除、角色分配、重置密码、个人密码和头像接口可用。
- 用户名重复、用户不存在、旧密码错误、内置用户删除返回统一业务错误。
- 逻辑删除后默认查询不返回该用户。
