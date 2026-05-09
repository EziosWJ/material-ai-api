package cn.ezios.baseapi.modules.qa.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.ezios.baseapi.common.enums.ResponseCode;
import cn.ezios.baseapi.common.exception.BusinessException;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.modules.ai.client.PythonAiClient;
import cn.ezios.baseapi.modules.ai.client.PythonAiClientException;
import cn.ezios.baseapi.modules.ai.dto.AiCallLogCreateRequest;
import cn.ezios.baseapi.modules.ai.dto.PythonAiAskRequest;
import cn.ezios.baseapi.modules.ai.dto.PythonAiAskResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiSourceSegment;
import cn.ezios.baseapi.modules.ai.entity.BizAiCallLog;
import cn.ezios.baseapi.modules.ai.service.AiCallLogService;
import cn.ezios.baseapi.modules.material.entity.BizMaterial;
import cn.ezios.baseapi.modules.material.mapper.BizMaterialMapper;
import cn.ezios.baseapi.modules.qa.dto.QaAskRequest;
import cn.ezios.baseapi.modules.qa.dto.QaSessionCreateRequest;
import cn.ezios.baseapi.modules.qa.dto.QaSessionMaterialUpdateRequest;
import cn.ezios.baseapi.modules.qa.dto.QaSessionPageQuery;
import cn.ezios.baseapi.modules.qa.entity.BizQaMessage;
import cn.ezios.baseapi.modules.qa.entity.BizQaSession;
import cn.ezios.baseapi.modules.qa.entity.BizQaSessionMaterial;
import cn.ezios.baseapi.modules.qa.mapper.BizQaMessageMapper;
import cn.ezios.baseapi.modules.qa.mapper.BizQaSessionMapper;
import cn.ezios.baseapi.modules.qa.mapper.BizQaSessionMaterialMapper;
import cn.ezios.baseapi.modules.qa.service.QaSessionService;
import cn.ezios.baseapi.modules.qa.vo.QaAskVO;
import cn.ezios.baseapi.modules.qa.vo.QaMaterialVO;
import cn.ezios.baseapi.modules.qa.vo.QaMessageVO;
import cn.ezios.baseapi.modules.qa.vo.QaSessionVO;
import cn.ezios.baseapi.modules.qa.vo.QaSourceSegmentVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class QaSessionServiceImpl implements QaSessionService {

    private static final String BUSINESS_TYPE_QA = "qa";
    private static final String ENDPOINT_ASK = "/ask";
    private static final String ROLE_USER = "user";
    private static final String ROLE_ASSISTANT = "assistant";
    private static final String ROLE_SYSTEM = "system";
    private static final String STATUS_ACTIVE = "active";
    private static final String MATERIAL_STATUS_AVAILABLE = "available";
    private static final String CALL_STATUS_SUCCESS = "success";
    private static final String CALL_STATUS_FAILED = "failed";
    private static final String DEFAULT_TITLE = "新问答会话";
    private static final int SUMMARY_LIMIT = 500;

    private final BizQaSessionMapper sessionMapper;
    private final BizQaMessageMapper messageMapper;
    private final BizQaSessionMaterialMapper sessionMaterialMapper;
    private final BizMaterialMapper materialMapper;
    private final PythonAiClient pythonAiClient;
    private final AiCallLogService aiCallLogService;
    private final ObjectMapper objectMapper;

    public QaSessionServiceImpl(BizQaSessionMapper sessionMapper, BizQaMessageMapper messageMapper,
            BizQaSessionMaterialMapper sessionMaterialMapper, BizMaterialMapper materialMapper,
            PythonAiClient pythonAiClient, AiCallLogService aiCallLogService, ObjectMapper objectMapper) {
        this.sessionMapper = sessionMapper;
        this.messageMapper = messageMapper;
        this.sessionMaterialMapper = sessionMaterialMapper;
        this.materialMapper = materialMapper;
        this.pythonAiClient = pythonAiClient;
        this.aiCallLogService = aiCallLogService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QaSessionVO create(QaSessionCreateRequest request) {
        Long userId = currentUserId();
        List<Long> materialIds = normalizeMaterialIds(request.getMaterialIds());
        Map<Long, BizMaterial> materialMap = requireMaterials(userId, materialIds);

        BizQaSession session = new BizQaSession();
        session.setUserId(userId);
        session.setTitle(StringUtils.hasText(request.getTitle()) ? request.getTitle() : DEFAULT_TITLE);
        session.setStatus(STATUS_ACTIVE);
        session.setMessageCount(0);
        sessionMapper.insert(session);
        replaceMaterials(session.getId(), userId, materialIds);
        return toSessionVO(session, materialIds, materialMap);
    }

    @Override
    public PageResult<QaSessionVO> page(QaSessionPageQuery query) {
        Long userId = currentUserId();
        Page<BizQaSession> page = sessionMapper.selectPage(Page.of(query.getPage(), query.getPageSize()),
                new LambdaQueryWrapper<BizQaSession>()
                        .eq(BizQaSession::getUserId, userId)
                        .eq(StringUtils.hasText(query.getStatus()), BizQaSession::getStatus, query.getStatus())
                        .orderByDesc(BizQaSession::getLastMessageTime)
                        .orderByDesc(BizQaSession::getUpdateTime)
                        .orderByDesc(BizQaSession::getId));
        return new PageResult<>(page.getRecords().stream().map(this::toSessionVO).toList(),
                page.getTotal(), query.getPage(), query.getPageSize());
    }

    @Override
    public QaSessionVO detail(Long id) {
        return toSessionVO(requireSession(id, currentUserId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<QaMaterialVO> updateMaterials(Long id, QaSessionMaterialUpdateRequest request) {
        Long userId = currentUserId();
        requireSession(id, userId);
        List<Long> materialIds = normalizeMaterialIds(request.getMaterialIds());
        Map<Long, BizMaterial> materialMap = requireMaterials(userId, materialIds);
        replaceMaterials(id, userId, materialIds);
        return toMaterialVOs(materialIds, materialMap);
    }

    @Override
    public List<QaMessageVO> messages(Long id, boolean includeSystem) {
        Long userId = currentUserId();
        requireSession(id, userId);
        return messageMapper.selectList(new LambdaQueryWrapper<BizQaMessage>()
                        .eq(BizQaMessage::getSessionId, id)
                        .eq(BizQaMessage::getUserId, userId)
                        .ne(!includeSystem, BizQaMessage::getRole, ROLE_SYSTEM)
                        .orderByAsc(BizQaMessage::getCreateTime)
                        .orderByAsc(BizQaMessage::getId))
                .stream()
                .map(this::toMessageVO)
                .toList();
    }

    @Override
    public QaAskVO ask(Long id, QaAskRequest request) {
        Long userId = currentUserId();
        BizQaSession session = requireSession(id, userId);
        List<Long> materialIds = sessionMaterialIds(id, userId);
        List<Long> askMaterialIds = materialIds.isEmpty() ? null : materialIds;

        BizQaMessage userMessage = insertMessage(id, userId, ROLE_USER, request.getQuestion(), null, null, null);
        touchSession(session, 1);

        LocalDateTime startedAt = LocalDateTime.now();
        try {
            PythonAiAskResponse response = pythonAiClient.ask(toAskRequest(request, userId, askMaterialIds));
            LocalDateTime finishedAt = LocalDateTime.now();
            List<PythonAiSourceSegment> sourceSegments = response == null ? null : response.getSourceSegments();
            String answer = response == null || response.getAnswer() == null ? "" : response.getAnswer();
            String sourceSegmentsJson = toSourceSegmentsJson(userId, sourceSegments);
            BizAiCallLog log = createCallLog(userId, id, askMaterialIds, request.getQuestion(), answer,
                    CALL_STATUS_SUCCESS, null, null, null, sourceCount(sourceSegments), startedAt, finishedAt);
            BizQaMessage assistantMessage = insertMessage(id, userId, ROLE_ASSISTANT, answer, sourceSegmentsJson,
                    null, log.getId());
            touchSession(session, 1);

            QaAskVO vo = new QaAskVO();
            vo.setUserMessage(toMessageVO(userMessage));
            vo.setAssistantMessage(toMessageVO(assistantMessage));
            return vo;
        } catch (PythonAiClientException ex) {
            LocalDateTime finishedAt = LocalDateTime.now();
            createCallLog(userId, id, askMaterialIds, request.getQuestion(), null, CALL_STATUS_FAILED,
                    ex.getHttpStatus(), ex.getErrorCode(), ex.getDetail(), 0, startedAt, finishedAt);
            throw new BusinessException(ResponseCode.INTERNAL_ERROR.getCode(), "Python AI 问答调用失败");
        } catch (RuntimeException ex) {
            LocalDateTime finishedAt = LocalDateTime.now();
            createCallLog(userId, id, askMaterialIds, request.getQuestion(), null, CALL_STATUS_FAILED,
                    null, "qa_call_failed", ex.getMessage(), 0, startedAt, finishedAt);
            throw ex;
        }
    }

    private PythonAiAskRequest toAskRequest(QaAskRequest request, Long userId, List<Long> materialIds) {
        PythonAiAskRequest aiRequest = new PythonAiAskRequest();
        aiRequest.setQuery(request.getQuestion());
        aiRequest.setUserId(userId);
        aiRequest.setMaterialIds(materialIds);
        aiRequest.setTopK(request.getTopK());
        return aiRequest;
    }

    private BizQaMessage insertMessage(Long sessionId, Long userId, String role, String content,
            String sourceSegmentsJson, String modelName, Long aiCallLogId) {
        BizQaMessage message = new BizQaMessage();
        message.setSessionId(sessionId);
        message.setUserId(userId);
        message.setRole(role);
        message.setContent(content);
        message.setSourceSegmentsJson(sourceSegmentsJson);
        message.setModelName(modelName);
        message.setAiCallLogId(aiCallLogId);
        messageMapper.insert(message);
        return message;
    }

    private void touchSession(BizQaSession session, int messageDelta) {
        BizQaSession update = new BizQaSession();
        update.setId(session.getId());
        update.setLastMessageTime(LocalDateTime.now());
        update.setMessageCount(Objects.requireNonNullElse(session.getMessageCount(), 0) + messageDelta);
        sessionMapper.updateById(update);
        session.setMessageCount(update.getMessageCount());
    }

    private BizAiCallLog createCallLog(Long userId, Long sessionId, List<Long> materialIds, String question,
            String answer, String status, Integer httpStatus, String errorCode, String errorMessage,
            Integer sourceCount, LocalDateTime startedAt, LocalDateTime finishedAt) {
        AiCallLogCreateRequest logRequest = new AiCallLogCreateRequest();
        logRequest.setUserId(userId);
        logRequest.setBusinessType(BUSINESS_TYPE_QA);
        logRequest.setBusinessId(sessionId);
        logRequest.setEndpoint(ENDPOINT_ASK);
        logRequest.setMaterialIds(materialIds);
        logRequest.setRequestSummary(truncate(question));
        logRequest.setResponseSummary(truncate(answer));
        logRequest.setStatus(status);
        logRequest.setHttpStatus(httpStatus);
        logRequest.setErrorCode(errorCode);
        logRequest.setErrorMessage(truncate(errorMessage));
        logRequest.setSourceCount(sourceCount);
        logRequest.setStartedAt(startedAt);
        logRequest.setFinishedAt(finishedAt);
        logRequest.setDurationMs(Duration.between(startedAt, finishedAt).toMillis());
        return aiCallLogService.create(logRequest);
    }

    private List<Long> normalizeMaterialIds(List<Long> materialIds) {
        if (materialIds == null) {
            return List.of();
        }
        if (materialIds.isEmpty()) {
            throw new BusinessException("materialIds 传入时不能为空数组");
        }
        if (materialIds.stream().anyMatch(Objects::isNull)) {
            throw new BusinessException("materialIds 不能包含空值");
        }
        return new ArrayList<>(new LinkedHashSet<>(materialIds));
    }

    private Map<Long, BizMaterial> requireMaterials(Long userId, List<Long> materialIds) {
        if (materialIds.isEmpty()) {
            return Map.of();
        }
        List<BizMaterial> materials = materialMapper.selectList(new LambdaQueryWrapper<BizMaterial>()
                .eq(BizMaterial::getUserId, userId)
                .in(BizMaterial::getId, materialIds));
        Map<Long, BizMaterial> materialMap = materials.stream()
                .collect(Collectors.toMap(BizMaterial::getId, Function.identity()));
        if (materialMap.size() != materialIds.size()) {
            throw new BusinessException("材料不存在或无权限");
        }
        if (materials.stream().anyMatch(material -> !MATERIAL_STATUS_AVAILABLE.equals(material.getStatus()))) {
            throw new BusinessException("材料未处理完成，不能用于问答");
        }
        return materialMap;
    }

    private void replaceMaterials(Long sessionId, Long userId, List<Long> materialIds) {
        sessionMaterialMapper.delete(new LambdaQueryWrapper<BizQaSessionMaterial>()
                .eq(BizQaSessionMaterial::getSessionId, sessionId)
                .eq(BizQaSessionMaterial::getUserId, userId));
        for (Long materialId : materialIds) {
            BizQaSessionMaterial relation = new BizQaSessionMaterial();
            relation.setSessionId(sessionId);
            relation.setMaterialId(materialId);
            relation.setUserId(userId);
            sessionMaterialMapper.insert(relation);
        }
    }

    private BizQaSession requireSession(Long id, Long userId) {
        BizQaSession session = sessionMapper.selectOne(new LambdaQueryWrapper<BizQaSession>()
                .eq(BizQaSession::getId, id)
                .eq(BizQaSession::getUserId, userId));
        if (session == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        return session;
    }

    private List<Long> sessionMaterialIds(Long sessionId, Long userId) {
        return sessionMaterialMapper.selectList(new LambdaQueryWrapper<BizQaSessionMaterial>()
                        .eq(BizQaSessionMaterial::getSessionId, sessionId)
                        .eq(BizQaSessionMaterial::getUserId, userId)
                        .orderByAsc(BizQaSessionMaterial::getId))
                .stream()
                .map(BizQaSessionMaterial::getMaterialId)
                .toList();
    }

    private QaSessionVO toSessionVO(BizQaSession session) {
        List<Long> materialIds = sessionMaterialIds(session.getId(), session.getUserId());
        Map<Long, BizMaterial> materialMap = loadMaterialMap(materialIds);
        return toSessionVO(session, materialIds, materialMap);
    }

    private QaSessionVO toSessionVO(BizQaSession session, List<Long> materialIds, Map<Long, BizMaterial> materialMap) {
        QaSessionVO vo = new QaSessionVO();
        BeanUtils.copyProperties(session, vo);
        vo.setMaterials(toMaterialVOs(materialIds, materialMap));
        return vo;
    }

    private List<QaMaterialVO> toMaterialVOs(Collection<Long> materialIds, Map<Long, BizMaterial> materialMap) {
        return materialIds.stream().map(materialId -> {
            QaMaterialVO vo = new QaMaterialVO();
            vo.setMaterialId(materialId);
            BizMaterial material = materialMap.get(materialId);
            if (material != null) {
                vo.setTitle(material.getTitle());
                vo.setOriginalFilename(material.getOriginalFilename());
            }
            return vo;
        }).toList();
    }

    private QaMessageVO toMessageVO(BizQaMessage message) {
        QaMessageVO vo = new QaMessageVO();
        BeanUtils.copyProperties(message, vo);
        vo.setSourceSegments(parseSourceSegments(message.getSourceSegmentsJson()));
        return vo;
    }

    private String toSourceSegmentsJson(Long userId, List<PythonAiSourceSegment> sourceSegments) {
        if (sourceSegments == null || sourceSegments.isEmpty()) {
            return null;
        }
        Map<Long, BizMaterial> materialMap = loadMaterialMap(userId, sourceSegments.stream()
                .map(PythonAiSourceSegment::getMaterialId)
                .filter(Objects::nonNull)
                .toList());
        List<QaSourceSegmentVO> vos = sourceSegments.stream().map(segment -> {
            QaSourceSegmentVO vo = new QaSourceSegmentVO();
            vo.setText(segment.getText());
            vo.setMaterialId(segment.getMaterialId());
            vo.setSegmentIndex(segment.getSegmentIndex());
            vo.setScore(segment.getScore());
            BizMaterial material = materialMap.get(segment.getMaterialId());
            if (material != null) {
                vo.setMaterialTitle(material.getTitle());
                vo.setOriginalFilename(material.getOriginalFilename());
            }
            return vo;
        }).toList();
        try {
            return objectMapper.writeValueAsString(vos);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    private List<QaSourceSegmentVO> parseSourceSegments(String sourceSegmentsJson) {
        if (!StringUtils.hasText(sourceSegmentsJson)) {
            return List.of();
        }
        try {
            return objectMapper.readValue(sourceSegmentsJson, new TypeReference<List<QaSourceSegmentVO>>() {
            });
        } catch (JsonProcessingException ex) {
            return List.of();
        }
    }

    private Map<Long, BizMaterial> loadMaterialMap(List<Long> materialIds) {
        if (materialIds == null || materialIds.isEmpty()) {
            return Map.of();
        }
        return materialMapper.selectList(new LambdaQueryWrapper<BizMaterial>()
                        .in(BizMaterial::getId, materialIds))
                .stream()
                .collect(Collectors.toMap(BizMaterial::getId, Function.identity(), (left, right) -> left));
    }

    private Map<Long, BizMaterial> loadMaterialMap(Long userId, List<Long> materialIds) {
        if (materialIds == null || materialIds.isEmpty()) {
            return Map.of();
        }
        return materialMapper.selectList(new LambdaQueryWrapper<BizMaterial>()
                        .eq(BizMaterial::getUserId, userId)
                        .in(BizMaterial::getId, materialIds))
                .stream()
                .collect(Collectors.toMap(BizMaterial::getId, Function.identity(), (left, right) -> left));
    }

    private int sourceCount(List<PythonAiSourceSegment> sourceSegments) {
        return sourceSegments == null ? 0 : sourceSegments.size();
    }

    private String truncate(String value) {
        if (!StringUtils.hasText(value) || value.length() <= SUMMARY_LIMIT) {
            return value;
        }
        return value.substring(0, SUMMARY_LIMIT);
    }

    private Long currentUserId() {
        return StpUtil.getLoginIdAsLong();
    }
}
