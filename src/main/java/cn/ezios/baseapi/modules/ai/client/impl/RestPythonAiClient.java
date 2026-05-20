package cn.ezios.baseapi.modules.ai.client.impl;

import cn.ezios.baseapi.common.exception.BusinessException;
import cn.ezios.baseapi.modules.ai.client.PythonAiClient;
import cn.ezios.baseapi.modules.ai.client.PythonAiClientException;
import cn.ezios.baseapi.modules.ai.dto.PythonAiAskRequest;
import cn.ezios.baseapi.modules.ai.dto.PythonAiAskResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiErrorResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiGenerateRequest;
import cn.ezios.baseapi.modules.ai.dto.PythonAiGenerateResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiHealthResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiMaterialProcessResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiSourceSegment;
import cn.ezios.baseapi.modules.ai.dto.PythonAiVectorDeleteResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Python AI 服务客户端的 REST 实现。
 * <p>通过 {@link RestClient} 调用 Python AI 服务的各接口，并将 Python 侧的响应格式转换为 Java 业务 DTO。
 * 内部定义了与 Python 服务通信的 Wire 类，负责字段映射和类型转换。</p>
 */
@Component
public class RestPythonAiClient implements PythonAiClient {

    private static final String ENDPOINT_HEALTH = "/health";
    private static final String ENDPOINT_MATERIAL_PROCESS = "/materials/process";
    private static final String ENDPOINT_GENERATE = "/generate";
    private static final String ENDPOINT_ASK = "/ask";

    /** 预配置的 RestClient，已绑定 Python AI 服务基础 URL */
    private final RestClient pythonAiRestClient;
    private final ObjectMapper objectMapper;

    public RestPythonAiClient(RestClient pythonAiRestClient, ObjectMapper objectMapper) {
        this.pythonAiRestClient = pythonAiRestClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public PythonAiHealthResponse health(boolean deep) {
        return execute(() -> {
            PythonHealthWireResponse wire = pythonAiRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(ENDPOINT_HEALTH).queryParam("deep", deep).build())
                    .retrieve()
                    .onStatus(status -> status.isError(), (request, response) -> {
                        throw toClientException(response);
                    })
                    .body(PythonHealthWireResponse.class);
            PythonAiHealthResponse response = new PythonAiHealthResponse();
            if (wire != null) {
                response.setStatus(wire.getStatus());
                response.setDetail(wire.getDetail());
            }
            return response;
        });
    }

    @Override
    public PythonAiMaterialProcessResponse processMaterial(Long userId, Long materialId, Resource file) {
        requireId(userId, "userId");
        requireId(materialId, "materialId");
        if (file == null) {
            throw new BusinessException("材料文件不能为空");
        }
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);
        body.add("material_id", materialId.toString());
        body.add("user_id", userId.toString());

        return execute(() -> {
            PythonMaterialProcessWireResponse wire = pythonAiRestClient.post()
                    .uri(ENDPOINT_MATERIAL_PROCESS)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .onStatus(status -> status.isError(), (request, response) -> {
                        throw toClientException(response);
                    })
                    .body(PythonMaterialProcessWireResponse.class);
            PythonAiMaterialProcessResponse response = new PythonAiMaterialProcessResponse();
            if (wire != null) {
                response.setDeletedCount(nonNegative(wire.getDeletedCount()));
                response.setSegmentCount(nonNegative(wire.getChunkCount()));
            }
            return response;
        });
    }

    @Override
    public PythonAiVectorDeleteResponse deleteMaterialVectors(Long userId, Long materialId) {
        requireId(userId, "userId");
        requireId(materialId, "materialId");
        return execute(() -> {
            PythonVectorDeleteWireResponse wire = pythonAiRestClient.delete()
                    .uri(uriBuilder -> uriBuilder.path("/materials/{materialId}/vectors")
                            .queryParam("user_id", userId)
                            .build(materialId))
                    .retrieve()
                    .onStatus(status -> status.isError(), (request, response) -> {
                        throw toClientException(response);
                    })
                    .body(PythonVectorDeleteWireResponse.class);
            PythonAiVectorDeleteResponse response = new PythonAiVectorDeleteResponse();
            response.setDeletedCount(wire == null ? 0 : nonNegative(wire.getDeletedCount()));
            return response;
        });
    }

    @Override
    public PythonAiGenerateResponse generate(PythonAiGenerateRequest request) {
        validateGenerateRequest(request);
        return execute(() -> {
            PythonGenerateWireResponse wire = pythonAiRestClient.post()
                    .uri(ENDPOINT_GENERATE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(toGenerateBody(request))
                    .retrieve()
                    .onStatus(status -> status.isError(), (httpRequest, response) -> {
                        throw toClientException(response);
                    })
                    .body(PythonGenerateWireResponse.class);
            PythonAiGenerateResponse response = new PythonAiGenerateResponse();
            if (wire != null) {
                response.setGeneratedText(wire.getGeneratedText());
                response.setSourceSegments(toSourceSegments(wire.getSources()));
            }
            return response;
        });
    }

    @Override
    public PythonAiAskResponse ask(PythonAiAskRequest request) {
        validateAskRequest(request);
        return execute(() -> {
            PythonAskWireResponse wire = pythonAiRestClient.post()
                    .uri(ENDPOINT_ASK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(toAskBody(request))
                    .retrieve()
                    .onStatus(status -> status.isError(), (httpRequest, response) -> {
                        throw toClientException(response);
                    })
                    .body(PythonAskWireResponse.class);
            PythonAiAskResponse response = new PythonAiAskResponse();
            if (wire != null) {
                response.setAnswer(wire.getAnswer());
                response.setSourceSegments(toSourceSegments(wire.getSources()));
            }
            return response;
        });
    }

    /** 校验内容生成请求参数 */
    private void validateGenerateRequest(PythonAiGenerateRequest request) {
        if (request == null) {
            throw new BusinessException("生成请求不能为空");
        }
        if (!StringUtils.hasText(request.getType())) {
            throw new BusinessException("写作类型不能为空");
        }
        if (!StringUtils.hasText(request.getTopic())) {
            throw new BusinessException("写作主题不能为空");
        }
        requireId(request.getUserId(), "userId");
        if ("polished".equals(request.getType()) && !StringUtils.hasText(request.getContent())) {
            throw new BusinessException("润色内容不能为空");
        }
        validateMaterialIds(request.getMaterialIds());
        validateTopK(request.getTopK());
    }

    /** 校验问答请求参数 */
    private void validateAskRequest(PythonAiAskRequest request) {
        if (request == null) {
            throw new BusinessException("问答请求不能为空");
        }
        if (!StringUtils.hasText(request.getQuery())) {
            throw new BusinessException("问题不能为空");
        }
        requireId(request.getUserId(), "userId");
        validateMaterialIds(request.getMaterialIds());
        validateTopK(request.getTopK());
    }

    private void validateMaterialIds(List<Long> materialIds) {
        if (materialIds != null && materialIds.isEmpty()) {
            throw new BusinessException("materialIds 传入时不能为空数组");
        }
    }

    private void validateTopK(Integer topK) {
        if (topK != null && (topK < 1 || topK > 20)) {
            throw new BusinessException("topK 范围必须为 1-20");
        }
    }

    private void requireId(Long id, String fieldName) {
        if (id == null) {
            throw new BusinessException(fieldName + " 不能为空");
        }
    }

    /** 将生成请求转换为 Python 服务所需的请求体 */
    private Map<String, Object> toGenerateBody(PythonAiGenerateRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", request.getType());
        body.put("topic", request.getTopic());
        body.put("user_id", request.getUserId().toString());
        putIfText(body, "content", request.getContent());
        putIfNotNull(body, "material_ids", toStringIds(request.getMaterialIds()));
        putIfNotNull(body, "top_k", request.getTopK());
        return body;
    }

    /** 将问答请求转换为 Python 服务所需的请求体 */
    private Map<String, Object> toAskBody(PythonAiAskRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("query", request.getQuery());
        body.put("user_id", request.getUserId().toString());
        putIfNotNull(body, "material_ids", toStringIds(request.getMaterialIds()));
        putIfNotNull(body, "top_k", request.getTopK());
        return body;
    }

    private void putIfText(Map<String, Object> body, String key, String value) {
        if (StringUtils.hasText(value)) {
            body.put(key, value);
        }
    }

    private void putIfNotNull(Map<String, Object> body, String key, Object value) {
        if (value != null) {
            body.put(key, value);
        }
    }

    private List<String> toStringIds(List<Long> ids) {
        if (ids == null) {
            return null;
        }
        return ids.stream().map(String::valueOf).toList();
    }

    /** 将 Python 服务的来源片段 Wire 对象转换为业务 DTO */
    private List<PythonAiSourceSegment> toSourceSegments(List<PythonSourceSegmentWireResponse> sources) {
        if (sources == null) {
            return List.of();
        }
        return sources.stream().map(source -> {
            PythonAiSourceSegment segment = new PythonAiSourceSegment();
            segment.setText(source.getText());
            segment.setMaterialId(parseLong(source.getMaterialId()));
            segment.setSegmentIndex(nonNegativeOrNull(source.getChunkIndex()));
            segment.setScore(source.getScore());
            return segment;
        }).toList();
    }

    private Long parseLong(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer nonNegative(Integer value) {
        return value == null || value < 0 ? 0 : value;
    }

    private Integer nonNegativeOrNull(Integer value) {
        return value == null || value < 0 ? null : value;
    }

    /** 统一执行 Python AI 调用，将 RestClientException 包装为 PythonAiClientException */
    private <T> T execute(PythonAiCall<T> call) {
        try {
            return call.invoke();
        } catch (PythonAiClientException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new PythonAiClientException(null, "python_ai_call_failed", ex.getMessage());
        }
    }

    /** 解析 Python 服务的错误响应并转换为 PythonAiClientException */
    private PythonAiClientException toClientException(ClientHttpResponse response) throws IOException {
        String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        PythonAiErrorResponse error = parseError(body);
        String errorCode = StringUtils.hasText(error.getError()) ? error.getError() : "python_ai_error";
        String detail = StringUtils.hasText(error.getDetail()) ? error.getDetail() : body;
        return new PythonAiClientException(response.getStatusCode().value(), errorCode, detail);
    }

    private PythonAiErrorResponse parseError(String body) {
        if (!StringUtils.hasText(body)) {
            return new PythonAiErrorResponse();
        }
        try {
            return objectMapper.readValue(body, PythonAiErrorResponse.class);
        } catch (JsonProcessingException ex) {
            PythonAiErrorResponse error = new PythonAiErrorResponse();
            error.setDetail(body);
            return error;
        }
    }

    /** Python AI 调用的函数式接口，用于统一异常处理 */
    @FunctionalInterface
    private interface PythonAiCall<T> {

        T invoke();
    }

    /** Python 健康检查接口的 Wire 响应 */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    private static class PythonHealthWireResponse {

        private String status;

        private String detail;
    }

    /** Python 材料处理接口的 Wire 响应 */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    private static class PythonMaterialProcessWireResponse {

        @JsonProperty("deleted_count")
        private Integer deletedCount;

        @JsonProperty("chunk_count")
        private Integer chunkCount;
    }

    /** Python 向量删除接口的 Wire 响应 */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    private static class PythonVectorDeleteWireResponse {

        @JsonProperty("deleted_count")
        private Integer deletedCount;
    }

    /** Python 内容生成接口的 Wire 响应 */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    private static class PythonGenerateWireResponse {

        @JsonProperty("generated_text")
        private String generatedText;

        private List<PythonSourceSegmentWireResponse> sources;
    }

    /** Python 问答接口的 Wire 响应 */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    private static class PythonAskWireResponse {

        private String answer;

        private List<PythonSourceSegmentWireResponse> sources;
    }

    /** Python 来源片段的 Wire 响应，使用 snake_case 字段名 */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    private static class PythonSourceSegmentWireResponse {

        private String text;

        @JsonProperty("material_id")
        private String materialId;

        @JsonProperty("chunk_index")
        private Integer chunkIndex;

        private java.math.BigDecimal score;
    }
}
