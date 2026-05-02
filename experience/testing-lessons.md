# 集成测试经验教训

> 总结自配置管理模块（config module）的三轮测试调试过程。

---

## 一、测试前必须确认的事项

### 1.1 测试环境配置

在编写和运行测试之前，**必须先确认以下信息**，而不是假设默认值：

- **数据库连接**：测试使用的数据库地址、用户名、密码是否正确？是否与开发环境共用同一个库？
- **测试数据状态**：数据库中是否已有残留的测试数据？是否有 data.sql 初始化数据？
- **Schema 是否最新**：测试前是否需要重新执行 schema.sql？表结构是否与代码匹配？

**建议做法：** 每次开始测试前，向用户确认：
```
测试环境使用哪个数据库？连接信息是什么？
是否需要我先检查数据库中的现有数据？
```

### 1.2 异常处理机制

不同类型的异常在全局异常处理器中的处理方式不同，直接影响测试断言：

| 异常类型                         | HTTP 状态码 | 响应体 code |
| -------------------------------- | ----------- | ----------- |
| `BusinessException`              | 200         | 业务错误码  |
| `MethodArgumentNotValidException` | 400         | 400         |
| `DuplicateKeyException`          | **500**     | 500（未捕获时） |

**教训：** 不要假设所有错误都返回 HTTP 200。先看 `GlobalExceptionHandler` 的实现再写断言。

### 1.3 响应 VO 的字段范围

`GET /api/system/config/key/{configKey}` 返回的 `ConfigByKeyVO` 只有 4 个字段，不是完整实体。

**教训：** 写测试断言前，先确认 VO 类包含哪些字段，不要假设它和 Entity 或主 VO 一样。

---

## 二、测试数据隔离

### 2.1 逻辑删除 + 唯一索引 = 隐蔽冲突

这是本次测试遇到的最隐蔽的问题：

```
创建 configKey="test.a" → 逻辑删除（deleted=1）→ 再次创建 configKey="test.a"
→ 数据库报 DuplicateKeyException → 接口返回 500
```

**原因：**
- `@TableLogic` 让 MyBatis-Plus 的 `selectCount` 自动过滤 `deleted=1` 的行
- `ensureConfigKeyUnique` 查不到已删除的记录，认为 key 可用
- 但数据库唯一索引 `uk_config_key` 不区分 `deleted` 状态，阻止插入
- 最终 `mapper.insert()` 抛出 `DuplicateKeyException`，未被 Service 层捕获，返回 500

**解决方案：**
1. **Service 层**：`catch (DuplicateKeyException e)` 转为 `BusinessException("配置键已存在")`
2. **测试层**：使用带时间戳的唯一 key 前缀，避免跨次运行冲突

**教训：** 凡是有逻辑删除 + 唯一索引的表，都要在 Service 的 insert 操作中捕获 `DuplicateKeyException`。

### 2.2 测试数据清理策略

**不要依赖测试顺序来隔离数据。** 测试可能中途失败，后续清理不会执行。

推荐做法：
- 使用 `PREFIX = "itest." + System.currentTimeMillis() + "."` 作为 key 前缀
- 用 `List<Long> createdIds` 记录测试中创建的 ID
- 在 `@AfterAll` 中统一清理（即使前面的测试失败也会执行）
- 清理时用 `try-catch` 包裹，避免清理失败导致整体报错

---

## 三、断言编写准则

### 3.1 HTTP 状态码 vs 业务 code

| 场景                       | HTTP 状态码 | JSON code |
| -------------------------- | ----------- | --------- |
| 业务成功                   | 200         | 200       |
| 业务错误（如重复键）       | 200         | 400       |
| 参数校验失败               | **400**     | 400       |
| 未登录                     | **401**     | 401       |
| 系统异常                   | **500**     | 500       |

**规则：**
- 参数校验失败：用 `status().isBadRequest()` + `jsonPath("$.code").value(400)`
- 业务错误：用 `status().isOk()` + `jsonPath("$.code").value(400)`
- 不要混用

### 3.2 先读源码再写断言

写测试断言前，按顺序检查：

1. **Controller** → 确认请求方法、路径、参数绑定方式
2. **Service** → 确认业务逻辑、异常抛出方式
3. **GlobalExceptionHandler** → 确认各类异常的 HTTP 状态码和响应格式
4. **VO 类** → 确认响应中包含哪些字段

---

## 四、常见陷阱清单

| 陷阱 | 表现 | 预防 |
|------|------|------|
| 上次测试残留数据 | 新增接口返回 400/500 | 使用唯一 key + @AfterAll 清理 |
| 逻辑删除+唯一索引 | insert 抛 DuplicateKeyException | Service 层 catch 并转 BusinessException |
| 假设 HTTP 200 | `status().isOk()` 断言失败 | 先看 GlobalExceptionHandler 的 @ResponseStatus |
| 假设 VO 包含全部字段 | jsonPath 断言失败 | 先看 VO 类定义 |
| 测试数据 key 硬编码 | 第二次运行失败 | key 加时间戳前缀 |
| 清理代码放在测试末尾 | 测试中途失败则不执行 | 用 @AfterAll 统一清理 |

---

## 五、推荐测试模板

```java
@AutoConfigureMockMvc
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class XxxModuleIntegrationTest {

    private static final String PREFIX = "itest." + System.currentTimeMillis() + ".";
    private static final List<Long> createdIds = new ArrayList<>();

    // 测试方法中使用 PREFIX + "xxx" 作为唯一 key
    // 创建成功后将 id 加入 createdIds

    @AfterAll
    static void cleanup(@Autowired MockMvc mockMvc) throws Exception {
        // 登录 + 遍历 createdIds 逐一删除
    }

    // 辅助方法
    private String loginAsAdmin() { ... }
    private long findXxxByKey(String token, String key) { ... }
    private void createXxxDirectly(String token, String key) { ... }
}
```
