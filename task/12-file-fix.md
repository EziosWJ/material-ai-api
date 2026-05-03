# 任务 Prompt：文件模块问题修复 file-fix

## 任务目标

修复前端对接文件管理模块时提出的 5 个问题，涉及 3 处后端代码改动和 2 处文档更新。

## 开始前阅读

- `doc/development-constraints.md`
- `doc/ai-project-prompt.md`
- `doc/API_INTEGRATION_TODOS.md`（了解原始问题列表）
- `doc/API_INTEGRATION_FEEDBACK.md`（了解已有反馈格式）
- `task/08-file.md`（了解文件模块原始设计）

## 涉及文件

| 文件 | 改动类型 |
|------|----------|
| `src/.../framework/config/SaTokenConfig.java` | 修改 |
| `src/.../modules/system/file/service/FileService.java` | 修改 |
| `src/.../modules/system/file/service/impl/FileServiceImpl.java` | 修改 |
| `src/.../modules/system/file/controller/FileController.java` | 修改 |
| `src/.../modules/system/file/dto/BatchUploadResult.java` | **新增** |
| `doc/API_INTEGRATION_TODOS.md` | 修改 |
| `doc/API_INTEGRATION_FEEDBACK.md` | 修改 |

## 改动 1：预览接口免登录放行

**背景**：`GET /api/system/file/{id}/view` 被 Sa-Token 全局拦截器要求登录，前端 `<img src>` / `<iframe src>` 无法携带 Authorization header，导致预览 401。

**方案**：将预览接口加入 Sa-Token 排除路径，使其免登录可访问。文件 ID 本身具有一定不可猜测性，风险可控。后续可升级为签名 URL 方案。

**改动**：修改 `SaTokenConfig.java` 的 `EXCLUDE_PATHS` 列表，新增：

```java
"/api/system/file/*/view"
```

注意使用 `*` 匹配单级路径段（文件 ID），不要用 `**`。

**安全说明**：下载接口 `/download` 保持需要登录，仅放行预览。这是临时方案，需在代码注释中标注 `// TODO: 预览接口临时免登录，后续改为签名 URL 方案`。

## 改动 2：批量上传部分失败处理

**背景**：当前 `uploadBatch` 在某个文件上传失败时直接抛异常，但前面已成功的文件事务已提交，导致数据不一致——前端收到错误响应，但部分文件已入库。

**方案**：用 try-catch 包裹单个文件上传，收集成功和失败结果，统一返回。

### 2.1 新增 DTO

新增 `src/.../modules/system/file/dto/BatchUploadResult.java`：

```java
package cn.ezios.baseapi.modules.system.file.dto;

import cn.ezios.baseapi.modules.system.file.vo.FileVO;
import java.util.List;

public class BatchUploadResult {
    private List<FileVO> succeeded;
    private List<FailedItem> failed;

    // constructor, getters, setters

    public static class FailedItem {
        private String fileName;
        private String message;
        // constructor, getters, setters
    }
}
```

JSON 结构示例：

```json
{
  "succeeded": [FileVO, ...],
  "failed": [
    {"fileName": "a.txt", "message": "单文件不能超过 50MB"},
    {"fileName": "b.txt", "message": "文件保存失败"}
  ]
}
```

### 2.2 修改 Service 接口

`FileService.java` 中 `uploadBatch` 返回类型从 `List<FileVO>` 改为 `BatchUploadResult`。

### 2.3 修改 Service 实现

`FileServiceImpl.java` 中 `uploadBatch` 方法改为：

```java
@Override
public BatchUploadResult uploadBatch(MultipartFile[] files, String businessModule, String remark) {
    if (files == null || files.length == 0) {
        throw new BusinessException("文件不能为空");
    }
    List<FileVO> succeeded = new ArrayList<>();
    List<BatchUploadResult.FailedItem> failed = new ArrayList<>();
    for (MultipartFile file : files) {
        try {
            succeeded.add(upload(file, businessModule, remark));
        } catch (Exception e) {
            String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown";
            failed.add(new BatchUploadResult.FailedItem(fileName, e.getMessage()));
        }
    }
    return new BatchUploadResult(succeeded, failed);
}
```

### 2.4 修改 Controller

`FileController.java` 中 `uploadBatch` 方法返回类型改为 `ApiResponse<BatchUploadResult>`。

## 改动 3：更新文档

### 3.1 更新 `doc/API_INTEGRATION_TODOS.md`

- **删除**「系统配置模块」整个章节（全部已确认，无需保留）
- **保留**「文件管理模块」章节，将 5 个 TODO 更新为已处理状态：

| TODO | 处理结果 |
|------|----------|
| 预览接口鉴权 | 已放行：预览接口免登录，下载接口仍需登录。后续计划改为签名 URL |
| Content-Disposition 文件名 | 已确认：下载使用 RFC 5987 `filename*=UTF-8''` 编码。前端优先使用列表中的 `originalName` 作为文件名 |
| status=0 文件预览/下载 | 已确认：后端不限制，`status=0` 的文件仍可预览和下载。前端保持展示操作按钮 |
| upload-batch 顺序和部分失败 | 已修复：返回顺序与上传顺序一致；新增部分失败处理，返回 `{succeeded, failed}` 结构 |
| mimeType/businessModule 选项 | 已确认：`businessModule` 可通过 `GET /api/system/dict/FILE_BUSINESS_MODULE/items` 获取；`mimeType` 无选项接口，前端用文本筛选 |

### 3.2 更新 `doc/API_INTEGRATION_FEEDBACK.md`

在「已解决事项」末尾追加以下内容（保持已有格式）：

```markdown
### 9. 文件预览接口鉴权放行

**问题**：`GET /api/system/file/{id}/view` 要求登录，`<img src>` / `<iframe src>` 无法携带 token。

**处理**：预览接口已加入 Sa-Token 排除路径，无需登录即可访问。下载接口仍保持登录要求。

**注意**：这是临时方案，后续计划改为签名 URL。前端可直接使用 `<img src="/api/system/file/{id}/view">` 预览图片。

### 10. 批量上传部分失败处理

**问题**：`POST /api/system/file/upload-batch` 某个文件失败时，前面已成功的文件已入库但前端收到错误。

**处理**：批量上传接口改为始终返回 200，通过响应体中的 `succeeded` 和 `failed` 数组区分成功和失败：

{
  "code": 200,
  "message": "success",
  "data": {
    "succeeded": [FileVO, ...],
    "failed": [
      {"fileName": "a.txt", "message": "单文件不能超过 50MB"}
    ]
  }
}

前端应遍历 `failed` 数组提示用户哪些文件上传失败及原因。
```

## 验收标准

- [ ] `GET /api/system/file/{id}/view` 无需 token 即可访问（返回 200 + 文件内容）
- [ ] `GET /api/system/file/{id}/download` 仍需 token（无 token 返回 401）
- [ ] `POST /api/system/file/upload-batch` 部分文件失败时，响应 200 且 `succeeded` 包含成功的文件、`failed` 包含失败的文件及原因
- [ ] `POST /api/system/file/upload-batch` 全部成功时，`failed` 为空数组
- [ ] `POST /api/system/file/upload-batch` 全部失败时，`succeeded` 为空数组
- [ ] `doc/API_INTEGRATION_TODOS.md` 系统配置章节已删除，文件管理章节已更新
- [ ] `doc/API_INTEGRATION_FEEDBACK.md` 已追加第 9、10 条反馈
