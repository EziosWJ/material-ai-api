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
class MaterialModuleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final List<Long> createdMaterialIds = new ArrayList<>();
    private static final List<Long> createdFileIds = new ArrayList<>();
    private static Long testFileId;
    private static String testStoragePath;
    private static String testOriginalFilename;
    private static String testFileMd5;

    // ========== 文件上传（材料创建前置） ==========

    @Test
    @Order(0)
    void uploadTestFile() throws Exception {
        String token = loginAsAdmin();

        MockMultipartFile file = new MockMultipartFile(
                "file", "测试材料.txt", "text/plain",
                "这是用于材料模块集成测试的文件内容。".getBytes(StandardCharsets.UTF_8));

        String response = mockMvc.perform(multipart("/api/system/file/upload")
                        .file(file)
                        .param("businessModule", "material")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode data = objectMapper.readTree(response).path("data");
        testFileId = data.path("id").asLong();
        testStoragePath = data.path("storagePath").asText();
        testOriginalFilename = data.path("originalName").asText();
        testFileMd5 = data.path("fileMd5").asText();
        createdFileIds.add(testFileId);
    }

    // ========== 创建材料 ==========

    @Test
    @Order(10)
    void createMaterial_shouldSucceed() throws Exception {
        String token = loginAsAdmin();
        ensureFileUploaded(token);

        String body = """
                {
                    "title": "集成测试材料",
                    "originalFilename": "%s",
                    "fileId": %d,
                    "fileType": "txt",
                    "fileSize": 100,
                    "fileMd5": "%s",
                    "storagePath": "%s"
                }
                """.formatted(testOriginalFilename, testFileId, testFileMd5, testStoragePath);

        String response = mockMvc.perform(post("/api/material")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.title").value("集成测试材料"))
                .andExpect(jsonPath("$.data.status").value("processing"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).path("data").path("id").asLong();
        createdMaterialIds.add(id);
    }

    @Test
    @Order(11)
    void createMaterial_shouldFail_whenMissingTitle() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "originalFilename": "test.txt",
                    "fileId": %d,
                    "storagePath": "/tmp/test.txt"
                }
                """.formatted(testFileId);

        mockMvc.perform(post("/api/material")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.title").exists());
    }

    @Test
    @Order(12)
    void createMaterial_shouldFail_whenMissingFileId() throws Exception {
        String token = loginAsAdmin();

        String body = """
                {
                    "title": "缺少文件ID",
                    "originalFilename": "test.txt",
                    "storagePath": "/tmp/test.txt"
                }
                """;

        mockMvc.perform(post("/api/material")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.fileId").exists());
    }

    // ========== 分页查询 ==========

    @Test
    @Order(20)
    void materialPage_shouldReturnSuccess() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/material/page")
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
    void materialPage_shouldContainAllFields() throws Exception {
        String token = loginAsAdmin();

        String response = mockMvc.perform(get("/api/material/page")
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("pageSize", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode records = objectMapper.readTree(response).path("data").path("records");
        if (records.isArray() && !records.isEmpty()) {
            JsonNode first = records.get(0);
            assert first.has("id") : "应包含 id";
            assert first.has("userId") : "应包含 userId";
            assert first.has("title") : "应包含 title";
            assert first.has("originalFilename") : "应包含 originalFilename";
            assert first.has("fileId") : "应包含 fileId";
            assert first.has("status") : "应包含 status";
            assert first.has("segmentCount") : "应包含 segmentCount";
            assert first.has("createTime") : "应包含 createTime";
        }
    }

    @Test
    @Order(22)
    void materialPage_shouldFilterByTitle() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/material/page")
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("pageSize", "10")
                        .param("title", "不存在的标题xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.records").isEmpty());
    }

    @Test
    @Order(23)
    void materialPage_shouldFilterByStatus() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/material/page")
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("pageSize", "10")
                        .param("status", "processing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    // ========== 详情 ==========

    @Test
    @Order(30)
    void materialDetail_shouldReturnMaterial() throws Exception {
        String token = loginAsAdmin();
        long id = ensureMaterialCreated(token);

        mockMvc.perform(get("/api/material/" + id)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.title").value("集成测试材料"))
                .andExpect(jsonPath("$.data.userId").isNumber())
                .andExpect(jsonPath("$.data.status").exists())
                .andExpect(jsonPath("$.data.segmentCount").isNumber());
    }

    @Test
    @Order(31)
    void materialDetail_shouldReturn404_whenNotFound() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/material/99999")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("数据不存在"));
    }

    // ========== 更新 ==========

    @Test
    @Order(40)
    void updateMaterial_shouldSucceed() throws Exception {
        String token = loginAsAdmin();
        long id = ensureMaterialCreated(token);

        String body = """
                {
                    "title": "已修改材料标题",
                    "remark": "集成测试修改"
                }
                """;

        mockMvc.perform(put("/api/material/" + id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证修改成功
        mockMvc.perform(get("/api/material/" + id)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("已修改材料标题"))
                .andExpect(jsonPath("$.data.remark").value("集成测试修改"));
    }

    // ========== 删除 ==========

    @Test
    @Order(50)
    void deleteMaterial_shouldSucceed() throws Exception {
        String token = loginAsAdmin();
        ensureFileUploaded(token);

        // 创建一个专门用于删除的材料
        String body = """
                {
                    "title": "待删除材料",
                    "originalFilename": "%s",
                    "fileId": %d,
                    "fileType": "txt",
                    "fileSize": 100,
                    "storagePath": "%s"
                }
                """.formatted(testOriginalFilename, testFileId, testStoragePath);

        String createResponse = mockMvc.perform(post("/api/material")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(createResponse).path("data").path("id").asLong();

        // 删除
        mockMvc.perform(delete("/api/material/" + id)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证已删除（逻辑删除，详情返回 404）
        mockMvc.perform(get("/api/material/" + id)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @Order(51)
    void deleteMaterial_shouldReturn404_whenNotFound() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(delete("/api/material/99999")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    // ========== 批量删除 ==========

    @Test
    @Order(60)
    void batchDeleteMaterial_shouldSucceed() throws Exception {
        String token = loginAsAdmin();
        ensureFileUploaded(token);

        // 创建两个材料用于批量删除
        long id1 = createMaterialDirectly(token, "批量删除材料1");
        long id2 = createMaterialDirectly(token, "批量删除材料2");

        String batchBody = "{\"ids\": [" + id1 + ", " + id2 + "]}";
        mockMvc.perform(post("/api/material/batch-delete")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(batchBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证均已删除
        mockMvc.perform(get("/api/material/" + id1)
                        .header("Authorization", token))
                .andExpect(jsonPath("$.code").value(404));
        mockMvc.perform(get("/api/material/" + id2)
                        .header("Authorization", token))
                .andExpect(jsonPath("$.code").value(404));
    }

    // ========== 材料处理记录 ==========

    @Test
    @Order(70)
    void processRecordPage_shouldReturnSuccess() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/material/process-record/page")
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
    @Order(71)
    void processRecordPage_shouldContainAllFields() throws Exception {
        String token = loginAsAdmin();

        String response = mockMvc.perform(get("/api/material/process-record/page")
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
            assert first.has("materialId") : "应包含 materialId";
            assert first.has("userId") : "应包含 userId";
            assert first.has("processType") : "应包含 processType";
            assert first.has("status") : "应包含 status";
            assert first.has("segmentCount") : "应包含 segmentCount";
            assert first.has("startedAt") : "应包含 startedAt";
            assert first.has("finishedAt") : "应包含 finishedAt";
            assert first.has("durationMs") : "应包含 durationMs";
        }
    }

    @Test
    @Order(72)
    void processRecordPage_shouldFilterByMaterialId() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/material/process-record/page")
                        .header("Authorization", token)
                        .param("page", "1")
                        .param("pageSize", "10")
                        .param("materialId", "99999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @Order(80)
    void processRecordDetail_shouldReturn404_whenNotFound() throws Exception {
        String token = loginAsAdmin();

        mockMvc.perform(get("/api/material/process-record/99999")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    // ========== 未认证访问 ==========

    @Test
    @Order(90)
    void materialApi_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/material/page"))
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

    private void ensureFileUploaded(String token) throws Exception {
        if (testFileId != null) {
            return;
        }
        uploadTestFile();
    }

    private long ensureMaterialCreated(String token) throws Exception {
        if (!createdMaterialIds.isEmpty()) {
            return createdMaterialIds.get(0);
        }
        ensureFileUploaded(token);
        long id = createMaterialDirectly(token, "集成测试材料");
        return id;
    }

    private long createMaterialDirectly(String token, String title) throws Exception {
        String body = """
                {
                    "title": "%s",
                    "originalFilename": "%s",
                    "fileId": %d,
                    "fileType": "txt",
                    "fileSize": 100,
                    "storagePath": "%s"
                }
                """.formatted(title, testOriginalFilename, testFileId, testStoragePath);

        String response = mockMvc.perform(post("/api/material")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).path("data").path("id").asLong();
        createdMaterialIds.add(id);
        return id;
    }
}
