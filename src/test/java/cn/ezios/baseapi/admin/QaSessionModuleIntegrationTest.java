package cn.ezios.baseapi.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QaSessionModuleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final List<Long> createdSessionIds = new ArrayList<>();
    private static final List<Long> createdMaterialIds = new ArrayList<>();
    private static final List<Long> createdFileIds = new ArrayList<>();
    private static Long testMaterialId;

    // ========== 前置：创建测试材料 ==========

    @Test
    @Order(0)
    void setup_createTestMaterial() throws Exception {
        String token = loginAsAdmin();

        // 上传文件
        MockMultipartFile file = new MockMultipartFile(
                "file", "问答测试材料.txt", "text/plain",
                "这是用于问答模块集成测试的材料内容。".getBytes(StandardCharsets.UTF_8));

        String fileResponse = mockMvc.perform(multipart("/api/system/file/upload")
                                .file(file)
                                .param("businessModule", "material")
                                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode fileData = objectMapper.readTree(fileResponse).path("data");
        long fileId = fileData.path("id").asLong();
        String storagePath = fileData.path("storagePath").asText();
        String originalName = fileData.path("originalName").asText();
        createdFileIds.add(fileId);

        // 创建材料
        String materialBody = """
                {
                    "userId": 1,
                    "title": "问答测试材料",
                    "originalFilename": "%s",
                    "fileId": %d,
                    "fileType": "txt",
                    "fileSize": 100,
                    "storagePath": "%s",
                    "status": "available"
                }
                """.formatted(originalName, fileId, storagePath);

        String materialResponse = mockMvc.perform(post("/api/material")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(materialBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        testMaterialId = objectMapper.readTree(materialResponse).path("data").path("id").asLong();
        createdMaterialIds.add(testMaterialId);
    }

    // ========== 创建问答会话 ==========

    @Test
    @Order(10)
    void createSession_shouldSucceed_withMaterials() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "title": "集成测试问答会话",
                    "materialIds": [%d]
                }
                """.formatted(testMaterialId);

        String response = mockMvc.perform(post("/api/qa/session")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.title").value("集成测试问答会话"))
                .andExpect(jsonPath("$.data.status").value("active"))
                .andExpect(jsonPath("$.data.messageCount").value(0))
                .andExpect(jsonPath("$.data.materials").isArray())
                .andExpect(jsonPath("$.data.materials[0].materialId").value(testMaterialId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).path("data").path("id").asLong();
        createdSessionIds.add(id);
    }

    @Test
    @Order(11)
    void createSession_shouldSucceed_withoutTitle() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "materialIds": [%d]
                }
                """.formatted(testMaterialId);

        String response = mockMvc.perform(post("/api/qa/session")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("新问答会话"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).path("data").path("id").asLong();
        createdSessionIds.add(id);
    }

    @Test
    @Order(12)
    void createSession_shouldSucceed_withoutMaterials() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "title": "无材料会话"
                }
                """;

        String response = mockMvc.perform(post("/api/qa/session")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("无材料会话"))
                .andExpect(jsonPath("$.data.materials").isEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).path("data").path("id").asLong();
        createdSessionIds.add(id);
    }

    // ========== 分页查询 ==========

    @Test
    @Order(20)
    void sessionPage_shouldReturnSuccess() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/qa/session/page")
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
    @Order(21)
    void sessionPage_shouldContainAllFields() throws Exception {
        String token = loginAsAdmin();

        String response = mockMvc.perform(get("/api/qa/session/page")
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
            assert first.has("title") : "应包含 title";
            assert first.has("status") : "应包含 status";
            assert first.has("messageCount") : "应包含 messageCount";
            assert first.has("materials") : "应包含 materials";
            assert first.has("createTime") : "应包含 createTime";
        }
    }

    @Test
    @Order(22)
    void sessionPage_shouldFilterByStatus() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/qa/session/page")
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("pageSize", "10")
                        .param("status", "active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    // ========== 会话详情 ==========

    @Test
    @Order(30)
    void sessionDetail_shouldReturnSession() throws Exception {
        String token = loginAsAdmin();
        long id = ensureSessionCreated(token);

        mockMvc.perform(get("/api/qa/session/" + id)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.status").value("active"))
                .andExpect(jsonPath("$.data.materials").isArray());
    }

    @Test
    @Order(31)
    void sessionDetail_shouldReturn404_whenNotFound() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/qa/session/99999")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    // ========== 更新会话材料 ==========

    @Test
    @Order(40)
    void updateMaterials_shouldSucceed() throws Exception {
        String token = loginAsAdmin();
        long id = ensureSessionCreated(token);

        String body = """
                {
                    "materialIds": [%d]
                }
                """.formatted(testMaterialId);

        mockMvc.perform(put("/api/qa/session/" + id + "/material")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].materialId").value(testMaterialId));

        // 验证详情中的材料已更新
        mockMvc.perform(get("/api/qa/session/" + id)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.materials").isArray());
    }

    // ========== 消息列表 ==========

    @Test
    @Order(50)
    void messages_shouldReturnEmptyList_forNewSession() throws Exception {
        String token = loginAsAdmin();
        long id = ensureSessionCreated(token);

        mockMvc.perform(get("/api/qa/session/" + id + "/message")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @Order(51)
    void messages_shouldReturn404_whenSessionNotFound() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/qa/session/99999/message")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    // ========== 未认证访问 ==========

    @Test
    @Order(90)
    void qaApi_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/qa/session/page"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    // ========== 清理 ==========

    @AfterAll
    static void cleanup(@Autowired MockMvc mockMvc) throws Exception {
        String loginResp = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andReturn().getResponse().getContentAsString();
        String token = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(loginResp).path("data").path("tokenValue").asText();

        // 会话无删除接口，跳过
        for (Long id : createdMaterialIds) {
            try {
                mockMvc.perform(delete("/api/material/" + id)
                        .header("Authorization", token));
            } catch (Exception ignored) {
            }
        }
        for (Long id : createdFileIds) {
            try {
                mockMvc.perform(delete("/api/system/file/" + id)
                        .header("Authorization", token));
            } catch (Exception ignored) {
            }
        }
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

    private long ensureSessionCreated(String token) throws Exception {
        if (!createdSessionIds.isEmpty()) {
            return createdSessionIds.get(0);
        }
        String body = """
                {
                    "title": "集成测试问答会话",
                    "materialIds": [%d]
                }
                """.formatted(testMaterialId);

        String response = mockMvc.perform(post("/api/qa/session")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).path("data").path("id").asLong();
        createdSessionIds.add(id);
        return id;
    }
}
