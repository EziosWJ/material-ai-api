package cn.ezios.baseapi.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WritingTaskModuleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ========== 分页查询 ==========

    @Test
    @Order(10)
    void taskPage_shouldReturnSuccess() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/writing/task/page")
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10));
    }

    @Test
    @Order(11)
    void taskPage_shouldContainAllFields() throws Exception {
        String token = loginAsAdmin();

        String response = mockMvc.perform(get("/api/writing/task/page")
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("pageSize", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode records = objectMapper.readTree(response).path("data").path("records");
        if (records.isArray() && !records.isEmpty()) {
            JsonNode first = records.get(0);
            assert first.has("id") : "应包含 id";
            assert first.has("userId") : "应包含 userId";
            assert first.has("title") : "应包含 title";
            assert first.has("writingType") : "应包含 writingType";
            assert first.has("topic") : "应包含 topic";
            assert first.has("status") : "应包含 status";
            assert first.has("materialIds") : "应包含 materialIds";
            assert first.has("createTime") : "应包含 createTime";
        }
    }

    @Test
    @Order(12)
    void taskPage_shouldFilterByWritingType() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/writing/task/page")
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("pageSize", "10")
                        .param("writingType", "draft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @Order(13)
    void taskPage_shouldFilterByStatus() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/writing/task/page")
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("pageSize", "10")
                        .param("status", "success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @Order(14)
    void taskPage_shouldFilterByTitle() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/writing/task/page")
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("pageSize", "10")
                        .param("title", "不存在的标题xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.records").isEmpty());
    }

    // ========== 详情 ==========

    @Test
    @Order(20)
    void taskDetail_shouldReturn404_whenNotFound() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/writing/task/99999")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("数据不存在"));
    }

    @Test
    @Order(21)
    void taskDetail_shouldReturnTask_withResult_whenExists() throws Exception {
        String token = loginAsAdmin();

        // 先查询分页，看是否有现有数据
        String pageResponse = mockMvc.perform(get("/api/writing/task/page")
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("pageSize", "1")
                        .param("status", "success"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode records = objectMapper.readTree(pageResponse).path("data").path("records");
        if (records.isArray() && !records.isEmpty()) {
            long taskId = records.get(0).path("id").asLong();

            mockMvc.perform(get("/api/writing/task/" + taskId)
                            .header("Authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value(taskId))
                    .andExpect(jsonPath("$.data.title").exists())
                    .andExpect(jsonPath("$.data.writingType").exists())
                    .andExpect(jsonPath("$.data.topic").exists())
                    .andExpect(jsonPath("$.data.status").exists())
                    .andExpect(jsonPath("$.data.materialIds").isArray())
                    .andExpect(jsonPath("$.data.result").isNotEmpty())
                    .andExpect(jsonPath("$.data.result.content").exists())
                    .andExpect(jsonPath("$.data.result.sourceSegments").isArray());
        }
    }

    // ========== 创建写作任务（验证参数校验，不实际调用 AI） ==========

    @Test
    @Order(30)
    void createTask_shouldFail_whenMissingTitle() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "writingType": "draft",
                    "topic": "测试主题"
                }
                """;

        mockMvc.perform(post("/api/writing/task")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.title").exists());
    }

    @Test
    @Order(31)
    void createTask_shouldFail_whenMissingWritingType() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "title": "测试任务",
                    "topic": "测试主题"
                }
                """;

        mockMvc.perform(post("/api/writing/task")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.writingType").exists());
    }

    @Test
    @Order(32)
    void createTask_shouldFail_whenMissingTopic() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "title": "测试任务",
                    "writingType": "draft"
                }
                """;

        mockMvc.perform(post("/api/writing/task")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.topic").exists());
    }

    @Test
    @Order(33)
    void createTask_shouldFail_whenInvalidWritingType() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "title": "测试任务",
                    "writingType": "invalid_type",
                    "topic": "测试主题"
                }
                """;

        mockMvc.perform(post("/api/writing/task")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @Order(34)
    void createTask_shouldFail_whenPolishedWithoutInputContent() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "title": "润色任务",
                    "writingType": "polished",
                    "topic": "测试主题"
                }
                """;

        mockMvc.perform(post("/api/writing/task")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("润色写作任务的 inputContent 不能为空"));
    }

    // ========== 未认证访问 ==========

    @Test
    @Order(90)
    void writingApi_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/writing/task/page"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    // ========== 辅助方法 ==========

    private String loginAsAdmin() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).path("data").path("tokenValue").asText();
    }
}
