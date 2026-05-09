package cn.ezios.baseapi.modules.writing.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.ezios.baseapi.common.enums.ResponseCode;
import cn.ezios.baseapi.common.exception.BusinessException;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.modules.ai.client.PythonAiClient;
import cn.ezios.baseapi.modules.ai.client.PythonAiClientException;
import cn.ezios.baseapi.modules.ai.dto.AiCallLogCreateRequest;
import cn.ezios.baseapi.modules.ai.dto.PythonAiGenerateRequest;
import cn.ezios.baseapi.modules.ai.dto.PythonAiGenerateResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiSourceSegment;
import cn.ezios.baseapi.modules.ai.entity.BizAiCallLog;
import cn.ezios.baseapi.modules.ai.service.AiCallLogService;
import cn.ezios.baseapi.modules.material.entity.BizMaterial;
import cn.ezios.baseapi.modules.material.mapper.BizMaterialMapper;
import cn.ezios.baseapi.modules.writing.dto.WritingTaskCreateRequest;
import cn.ezios.baseapi.modules.writing.dto.WritingTaskPageQuery;
import cn.ezios.baseapi.modules.writing.entity.BizWritingResult;
import cn.ezios.baseapi.modules.writing.entity.BizWritingTask;
import cn.ezios.baseapi.modules.writing.entity.BizWritingTaskMaterial;
import cn.ezios.baseapi.modules.writing.mapper.BizWritingResultMapper;
import cn.ezios.baseapi.modules.writing.mapper.BizWritingTaskMapper;
import cn.ezios.baseapi.modules.writing.mapper.BizWritingTaskMaterialMapper;
import cn.ezios.baseapi.modules.writing.service.WritingTaskService;
import cn.ezios.baseapi.modules.writing.vo.WritingResultVO;
import cn.ezios.baseapi.modules.writing.vo.WritingSourceSegmentVO;
import cn.ezios.baseapi.modules.writing.vo.WritingTaskVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class WritingTaskServiceImpl implements WritingTaskService {

    private static final Set<String> SUPPORTED_WRITING_TYPES = Set.of("outline", "draft", "polished", "title");
    private static final String BUSINESS_TYPE_WRITING = "writing";
    private static final String ENDPOINT_GENERATE = "/generate";
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_RUNNING = "running";
    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_FAILED = "failed";
    private static final String MATERIAL_STATUS_AVAILABLE = "available";
    private static final int FIRST_VERSION = 1;

    private final BizWritingTaskMapper taskMapper;
    private final BizWritingTaskMaterialMapper taskMaterialMapper;
    private final BizWritingResultMapper resultMapper;
    private final BizMaterialMapper materialMapper;
    private final PythonAiClient pythonAiClient;
    private final AiCallLogService aiCallLogService;
    private final ObjectMapper objectMapper;

    public WritingTaskServiceImpl(BizWritingTaskMapper taskMapper,
                                  BizWritingTaskMaterialMapper taskMaterialMapper,
                                  BizWritingResultMapper resultMapper,
                                  BizMaterialMapper materialMapper,
                                  PythonAiClient pythonAiClient,
                                  AiCallLogService aiCallLogService,
                                  ObjectMapper objectMapper) {
        this.taskMapper = taskMapper;
        this.taskMaterialMapper = taskMaterialMapper;
        this.resultMapper = resultMapper;
        this.materialMapper = materialMapper;
        this.pythonAiClient = pythonAiClient;
        this.aiCallLogService = aiCallLogService;
        this.objectMapper = objectMapper;
    }

    @Override
    public WritingTaskVO create(WritingTaskCreateRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        validateRequest(request);
        List<Long> materialIds = normalizeMaterialIds(request.getMaterialIds());
        Map<Long, BizMaterial> materialMap = loadMaterialMap(userId, materialIds);

        BizWritingTask task = createPendingTask(userId, request);
        insertTaskMaterials(task.getId(), userId, materialIds);
        markRunning(task);

        LocalDateTime startedAt = task.getStartedAt();
        try {
            PythonAiGenerateResponse response = pythonAiClient.generate(toGenerateRequest(userId, request, materialIds));
            List<WritingSourceSegmentVO> sourceSegments = toSourceSegmentSnapshot(userId, response.getSourceSegments(),
                    materialMap);
            BizAiCallLog callLog = createCallLog(userId, task.getId(), materialIds, request, response,
                    STATUS_SUCCESS, null, null, startedAt);
            BizWritingResult result = createResult(userId, task.getId(), response, sourceSegments, callLog);
            markFinished(task.getId(), STATUS_SUCCESS, null);
            task = taskMapper.selectById(task.getId());
            return toVO(task, materialIds, result);
        } catch (RuntimeException ex) {
            createCallLog(userId, task.getId(), materialIds, request, null, STATUS_FAILED, ex, null, startedAt);
            markFinished(task.getId(), STATUS_FAILED, ex.getMessage());
            task = taskMapper.selectById(task.getId());
            return toVO(task, materialIds, null);
        }
    }

    @Override
    public PageResult<WritingTaskVO> page(WritingTaskPageQuery query) {
        Long userId = StpUtil.getLoginIdAsLong();
        Page<BizWritingTask> page = taskMapper.selectPage(Page.of(query.getPage(), query.getPageSize()),
                new LambdaQueryWrapper<BizWritingTask>()
                        .eq(BizWritingTask::getUserId, userId)
                        .eq(StringUtils.hasText(query.getWritingType()), BizWritingTask::getWritingType,
                                query.getWritingType())
                        .eq(StringUtils.hasText(query.getStatus()), BizWritingTask::getStatus, query.getStatus())
                        .like(StringUtils.hasText(query.getTitle()), BizWritingTask::getTitle, query.getTitle())
                        .orderByDesc(BizWritingTask::getCreateTime)
                        .orderByDesc(BizWritingTask::getId));
        List<WritingTaskVO> records = page.getRecords().stream()
                .map(task -> toVO(task, findMaterialIds(task.getId()), findLatestResult(task.getId())))
                .toList();
        return new PageResult<>(records, page.getTotal(), query.getPage(), query.getPageSize());
    }

    @Override
    public WritingTaskVO getDetail(Long id) {
        BizWritingTask task = requireOwnTask(id);
        return toVO(task, findMaterialIds(id), findLatestResult(id));
    }

    private void validateRequest(WritingTaskCreateRequest request) {
        if (!SUPPORTED_WRITING_TYPES.contains(request.getWritingType())) {
            throw new BusinessException("writingType 仅支持 outline、draft、polished、title");
        }
        if ("polished".equals(request.getWritingType()) && !StringUtils.hasText(request.getInputContent())) {
            throw new BusinessException("润色写作任务的 inputContent 不能为空");
        }
        if (request.getTopK() != null && (request.getTopK() < 1 || request.getTopK() > 20)) {
            throw new BusinessException("topK 范围必须为 1-20");
        }
    }

    private List<Long> normalizeMaterialIds(List<Long> materialIds) {
        if (materialIds == null) {
            return null;
        }
        if (materialIds.isEmpty()) {
            throw new BusinessException("materialIds 传入时不能为空数组");
        }
        LinkedHashSet<Long> normalized = new LinkedHashSet<>();
        for (Long materialId : materialIds) {
            if (materialId == null || materialId <= 0) {
                throw new BusinessException("materialIds 不能包含空值或非法 ID");
            }
            normalized.add(materialId);
        }
        return new ArrayList<>(normalized);
    }

    private Map<Long, BizMaterial> loadMaterialMap(Long userId, List<Long> materialIds) {
        if (materialIds == null) {
            return Map.of();
        }
        List<BizMaterial> materials = materialMapper.selectList(new LambdaQueryWrapper<BizMaterial>()
                .eq(BizMaterial::getUserId, userId)
                .in(BizMaterial::getId, materialIds));
        if (materials.size() != materialIds.size()) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "材料不存在或无权访问");
        }
        if (materials.stream().anyMatch(material -> !MATERIAL_STATUS_AVAILABLE.equals(material.getStatus()))) {
            throw new BusinessException("材料未处理完成，不能用于写作任务");
        }
        return materials.stream().collect(Collectors.toMap(BizMaterial::getId, Function.identity()));
    }

    private BizWritingTask createPendingTask(Long userId, WritingTaskCreateRequest request) {
        BizWritingTask task = new BizWritingTask();
        task.setUserId(userId);
        task.setTitle(request.getTitle());
        task.setWritingType(request.getWritingType());
        task.setTopic(request.getTopic());
        task.setRequirement(request.getRequirement());
        task.setInputContent(request.getInputContent());
        task.setStatus(STATUS_PENDING);
        taskMapper.insert(task);
        return task;
    }

    private void insertTaskMaterials(Long taskId, Long userId, List<Long> materialIds) {
        if (materialIds == null) {
            return;
        }
        for (Long materialId : materialIds) {
            BizWritingTaskMaterial relation = new BizWritingTaskMaterial();
            relation.setTaskId(taskId);
            relation.setMaterialId(materialId);
            relation.setUserId(userId);
            taskMaterialMapper.insert(relation);
        }
    }

    private void markRunning(BizWritingTask task) {
        LocalDateTime now = LocalDateTime.now();
        BizWritingTask update = new BizWritingTask();
        update.setId(task.getId());
        update.setStatus(STATUS_RUNNING);
        update.setStartedAt(now);
        taskMapper.updateById(update);
        task.setStatus(STATUS_RUNNING);
        task.setStartedAt(now);
    }

    private void markFinished(Long taskId, String status, String errorMessage) {
        BizWritingTask update = new BizWritingTask();
        update.setId(taskId);
        update.setStatus(status);
        update.setErrorMessage(truncate(errorMessage, 2000));
        update.setFinishedAt(LocalDateTime.now());
        taskMapper.updateById(update);
    }

    private PythonAiGenerateRequest toGenerateRequest(Long userId, WritingTaskCreateRequest request, List<Long> materialIds) {
        PythonAiGenerateRequest aiRequest = new PythonAiGenerateRequest();
        aiRequest.setType(request.getWritingType());
        aiRequest.setTopic(request.getTopic());
        aiRequest.setUserId(userId);
        aiRequest.setContent(request.getInputContent());
        aiRequest.setMaterialIds(materialIds);
        aiRequest.setTopK(request.getTopK());
        return aiRequest;
    }

    private BizAiCallLog createCallLog(Long userId, Long taskId, List<Long> materialIds, WritingTaskCreateRequest request,
                                       PythonAiGenerateResponse response, String status, RuntimeException ex,
                                       Integer httpStatus, LocalDateTime startedAt) {
        LocalDateTime finishedAt = LocalDateTime.now();
        AiCallLogCreateRequest logRequest = new AiCallLogCreateRequest();
        logRequest.setUserId(userId);
        logRequest.setBusinessType(BUSINESS_TYPE_WRITING);
        logRequest.setBusinessId(taskId);
        logRequest.setEndpoint(ENDPOINT_GENERATE);
        logRequest.setMaterialIds(materialIds);
        logRequest.setRequestSummary(toRequestSummary(request, materialIds));
        logRequest.setResponseSummary(toResponseSummary(response));
        logRequest.setStatus(status);
        logRequest.setHttpStatus(resolveHttpStatus(ex, httpStatus));
        logRequest.setErrorCode(resolveErrorCode(ex));
        logRequest.setErrorMessage(ex == null ? null : truncate(ex.getMessage(), 2000));
        logRequest.setSourceCount(response == null || response.getSourceSegments() == null
                ? 0 : response.getSourceSegments().size());
        logRequest.setDurationMs(Duration.between(startedAt, finishedAt).toMillis());
        logRequest.setStartedAt(startedAt);
        logRequest.setFinishedAt(finishedAt);
        return aiCallLogService.create(logRequest);
    }

    private Integer resolveHttpStatus(RuntimeException ex, Integer httpStatus) {
        if (httpStatus != null) {
            return httpStatus;
        }
        if (ex instanceof PythonAiClientException clientException) {
            return clientException.getHttpStatus();
        }
        return ex == null ? 200 : null;
    }

    private String resolveErrorCode(RuntimeException ex) {
        if (ex instanceof PythonAiClientException clientException) {
            return clientException.getErrorCode();
        }
        return ex == null ? null : "writing_generate_failed";
    }

    private BizWritingResult createResult(Long userId, Long taskId, PythonAiGenerateResponse response,
                                          List<WritingSourceSegmentVO> sourceSegments, BizAiCallLog callLog) {
        BizWritingResult result = new BizWritingResult();
        result.setTaskId(taskId);
        result.setUserId(userId);
        result.setVersionNo(FIRST_VERSION);
        result.setContent(response == null ? "" : response.getGeneratedText());
        result.setSourceSegmentsJson(toJson(sourceSegments));
        result.setAiCallLogId(callLog == null ? null : callLog.getId());
        resultMapper.insert(result);
        return result;
    }

    private List<WritingSourceSegmentVO> toSourceSegmentSnapshot(Long userId, List<PythonAiSourceSegment> sourceSegments,
                                                                 Map<Long, BizMaterial> materialMap) {
        if (sourceSegments == null) {
            return List.of();
        }
        Map<Long, BizMaterial> snapshotMaterialMap = fillSourceMaterialMap(userId, sourceSegments, materialMap);
        return sourceSegments.stream().map(segment -> {
            WritingSourceSegmentVO vo = new WritingSourceSegmentVO();
            vo.setText(segment.getText());
            vo.setMaterialId(segment.getMaterialId());
            vo.setSegmentIndex(segment.getSegmentIndex());
            vo.setScore(segment.getScore());
            BizMaterial material = snapshotMaterialMap.get(segment.getMaterialId());
            if (material != null) {
                vo.setMaterialTitle(material.getTitle());
                vo.setOriginalFilename(material.getOriginalFilename());
            }
            return vo;
        }).toList();
    }

    private Map<Long, BizMaterial> fillSourceMaterialMap(Long userId, List<PythonAiSourceSegment> sourceSegments,
                                                         Map<Long, BizMaterial> materialMap) {
        Set<Long> sourceMaterialIds = sourceSegments.stream()
                .map(PythonAiSourceSegment::getMaterialId)
                .filter(id -> id != null && !materialMap.containsKey(id))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (sourceMaterialIds.isEmpty()) {
            return materialMap;
        }
        Map<Long, BizMaterial> filled = new LinkedHashMap<>(materialMap);
        materialMapper.selectList(new LambdaQueryWrapper<BizMaterial>()
                        .eq(BizMaterial::getUserId, userId)
                        .in(BizMaterial::getId, sourceMaterialIds))
                .forEach(material -> filled.put(material.getId(), material));
        return filled;
    }

    private String toRequestSummary(WritingTaskCreateRequest request, List<Long> materialIds) {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("writingType", request.getWritingType());
        summary.put("topic", request.getTopic());
        summary.put("materialIds", materialIds);
        summary.put("topK", request.getTopK());
        return toJson(summary);
    }

    private String toResponseSummary(PythonAiGenerateResponse response) {
        if (response == null) {
            return null;
        }
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("generatedTextLength", response.getGeneratedText() == null ? 0 : response.getGeneratedText().length());
        summary.put("sourceCount", response.getSourceSegments() == null ? 0 : response.getSourceSegments().size());
        return toJson(summary);
    }

    private List<Long> findMaterialIds(Long taskId) {
        return taskMaterialMapper.selectList(new LambdaQueryWrapper<BizWritingTaskMaterial>()
                        .eq(BizWritingTaskMaterial::getTaskId, taskId)
                        .orderByAsc(BizWritingTaskMaterial::getId))
                .stream()
                .map(BizWritingTaskMaterial::getMaterialId)
                .toList();
    }

    private BizWritingResult findLatestResult(Long taskId) {
        return resultMapper.selectOne(new LambdaQueryWrapper<BizWritingResult>()
                .eq(BizWritingResult::getTaskId, taskId)
                .orderByDesc(BizWritingResult::getVersionNo)
                .orderByDesc(BizWritingResult::getId)
                .last("LIMIT 1"));
    }

    private BizWritingTask requireOwnTask(Long id) {
        BizWritingTask task = taskMapper.selectOne(new LambdaQueryWrapper<BizWritingTask>()
                .eq(BizWritingTask::getId, id)
                .eq(BizWritingTask::getUserId, StpUtil.getLoginIdAsLong()));
        if (task == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        return task;
    }

    private WritingTaskVO toVO(BizWritingTask task, List<Long> materialIds, BizWritingResult result) {
        WritingTaskVO vo = new WritingTaskVO();
        BeanUtils.copyProperties(task, vo);
        vo.setMaterialIds(materialIds);
        vo.setResult(toResultVO(result));
        return vo;
    }

    private WritingResultVO toResultVO(BizWritingResult result) {
        if (result == null) {
            return null;
        }
        WritingResultVO vo = new WritingResultVO();
        BeanUtils.copyProperties(result, vo);
        vo.setSourceSegments(parseSourceSegments(result.getSourceSegmentsJson()));
        return vo;
    }

    private List<WritingSourceSegmentVO> parseSourceSegments(String sourceSegmentsJson) {
        if (!StringUtils.hasText(sourceSegmentsJson)) {
            return List.of();
        }
        try {
            return objectMapper.readerForListOf(WritingSourceSegmentVO.class).readValue(sourceSegmentsJson);
        } catch (JsonProcessingException ex) {
            return List.of();
        }
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
