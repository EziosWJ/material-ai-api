# 材料 AI 业务模块 Issue 2-8 实现总结

## 修改文件

- `docs/MATERIAL_AI_DATABASE_DESIGN.md`
- `docs/MATERIAL_AI_DATABASE_DESIGN_SUMMARY.md`
- `src/main/java/cn/ezios/baseapi/modules/ai/**`
- `src/main/java/cn/ezios/baseapi/modules/material/**`
- `src/main/java/cn/ezios/baseapi/modules/writing/**`
- `src/main/java/cn/ezios/baseapi/modules/qa/**`

## 实现内容

1. 补充材料 AI 数据库设计说明，明确业务表统一使用 `biz_` 前缀、`material` 为主域、`TEXT` 存储 JSON 字符串、无物理外键、逻辑删除、审计字段和索引规范。
2. 新增材料基础层，覆盖材料主数据、材料处理记录、材料创建、分页、详情、更新、逻辑删除、材料处理和向量删除入口。
3. 新增 AI 基础层，覆盖 Python AI 服务调用客户端、请求响应 DTO、调用日志实体、Mapper、Service 和配置读取。
4. 新增材料处理流程，Java 后端负责材料权限校验、状态流转、Python 调用、处理记录、失败原因和 AI 调用审计。
5. 新增写作任务链路，Java 后端负责写作任务创建、材料范围控制、状态流转、Python 生成调用、结果保存、来源片段快照和调用审计。
6. 新增问答链路，Java 后端负责问答会话、材料范围、用户消息、Python 问答调用、助手回复、来源片段快照和调用审计。
7. 代码实现保持前端只调用 Java 后端、Java 后端通过 HTTP 调用 Python AI 服务、Python 不直接访问 Java 业务数据库的边界。

## 验证结果

- 已执行 `./mvnw -DskipTests clean compile`，编译通过。
- 已执行 `./mvnw test`，测试过程中系统集成测试在登录管理员账号时无法连接本地 MySQL，失败点为 `SysUserMapper` 查询数据库获取连接失败，属于当前测试环境缺少可用 MySQL 服务或测试库配置不可用，不是新增模块编译错误。

## 未完成事项

- 尚未接入真实 Python AI 服务做端到端联调。
- 尚未补充针对材料、写作、问答模块的独立单元测试或集成测试。
- GitHub issue 状态尚未关闭，建议在人工验收后再关闭对应 issue。
