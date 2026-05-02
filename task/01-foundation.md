# 任务 Prompt：基础设施与通用能力
## 任务目标
实现后端通用基础设施，让各模块 agent 可以并行开发并复用统一契约。
## 开始前阅读
- `doc/development-constraints.md`
- `doc/ai-project-prompt.md`
## 模块边界
- 本任务只做通用框架能力，不实现具体业务 CRUD。
- 若其他模块已补充部分共享类，保留兼容并最小调整。
## 通用包结构
- 根包按当前项目实际包名确定。
- 推荐目录：`common`、`framework`、`modules`。
- 通用响应、异常、分页、校验、配置放在 `common` 或 `framework`。
## 统一响应契约
- 成功：`{"code":200,"message":"success","data":{}}`。
- 分页：`{"records":[],"total":100,"page":1,"pageSize":10}`。
- 业务响应码：`200` 成功，`400` 参数错误，`401` 未登录或 token 失效，`403` 无权限，`404` 数据不存在，`500` 系统错误。
- 参数校验失败返回字段级错误，例如 `{"username":"用户名不能为空"}`。
## 通用 DTO/VO 建议
- `PageQuery`：`page`、`pageSize`。
- `PageResult<T>`：`records`、`total`、`page`、`pageSize`。
- `BatchIdsRequest`：`List<Long> ids`。
- `StatusUpdateRequest`：`Integer status`。
## MyBatis-Plus 规则
- 主键类型：`BIGINT` 自增。
- 逻辑删除字段：`deleted`，`0=正常`，`1=已删除`。
- 启用禁用字段：`status`，`1=启用`，`0=禁用`。
- 内置数据字段：`is_builtin`，`1=内置`，`0=普通`。
- 自动填充：`create_time`、`update_time`。
- 时间类型使用 `datetime`，第一版按服务器本地时间 / 北京时间处理。
## Sa-Token 契约
- 登录接口：`POST /api/auth/login` 不需要登录。
- 其他后台接口默认需要登录。
- Token 请求头：`Authorization: Bearer <token>`。
- Token 有效期：2 小时。
- 刷新策略：滑动续期。
- 多端策略：允许同一账号多端登录；退出只退出当前 token。
## CORS 契约
- dev 允许来源：`http://localhost:5173`。
- 允许请求头：`Authorization`、`Content-Type`。
- 允许方法：`GET`、`POST`、`PUT`、`DELETE`、`PATCH`、`OPTIONS`。
- prod 必须按实际域名配置。
## Knife4j 契约
- 仅 dev 开启。
- prod 关闭文档入口。
- 按认证、用户、角色、菜单、部门、字典、日志、文件分组。
## 配置项
- `system.default-password`：默认 `admin123`。
- `system.log-clear-enabled`：dev 默认 `true`，prod 默认 `false`。
- `system.file.upload-root`：本地上传根目录。
- `spring.servlet.multipart.max-file-size`：建议 `50MB`。
- `spring.servlet.multipart.max-request-size`：按批量上传需要配置。
## 验收标准
- 未登录访问后台接口返回统一 `401` 响应。
- 参数校验失败返回统一 `400` 响应和字段级错误。
- MyBatis-Plus 分页、逻辑删除、自动填充可用。
- CORS、Knife4j、Sa-Token 配置按环境生效。
