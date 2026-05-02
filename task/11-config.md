# 任务 Prompt：配置模块 system/config
## 任务目标
实现系统配置项管理，支持按配置键查询配置值。本文已包含所需接口与表字段，不依赖 `tmp` 目录。
## 开始前阅读
- `doc/development-constraints.md`
- `doc/ai-project-prompt.md`
## 模块目录
- `modules/system/config/controller`
- `modules/system/config/service`
- `modules/system/config/service/impl`
- `modules/system/config/mapper`
- `modules/system/config/entity`
- `modules/system/config/dto`
- `modules/system/config/vo`
## 接口
- `GET /api/system/config/page`
- `GET /api/system/config/{id}`
- `GET /api/system/config/key/{configKey}`
- `POST /api/system/config`
- `PUT /api/system/config/{id}`
- `DELETE /api/system/config/{id}`
- `POST /api/system/config/batch-delete`
- `PATCH /api/system/config/{id}/status`
## 请求参数
- 分页查询：`configName`、`configKey`、`configType`、`status`、`page`、`pageSize`。
- 新增/修改：`configName`（必填）、`configKey`（必填，修改时标识字段禁止修改）、`configValue`、`configType`（默认 SYSTEM）、`valueType`（默认 TEXT）、`status`、`remark`。
- 批量删除：`{"ids":[1,2,3]}`。
- 修改状态：`{"status":1}`。
## DDL 摘要
- `sys_config.id BIGINT AUTO_INCREMENT PRIMARY KEY`。
- `config_name VARCHAR(100) NOT NULL`。
- `config_key VARCHAR(100) NOT NULL`，唯一索引 `uk_config_key(config_key)`。
- `config_value VARCHAR(500) NULL`。
- `config_type VARCHAR(20) NOT NULL DEFAULT 'SYSTEM'`，枚举编码：`SYSTEM`（系统配置）、`CUSTOM`（自定义配置）。索引 `idx_config_type(config_type)`。
- `value_type VARCHAR(20) NOT NULL DEFAULT 'TEXT'`，枚举编码：`TEXT`（文本）、`NUMBER`（数字）、`BOOLEAN`（布尔）。
- `status TINYINT NOT NULL DEFAULT 1`。
- `is_builtin TINYINT NOT NULL DEFAULT 0`。
- `remark VARCHAR(500) NULL`。
- `create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`。
- `update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`。
- `create_by BIGINT NULL`，`update_by BIGINT NULL`。
- `deleted TINYINT NOT NULL DEFAULT 0`。
- 索引：`idx_deleted_status(deleted, status)`。
## 业务规则
- `config_key` 必填且唯一。
- `config_type` 枚举值为 `SYSTEM`、`CUSTOM`，使用字符串编码存储。
- `value_type` 枚举值为 `TEXT`、`NUMBER`、`BOOLEAN`，使用字符串编码存储。
- `is_builtin=1` 的配置项禁止修改、禁止删除。
- 批量删除时跳过内置配置项（静默跳过，不报错）。
- 删除执行逻辑删除。
- 按配置键查询接口（`GET /api/system/config/key/{configKey}`）只返回 `deleted=0`、`status=1` 的配置。
- 按配置键查询接口返回 `configValue`、`valueType`、`configName`。
- 配置管理模块对应的菜单数据已在 `data.sql` 中初始化（`id=11`）。
- 字典数据 `CONFIG_TYPE` 和 `CONFIG_VALUE_TYPE` 已在 `data.sql` 中初始化。
- 内置配置项 `system.log-clear-enabled` 已在 `data.sql` 中初始化。
## 返回要求
- 分页和详情返回全部字段。
- 按配置键查询返回：`configKey`、`configValue`、`valueType`、`configName`。
## 验收标准
- 配置项 CRUD 可用。
- 重复 `config_key` 返回统一业务错误。
- 修改或删除内置配置项返回统一业务错误。
- 批量删除中包含内置项时，内置项被跳过，非内置项正常删除。
- 按配置键查询不存在或已禁用的配置返回 404。
