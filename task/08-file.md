# 任务 Prompt：文件模块 system/file
## 任务目标
实现本地文件管理模块：单文件上传、批量上传、分页、详情、修改元信息、逻辑删除、批量删除、启用禁用、下载、预览。本文已包含所需接口与表字段，不依赖 `tmp` 目录。
## 开始前阅读
- `doc/development-constraints.md`
- `doc/ai-project-prompt.md`
## 模块目录
- `modules/system/file/controller`
- `modules/system/file/service`
- `modules/system/file/service/impl`
- `modules/system/file/mapper`
- `modules/system/file/entity`
- `modules/system/file/dto`
- `modules/system/file/vo`
## 接口
- `POST /api/system/file/upload`
- `POST /api/system/file/upload-batch`
- `GET /api/system/file/page`
- `GET /api/system/file/{id}`
- `PUT /api/system/file/{id}`
- `DELETE /api/system/file/{id}`
- `DELETE /api/system/file/batch`
- `PATCH /api/system/file/{id}/status`
- `GET /api/system/file/{id}/download`
- `GET /api/system/file/{id}/view`
## 请求参数
- 上传请求类型：`multipart/form-data`。
- 单文件上传参数：`file`、`businessModule`、`remark`。
- 多文件上传参数：`files`、`businessModule`、`remark`。
- 分页查询：`originalName`、`businessModule`、`mimeType`、`status`、`page`、`pageSize`。
- 修改文件信息：`businessModule`、`remark`。
- 批量删除：`{"ids":[1,2,3]}`。
- 修改状态：`{"status":1}`。
## DDL 摘要
- `sys_file.id BIGINT AUTO_INCREMENT PRIMARY KEY`。
- `original_name VARCHAR(255) NOT NULL`。
- `storage_name VARCHAR(255) NOT NULL`。
- `extension VARCHAR(50) NULL`。
- `mime_type VARCHAR(100) NULL`。
- `file_size BIGINT NOT NULL DEFAULT 0`。
- `file_md5 VARCHAR(32) NULL`，索引 `idx_file_md5(file_md5)`。
- `storage_path VARCHAR(500) NOT NULL`。
- `access_url VARCHAR(500) NULL`。
- `business_module VARCHAR(50) NULL`，索引 `idx_business_module(business_module)`。
- `status TINYINT NOT NULL DEFAULT 1`。
- `remark VARCHAR(500) NULL`。
- `create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP`。
- `update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`。
- `create_by BIGINT NULL`，`update_by BIGINT NULL`。
- `deleted TINYINT NOT NULL DEFAULT 0`。
- 索引：`idx_deleted_status(deleted,status)`、`idx_create_time(create_time)`。
## 业务规则
- 第一版使用本地文件存储，不接入对象存储。
- 单文件最大 50MB。
- 第一版不限制文件类型。
- 上传目录按日期分组：`uploads/yyyy/MM/dd/xxx.ext`。
- 记录原始文件名、存储文件名、扩展名、MIME 类型、文件大小、MD5、存储相对路径、访问 URL、业务模块。
- `storage_path` 存物理相对路径，不存绝对磁盘路径。
- `access_url` 存后端访问接口，不存磁盘路径。
- 下载和预览必须通过后端接口转发。
- 文件不存在时返回 `404`。
- 修改文件信息只允许修改备注、业务模块等元信息，不修改物理文件内容。
- 删除和批量删除只做逻辑删除，不同步删除本地物理文件。
- 文件预览和下载也需要登录。
## 返回要求
- 上传成功返回文件元信息和 `accessUrl`。
- 下载接口设置合理文件名和内容类型。
- 预览接口适用于图片、PDF 等浏览器可预览文件。
## 验收标准
- 上传后 `sys_file` 写入完整元信息。
- 分页、详情、修改、删除、状态修改、下载、预览可用。
- 下载和预览不暴露服务器磁盘路径。
