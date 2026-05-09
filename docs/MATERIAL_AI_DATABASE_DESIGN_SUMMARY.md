# 材料 AI 数据模型说明文档任务总结

## 修改文件

- `docs/MATERIAL_AI_DATABASE_DESIGN.md`
- `docs/MATERIAL_AI_DATABASE_DESIGN_SUMMARY.md`

## 实现内容

- 新增材料 AI 数据模型说明文档，覆盖 `material_ai_schema_v1.sql` 中 9 张 `biz_` 业务表的职责、核心字段语义和关系。
- 补充材料、问答、写作任务、AI 调用日志的状态流转说明。
- 说明索引策略、`source_segments_json` JSON 快照规范、不单独建立片段表的原因。
- 明确 Java 后端与 Python AI 服务的职责边界，强调前端只调用 Java 后端、Python 不直接访问 Java 业务数据库。

## 验证结果

- 已根据以下文件进行对齐：`docs/MATERIAL_AI_DATABASE_PROMPT_REVIEW.md`、`docs/backend-architecture.md`、`docs/domain-language.md`、`src/main/resources/sql/material_ai_schema_v1.sql`。
- 本次只新增文档，未修改 Java 代码或 SQL 文件。

## 未完成事项

- 未做业务代码实现。
- 未做数据库迁移执行验证。
