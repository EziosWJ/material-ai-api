# 任务 Prompt：字典模块 system/dict
## 任务目标
实现字典类型和字典数据管理，并提供前端按字典编码查询字典项接口。本文已包含所需接口与表字段，不依赖 `tmp` 目录。
## 开始前阅读
- `doc/development-constraints.md`
- `doc/ai-project-prompt.md`
## 模块目录
- `modules/system/dict/controller`
- `modules/system/dict/service`
- `modules/system/dict/service/impl`
- `modules/system/dict/mapper`
- `modules/system/dict/entity`
- `modules/system/dict/dto`
- `modules/system/dict/vo`
## 接口
- `GET /api/system/dict-type/page`
- `GET /api/system/dict-type/{id}`
- `POST /api/system/dict-type`
- `PUT /api/system/dict-type/{id}`
- `DELETE /api/system/dict-type/{id}`
- `DELETE /api/system/dict-type/batch`
- `PATCH /api/system/dict-type/{id}/status`
- `GET /api/system/dict-data/page`
- `GET /api/system/dict-data/{id}`
- `POST /api/system/dict-data`
- `PUT /api/system/dict-data/{id}`
- `DELETE /api/system/dict-data/{id}`
- `DELETE /api/system/dict-data/batch`
- `GET /api/system/dict/{dictCode}/items`
## 请求参数
- 字典类型分页：`dictName`、`dictCode`、`status`、`page`、`pageSize`。
- 字典数据分页：`dictTypeId`、`dictCode`、`dictLabel`、`dictValue`、`page`、`pageSize`。
- 批量删除：`{"ids":[1,2,3]}`。
- 修改字典类型状态：`{"status":1}`。
## DDL 摘要
- `sys_dict_type.id BIGINT AUTO_INCREMENT PRIMARY KEY`。
- `dict_name VARCHAR(100) NOT NULL`。
- `dict_code VARCHAR(100) NOT NULL`，唯一索引 `uk_dict_code(dict_code)`。
- `status TINYINT NOT NULL DEFAULT 1`。
- `sort_order INT NOT NULL DEFAULT 0`。
- `is_builtin TINYINT NOT NULL DEFAULT 0`。
- `remark VARCHAR(500) NULL`。
- `create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`。
- `update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`。
- `create_by BIGINT NULL`，`update_by BIGINT NULL`。
- `deleted TINYINT NOT NULL DEFAULT 0`，索引 `idx_deleted_status_sort(deleted,status,sort_order)`。
- `sys_dict_data.id BIGINT AUTO_INCREMENT PRIMARY KEY`。
- `dict_type_id BIGINT NOT NULL`，索引 `idx_dict_type_id(dict_type_id)`。
- `dict_label VARCHAR(100) NOT NULL`。
- `dict_value VARCHAR(100) NOT NULL`。
- `sort_order INT NOT NULL DEFAULT 0`。
- `remark VARCHAR(500) NULL`。
- `create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`。
- `update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`。
- `create_by BIGINT NULL`，`update_by BIGINT NULL`。
- `deleted TINYINT NOT NULL DEFAULT 0`。
- 唯一索引：`uk_type_value(dict_type_id, dict_value)`。
- 索引：`idx_deleted_sort(deleted,sort_order)`。
## 业务规则
- 字典类型 `dict_code` 必填且唯一。
- 字典类型支持启用/禁用、排序、内置标识。
- `is_builtin=1` 的字典类型禁止删除。
- 内置字典类型禁止修改编码。
- 删除字典类型前检查是否存在未删除字典数据。
- 同一个字典类型下 `dict_value` 唯一。
- 字典数据支持排序。
- 字典数据暂不做启用/禁用状态。
- 枚举类字段使用字符串编码，数据库不存中文展示名。
- 按字典编码查询字典项时，只返回启用且未删除的字典类型下未删除字典数据。
## 返回要求
- 字典项返回：`label`、`value`、`sortOrder`。
- 字典项按 `sort_order`、`id` 排序。
## 验收标准
- 字典类型和字典数据 CRUD 可用。
- 重复 `dict_code`、重复 `dict_value`、删除内置字典类型、删除存在数据的字典类型返回统一业务错误。
