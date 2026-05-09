package cn.ezios.baseapi.admin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class QaAskEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final List<Long> createdMaterialIds = new ArrayList<>();
    private static final List<Long> createdFileIds = new ArrayList<>();
    private static Long testMaterialId;
    private static Long testSessionId;

    // ========== 上传并处理材料 ==========

    @Test
    @Order(1)
    void uploadAndCreateMaterial() throws Exception {
        String token = loginAsAdmin();

        // 上传一个有实际内容的文件，便于 RAG 检索
        MockMultipartFile file = new MockMultipartFile(
                "file", "问答测试材料.txt", "text/plain",
                """
                本地化智能材料写作平台是一个基于 Java 和 Python 双服务架构的内容创作辅助系统。
                Java 后端负责业务管理，包括用户权限、材料管理、写作任务和问答记录。
                Python AI 服务负责材料解析、向量检索和大模型生成。
                系统支持材料上传、自动处理、片段切分、向量存储、智能问答和内容生成等功能。
                用户可以上传宣传文档，系统会自动解析并建立向量索引，然后基于材料内容进行智能问答。
                """.getBytes(StandardCharsets.UTF_8));

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
                    "title": "智能写作平台介绍",
                    "originalFilename": "%s",
                    "fileId": %d,
                    "fileType": "txt",
                    "fileSize": 500,
                    "storagePath": "%s",
                    "status": "processing"
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

    @Test
    @Order(2)
    void processMaterial_shouldSucceed() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(post("/api/material/" + testMaterialId + "/process")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("available"))
                .andExpect(jsonPath("$.data.segmentCount").isNumber());
    }

    // ========== 创建问答会话 ==========

    @Test
    @Order(10)
    void createSession_shouldSucceed() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "title": "端到端测试问答",
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
                .andExpect(jsonPath("$.data.status").value("active"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        testSessionId = objectMapper.readTree(response).path("data").path("id").asLong();
    }

    // ========== AI 问答 ==========

    @Test
    @Order(20)
    void ask_shouldReturnAnswer_withSourceSegments() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "question": "这个系统的主要功能是什么？"
                }
                """;

        String response = mockMvc.perform(post("/api/qa/session/" + testSessionId + "/ask")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userMessage").exists())
                .andExpect(jsonPath("$.data.userMessage.role").value("user"))
                .andExpect(jsonPath("$.data.userMessage.content").value("这个系统的主要功能是什么？"))
                .andExpect(jsonPath("$.data.assistantMessage").exists())
                .andExpect(jsonPath("$.data.assistantMessage.role").value("assistant"))
                .andExpect(jsonPath("$.data.assistantMessage.content").isNotEmpty())
                .andExpect(jsonPath("$.data.assistantMessage.aiCallLogId").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 验证来源片段（如果 Python 服务返回了 sources）
        JsonNode assistantMsg = objectMapper.readTree(response).path("data").path("assistantMessage");
        if (assistantMsg.has("sourceSegments") && assistantMsg.path("sourceSegments").isArray()
                && !assistantMsg.path("sourceSegments").isEmpty()) {
            JsonNode firstSource = assistantMsg.path("sourceSegments").get(0);
            assert firstSource.has("text") : "来源片段应包含 text";
            assert firstSource.has("materialId") : "来源片段应包含 materialId";
            assert firstSource.has("score") : "来源片段应包含 score";
        }
    }

    @Test
    @Order(21)
    void ask_shouldFail_whenQuestionBlank() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "question": ""
                }
                """;

        mockMvc.perform(post("/api/qa/session/" + testSessionId + "/ask")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @Order(22)
    void ask_shouldReturn404_whenSessionNotFound() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "question": "测试问题"
                }
                """;

        mockMvc.perform(post("/api/qa/session/99999/ask")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    // ========== 验证消息记录 ==========

    @Test
    @Order(30)
    void messages_shouldContainUserAndAssistantMessages() throws Exception {
        String token = loginAsAdmin();

        String response = mockMvc.perform(get("/api/qa/session/" + testSessionId + "/message")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode messages = objectMapper.readTree(response).path("data");
        assert messages.size() >= 2 : "应至少有 2 条消息（user + assistant）";

        // 最后两条应该是 user 和 assistant
        JsonNode lastTwo = messages;
        boolean hasUser = false;
        boolean hasAssistant = false;
        for (JsonNode msg : lastTwo) {
            if ("user".equals(msg.path("role").asText())) {
                hasUser = true;
            }
            if ("assistant".equals(msg.path("role").asText())) {
                hasAssistant = true;
            }
        }
        assert hasUser : "应包含 user 消息";
        assert hasAssistant : "应包含 assistant 消息";
    }

    @Test
    @Order(31)
    void sessionDetail_shouldShowUpdatedMessageCount() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/qa/session/" + testSessionId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testSessionId))
                .andExpect(jsonPath("$.data.messageCount").value(2));
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
}
