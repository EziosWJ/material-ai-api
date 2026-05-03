# 任务：代码审查问题修复

基于代码审查发现的问题，按优先级修复以下问题。

## P0 — 安全问题

### 1. 移除硬编码数据库密码
- **文件**: `src/main/resources/application.yml:14`
- **问题**: DB 密码 `rYEntANYGGbDBFe3` 作为 `${DB_PASSWORD}` 默认值提交到 Git
- **修复**: 移除默认值，改为 `${DB_PASSWORD}`（无默认值），部署时必须通过环境变量注入

### 2. 移除 Java 层默认密码硬编码
- **文件**: `framework/config/SystemProperties.java:10`
- **问题**: `defaultPassword = "admin123"` 硬编码在 Java 代码中
- **修复**: 移除 Java 默认值，改为从 `application.yml` 必须显式配置 `system.default-password`

### 3. 文件预览接口加回认证
- **文件**: `framework/config/SaTokenConfig.java:18`
- **问题**: `/api/system/file/*/view` 在排除路径中，任何人可枚举 ID 查看文件
- **修复**: 从 `EXCLUDE_PATHS` 中移除该路径，文件预览/下载均需登录

### 4. 文件上传路径穿越校验
- **文件**: `modules/system/file/service/impl/FileServiceImpl.java:69-71`
- **问题**: `targetPath` 未校验是否在 `uploadRoot` 内；`uploadRoot` 为相对路径
- **修复**:
  - 将 `uploadRoot` 转为绝对路径后再 resolve
  - 校验 `targetPath.startsWith(uploadRoot.toAbsolutePath())`
  - 同样修复 `loadResource()` 方法（line 190）

## P1 — 事务与健壮性

### 5. 补充缺失的 @Transactional
- `RoleServiceImpl.create()` (line 76)
- `RoleServiceImpl.update()` (line 87)
- `RoleServiceImpl.updateStatus()` (line 123)
- `MenuServiceImpl.create()` (line 76)
- `MenuServiceImpl.update()` (line 88)
- `MenuServiceImpl.updateStatus()` (line 122)
- `UserServiceImpl.updateStatus()` (line 132)
- `UserServiceImpl.resetPassword()` (line 157)
- `UserServiceImpl.changeCurrentPassword()` (line 168)
- `UserServiceImpl.updateCurrentAvatar()` (line 180)

### 6. OperLogAspect 异常处理加日志
- **文件**: `framework/log/OperLogAspect.java:76,91`
- **问题**: `catch (RuntimeException ignored)` 完全无日志
- **修复**: 改为 `catch (RuntimeException e)` 并 `log.warn("操作日志记录失败", e)`

## P2 — 代码质量

### 7. 抽取 getClientIp 工具方法
- **重复位置**:
  - `AuthServiceImpl.java:236-245`
  - `OperLogAspect.java:136-145`
- **修复**: 在 `common/util/` 下新建 `IpUtil.java`，两处改为调用

### 8. AuthServiceImpl 改用 Service 调用
- **文件**: `modules/auth/service/impl/AuthServiceImpl.java`
- **问题**: 直接注入 5 个 Mapper，跳过 Service 层
- **修复**: 改为注入 `UserService`、`RoleService`、`MenuService`、`DeptService`，通过 Service 获取数据
- **注意**: 需要在对应 Service 中补充 AuthService 需要的查询方法（如 `getById`、`selectEnabledRolesByUserId` 等），或者保持现有 Mapper 调用但缩小范围——本次只做最小改动：将 `deptMapper.selectById` 改为通过已有方法获取，其余保持不变，避免过度改动

**决策**: 考虑到 AuthService 的查询需求（按 ID 查单个用户、按 userId 查角色、按 userId 查菜单）与现有 Service 的分页/CRUD 接口不完全匹配，强行改用 Service 会引入不必要的接口膨胀。本次保持 Mapper 直接调用，但在代码中加注释说明原因。此项降级为建议，不强制修改。

## 不修改项

- **#7 重置密码返回明文**: 这是有意设计（admin 需要知道重置后的密码以便线下通知用户），保持不变
- **#6 StatusUpdateRequest 校验**: 已有 `@Min(0) @Max(1)` 注解，Controller 均使用 `@Valid`，无需修改
- **#14 BeanUtils.copyProperties**: 当前项目规模下可接受，不做改动
- **#17 sa-token.active-timeout**: 属于配置策略选择，不在本次修复范围

## 执行顺序

1. P0 安全修复（#1-#4）
2. P1 事务补充（#5-#6）
3. P2 代码质量（#7）
