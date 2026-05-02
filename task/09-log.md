# 任务 Prompt：日志模块 system/log
## 任务目标
实现登录日志和操作日志查询、详情、开发期清空，以及操作日志注解 + AOP 记录关键写操作。本文已包含所需接口与表字段，不依赖 `tmp` 目录。
## 开始前阅读
- `doc/development-constraints.md`
- `doc/ai-project-prompt.md`
## 模块目录
- `modules/system/log/controller`
- `modules/system/log/service`
- `modules/system/log/service/impl`
- `modules/system/log/mapper`
- `modules/system/log/entity`
- `modules/system/log/dto`
- `modules/system/log/vo`
- 操作日志注解和 AOP 可放在 `framework/log`。
## 接口
- `GET /api/system/login-log/page`
- `GET /api/system/login-log/{id}`
- `DELETE /api/system/login-log/clear`
- `GET /api/system/oper-log/page`
- `GET /api/system/oper-log/{id}`
- `DELETE /api/system/oper-log/clear`
## 请求参数
- 登录日志分页：`username`、`loginStatus`、`loginIp`、`page`、`pageSize`。
- 操作日志分页：`moduleName`、`operationType`、`operatorName`、`operationStatus`、`page`、`pageSize`。
## DDL 摘要
- `sys_login_log.id BIGINT AUTO_INCREMENT PRIMARY KEY`。
- `username VARCHAR(50) NOT NULL`。
- `login_status VARCHAR(20) NOT NULL`，取值 `SUCCESS/FAIL`。
- `login_ip VARCHAR(45) NULL`。
- `login_location VARCHAR(100) NULL`。
- `browser VARCHAR(100) NULL`。
- `os VARCHAR(100) NULL`。
- `user_agent VARCHAR(500) NULL`。
- `message VARCHAR(500) NULL`。
- `login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`。
- `create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`。
- 索引：`idx_username(username)`、`idx_login_status(login_status)`、`idx_login_time(login_time)`。
- `sys_oper_log.id BIGINT AUTO_INCREMENT PRIMARY KEY`。
- `module_name VARCHAR(100) NOT NULL`。
- `operation_type VARCHAR(50) NOT NULL`，取值 `CREATE/UPDATE/DELETE/IMPORT/EXPORT`。
- `request_method VARCHAR(20) NULL`。
- `request_url VARCHAR(500) NULL`。
- `operator_id BIGINT NULL`。
- `operator_name VARCHAR(50) NULL`。
- `operator_ip VARCHAR(45) NULL`。
- `operator_location VARCHAR(100) NULL`。
- `request_params TEXT NULL`。
- `response_result TEXT NULL`。
- `cost_time BIGINT NULL`，单位毫秒。
- `operation_status VARCHAR(20) NOT NULL`，取值 `SUCCESS/FAIL`。
- `error_message TEXT NULL`。
- `operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`。
- `create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`。
- 索引：`idx_module_name(module_name)`、`idx_operation_type(operation_type)`、`idx_operator_id(operator_id)`、`idx_operation_status(operation_status)`、`idx_operation_time(operation_time)`。
## 业务规则
- 日志表不设计 `deleted` 字段。
- 日志接口不提供单条删除和批量删除。
- 清空接口仅开发阶段使用。
- 清空日志使用物理删除。
- 清空接口不做额外角色限制，但必须受 `system.log-clear-enabled` 控制。
- dev 默认开启，prod 默认关闭。
- 配置关闭时调用清空接口返回 `403`。
- 登录日志不走操作日志注解，由认证登录逻辑单独记录。
- 操作日志使用注解 + AOP，例如 `@OperLog(title = "用户管理", type = "CREATE")`。
- 操作日志只记录关键写操作，不记录查询日志。
- 请求参数中的 `password`、`token` 等敏感字段必须脱敏。
- 响应结果不要记录完整大对象，可以记录摘要或截断内容。
- 登录地点、操作地点第一版可以先预留。
## 验收标准
- 登录日志分页、详情、清空可用。
- 操作日志分页、详情、清空可用。
- 带注解的写操作能记录请求方法、URL、操作人、IP、参数摘要、响应摘要、耗时、状态和错误信息。
- `system.log-clear-enabled=false` 时清空接口返回 `403`。
