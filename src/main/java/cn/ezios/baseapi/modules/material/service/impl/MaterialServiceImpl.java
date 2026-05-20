package cn.ezios.baseapi.modules.material.service.impl;

import cn.ezios.baseapi.common.enums.ResponseCode;
import cn.ezios.baseapi.common.exception.BusinessException;
import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.framework.config.SystemProperties;
import cn.ezios.baseapi.modules.ai.client.PythonAiClient;
import cn.ezios.baseapi.modules.ai.client.PythonAiClientException;
import cn.ezios.baseapi.modules.ai.dto.AiCallLogCreateRequest;
import cn.ezios.baseapi.modules.ai.dto.PythonAiMaterialProcessResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiVectorDeleteResponse;
import cn.ezios.baseapi.modules.ai.service.AiCallLogService;
import cn.ezios.baseapi.modules.material.dto.MaterialPageQuery;
import cn.ezios.baseapi.modules.material.dto.MaterialProcessRequest;
import cn.ezios.baseapi.modules.material.dto.MaterialSaveRequest;
import cn.ezios.baseapi.modules.material.dto.MaterialUpdateRequest;
import cn.ezios.baseapi.modules.material.entity.BizMaterial;
import cn.ezios.baseapi.modules.material.entity.BizMaterialProcessRecord;
import cn.ezios.baseapi.modules.material.mapper.BizMaterialMapper;
import cn.ezios.baseapi.modules.material.mapper.BizMaterialProcessRecordMapper;
import cn.ezios.baseapi.modules.material.service.MaterialService;
import cn.ezios.baseapi.modules.system.file.entity.SysFile;
import cn.ezios.baseapi.modules.system.file.mapper.SysFileMapper;
import cn.ezios.baseapi.modules.material.vo.MaterialVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.util.StringUtils;

/**
 * 材料业务服务实现
 * <p>
 * 核心职责：
 * <ul>
 *   <li>材料主数据的增删改查</li>
 *   <li>调用 Python AI 服务进行材料处理（片段切分、向量化）</li>
 *   <li>调用 Python AI 服务删除材料向量</li>
 *   <li>记录材料处理记录和 AI 调用日志</li>
 * </ul>
 * </p>
 */
@Service
public class MaterialServiceImpl implements MaterialService {

    private static final String DEFAULT_STATUS = "processing";
    private static final String STATUS_PROCESSING = "processing";
    private static final String STATUS_AVAILABLE = "available";
    private static final String STATUS_FAILED = "failed";
    private static final String PROCESS_TYPE_INITIAL = "initial";
    private static final String PROCESS_TYPE_REPROCESS = "reprocess";
    private static final String BUSINESS_TYPE_MATERIAL_PROCESS = "material_process";
    private static final String BUSINESS_TYPE_MATERIAL_VECTOR_DELETE = "material_vector_delete";
    private static final String ENDPOINT_MATERIAL_PROCESS = "/materials/process";
    private static final String ENDPOINT_MATERIAL_VECTOR_DELETE = "/materials/{materialId}/vectors";
    private static final String CALL_STATUS_SUCCESS = "success";
    private static final String CALL_STATUS_FAILED = "failed";

    private final BizMaterialMapper materialMapper;
    private final BizMaterialProcessRecordMapper processRecordMapper;
    private final PythonAiClient pythonAiClient;
    private final AiCallLogService aiCallLogService;
    private final SystemProperties systemProperties;
    private final SysFileMapper sysFileMapper;

    public MaterialServiceImpl(BizMaterialMapper materialMapper,
            BizMaterialProcessRecordMapper processRecordMapper,
            PythonAiClient pythonAiClient,
            AiCallLogService aiCallLogService,
            SystemProperties systemProperties,
            SysFileMapper sysFileMapper) {
        this.materialMapper = materialMapper;
        this.processRecordMapper = processRecordMapper;
        this.pythonAiClient = pythonAiClient;
        this.aiCallLogService = aiCallLogService;
        this.systemProperties = systemProperties;
        this.sysFileMapper = sysFileMapper;
    }

    /**
     * 创建材料记录
     * <p>从关联的文件记录中补充缺失的文件信息（存储路径、原始文件名、文件大小、MD5）</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaterialVO create(MaterialSaveRequest request) {
        BizMaterial material = new BizMaterial();
        BeanUtils.copyProperties(request, material);
        material.setUserId(StpUtil.getLoginIdAsLong());
        if (request.getFileId() != null) {
            SysFile file = sysFileMapper.selectById(request.getFileId());
            if (file != null) {
                material.setStoragePath(file.getStoragePath());
                if (!StringUtils.hasText(material.getOriginalFilename())) {
                    material.setOriginalFilename(file.getOriginalName());
                }
                if (material.getFileSize() == null) {
                    material.setFileSize(file.getFileSize());
                }
                if (!StringUtils.hasText(material.getFileMd5())) {
                    material.setFileMd5(file.getFileMd5());
                }
            }
        }
        if (!StringUtils.hasText(material.getStatus())) {
            material.setStatus(DEFAULT_STATUS);
        }
        material.setSegmentCount(0);
        materialMapper.insert(material);
        return toVO(material);
    }

    @Override
    public PageResult<MaterialVO> page(MaterialPageQuery query) {
        Page<BizMaterial> page = materialMapper.selectPage(Page.of(query.getPage(), query.getPageSize()),
                new LambdaQueryWrapper<BizMaterial>()
                        .eq(BizMaterial::getUserId, StpUtil.getLoginIdAsLong())
                        .like(StringUtils.hasText(query.getTitle()), BizMaterial::getTitle, query.getTitle())
                        .eq(query.getFileId() != null, BizMaterial::getFileId, query.getFileId())
                        .eq(StringUtils.hasText(query.getFileType()), BizMaterial::getFileType, query.getFileType())
                        .eq(StringUtils.hasText(query.getStatus()), BizMaterial::getStatus, query.getStatus())
                        .orderByDesc(BizMaterial::getCreateTime)
                        .orderByDesc(BizMaterial::getId));
        return new PageResult<>(page.getRecords().stream().map(this::toVO).toList(),
                page.getTotal(), query.getPage(), query.getPageSize());
    }

    @Override
    public MaterialVO getDetail(Long id) {
        return toVO(requireMaterial(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, MaterialUpdateRequest request) {
        requireMaterial(id);
        BizMaterial material = new BizMaterial();
        material.setId(id);
        material.setTitle(request.getTitle());
        material.setStatus(request.getStatus());
        material.setSegmentCount(request.getSegmentCount());
        material.setErrorMessage(request.getErrorMessage());
        material.setRemark(request.getRemark());
        materialMapper.updateById(material);
    }

    /**
     * 处理材料：调用 Python AI 服务进行片段切分和向量化
     * <p>
     * 处理流程：
     * 1. 更新材料状态为 processing
     * 2. 解析文件路径并调用 Python 服务
     * 3. 成功：更新状态为 available，记录片段数
     * 4. 失败：更新状态为 failed，记录错误信息
     * 5. 记录处理记录和 AI 调用日志
     * </p>
     */
    @Override
    public MaterialVO process(Long id, MaterialProcessRequest request) {
        BizMaterial material = requireMaterial(id);
        String processType = resolveProcessType(material, request);
        LocalDateTime startedAt = LocalDateTime.now();

        updateProcessStarted(material.getId());
        try {
            Resource file = toFileResource(material);
            PythonAiMaterialProcessResponse response = pythonAiClient.processMaterial(material.getUserId(), material.getId(), file);
            LocalDateTime finishedAt = LocalDateTime.now();
            long durationMs = durationMs(startedAt, finishedAt);
            int segmentCount = nonNegative(response == null ? null : response.getSegmentCount());
            int deletedCount = nonNegative(response == null ? null : response.getDeletedCount());

            updateProcessSucceeded(material.getId(), segmentCount, finishedAt);
            createProcessRecord(material, processType, CALL_STATUS_SUCCESS, deletedCount, segmentCount,
                    null, startedAt, finishedAt, durationMs);
            createAiCallLog(material, BUSINESS_TYPE_MATERIAL_PROCESS, ENDPOINT_MATERIAL_PROCESS,
                    CALL_STATUS_SUCCESS, 200, null, null, startedAt, finishedAt, durationMs,
                    "segmentCount=" + segmentCount + ", deletedCount=" + deletedCount);
            return toVO(requireMaterial(id));
        } catch (RuntimeException ex) {
            LocalDateTime finishedAt = LocalDateTime.now();
            long durationMs = durationMs(startedAt, finishedAt);
            String errorMessage = resolveErrorMessage(ex);

            updateProcessFailed(material.getId(), errorMessage);
            createProcessRecord(material, processType, CALL_STATUS_FAILED, 0, 0,
                    errorMessage, startedAt, finishedAt, durationMs);
            createAiCallLog(material, BUSINESS_TYPE_MATERIAL_PROCESS, ENDPOINT_MATERIAL_PROCESS,
                    CALL_STATUS_FAILED, resolveHttpStatus(ex), resolveErrorCode(ex), errorMessage,
                    startedAt, finishedAt, durationMs, null);
            throw new BusinessException("材料处理失败：" + errorMessage);
        }
    }

    /**
     * 删除材料的向量数据
     * <p>调用 Python AI 服务删除指定材料的所有向量，不影响材料主数据</p>
     */
    @Override
    public void deleteMaterialVectors(Long id) {
        BizMaterial material = requireMaterial(id);
        LocalDateTime startedAt = LocalDateTime.now();
        try {
            PythonAiVectorDeleteResponse response = pythonAiClient.deleteMaterialVectors(material.getUserId(), material.getId());
            LocalDateTime finishedAt = LocalDateTime.now();
            long durationMs = durationMs(startedAt, finishedAt);
            int deletedCount = nonNegative(response == null ? null : response.getDeletedCount());
            createAiCallLog(material, BUSINESS_TYPE_MATERIAL_VECTOR_DELETE, ENDPOINT_MATERIAL_VECTOR_DELETE,
                    CALL_STATUS_SUCCESS, 200, null, null, startedAt, finishedAt, durationMs,
                    "deletedCount=" + deletedCount);
        } catch (RuntimeException ex) {
            LocalDateTime finishedAt = LocalDateTime.now();
            long durationMs = durationMs(startedAt, finishedAt);
            String errorMessage = resolveErrorMessage(ex);
            createAiCallLog(material, BUSINESS_TYPE_MATERIAL_VECTOR_DELETE, ENDPOINT_MATERIAL_VECTOR_DELETE,
                    CALL_STATUS_FAILED, resolveHttpStatus(ex), resolveErrorCode(ex), errorMessage,
                    startedAt, finishedAt, durationMs, null);
            throw new BusinessException("删除材料向量失败：" + errorMessage);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireMaterial(id);
        materialMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(BatchIdsRequest request) {
        for (Long id : request.getIds()) {
            delete(id);
        }
    }

    private BizMaterial requireMaterial(Long id) {
        BizMaterial material = materialMapper.selectById(id);
        if (material == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        return material;
    }

    /**
     * 将材料的存储路径转换为文件资源
     * <p>包含路径遍历防护：确保解析后的路径在上传根目录下</p>
     */
    private Resource toFileResource(BizMaterial material) {
        if (!StringUtils.hasText(material.getStoragePath())) {
            throw new BusinessException("材料文件存储路径不能为空");
        }
        Path uploadRoot = Path.of(systemProperties.getFile().getUploadRoot()).toAbsolutePath().normalize();
        Path fullPath = uploadRoot.resolve(material.getStoragePath()).normalize();
         if (!fullPath.startsWith(uploadRoot)) {
            throw new BusinessException("非法文件路径");
        }
        FileSystemResource resource = new FileSystemResource(fullPath);
        if (!resource.exists() || !resource.isReadable()) {
            throw new BusinessException("材料文件不存在或不可读");
        }
        return resource;
    }

    private String resolveProcessType(BizMaterial material, MaterialProcessRequest request) {
        if (request != null && StringUtils.hasText(request.getProcessType())) {
            return request.getProcessType();
        }
        return material.getLastProcessTime() == null ? PROCESS_TYPE_INITIAL : PROCESS_TYPE_REPROCESS;
    }

    private void updateProcessStarted(Long materialId) {
        materialMapper.update(new LambdaUpdateWrapper<BizMaterial>()
                .eq(BizMaterial::getId, materialId)
                .set(BizMaterial::getStatus, STATUS_PROCESSING));
    }

    private void updateProcessSucceeded(Long materialId, Integer segmentCount, LocalDateTime finishedAt) {
        materialMapper.update(new LambdaUpdateWrapper<BizMaterial>()
                .eq(BizMaterial::getId, materialId)
                .set(BizMaterial::getStatus, STATUS_AVAILABLE)
                .set(BizMaterial::getSegmentCount, segmentCount)
                .set(BizMaterial::getLastProcessTime, finishedAt)
                .set(BizMaterial::getErrorMessage, null));
    }

    private void updateProcessFailed(Long materialId, String errorMessage) {
        materialMapper.update(new LambdaUpdateWrapper<BizMaterial>()
                .eq(BizMaterial::getId, materialId)
                .set(BizMaterial::getStatus, STATUS_FAILED)
                .set(BizMaterial::getSegmentCount, 0)
                .set(BizMaterial::getErrorMessage, errorMessage));
    }

    private void createProcessRecord(BizMaterial material, String processType, String status,
            Integer deletedCount, Integer segmentCount, String errorMessage,
            LocalDateTime startedAt, LocalDateTime finishedAt, Long durationMs) {
        BizMaterialProcessRecord record = new BizMaterialProcessRecord();
        record.setMaterialId(material.getId());
        record.setUserId(material.getUserId());
        record.setFileId(material.getFileId());
        record.setFileMd5(material.getFileMd5());
        record.setOriginalFilename(material.getOriginalFilename());
        record.setProcessType(processType);
        record.setStatus(status);
        record.setDeletedCount(deletedCount);
        record.setSegmentCount(segmentCount);
        record.setErrorMessage(errorMessage);
        record.setStartedAt(startedAt);
        record.setFinishedAt(finishedAt);
        record.setDurationMs(durationMs);
        processRecordMapper.insert(record);
    }

    private void createAiCallLog(BizMaterial material, String businessType, String endpoint,
            String status, Integer httpStatus, String errorCode, String errorMessage,
            LocalDateTime startedAt, LocalDateTime finishedAt, Long durationMs, String responseSummary) {
        AiCallLogCreateRequest request = new AiCallLogCreateRequest();
        request.setUserId(material.getUserId());
        request.setBusinessType(businessType);
        request.setBusinessId(material.getId());
        request.setEndpoint(endpoint);
        request.setMaterialIds(List.of(material.getId()));
        request.setRequestSummary("materialId=" + material.getId() + ", fileId=" + material.getFileId());
        request.setResponseSummary(responseSummary);
        request.setStatus(status);
        request.setHttpStatus(httpStatus);
        request.setErrorCode(errorCode);
        request.setErrorMessage(errorMessage);
        request.setDurationMs(durationMs);
        request.setStartedAt(startedAt);
        request.setFinishedAt(finishedAt);
        aiCallLogService.create(request);
    }

    private Long durationMs(LocalDateTime startedAt, LocalDateTime finishedAt) {
        return Duration.between(startedAt, finishedAt).toMillis();
    }

    private int nonNegative(Integer value) {
        return value == null || value < 0 ? 0 : value;
    }

    private Integer resolveHttpStatus(RuntimeException ex) {
        if (ex instanceof PythonAiClientException clientException) {
            return clientException.getHttpStatus();
        }
        return null;
    }

    private String resolveErrorCode(RuntimeException ex) {
        if (ex instanceof PythonAiClientException clientException) {
            return clientException.getErrorCode();
        }
        return ex.getClass().getSimpleName();
    }

    private String resolveErrorMessage(RuntimeException ex) {
        if (ex instanceof PythonAiClientException clientException && StringUtils.hasText(clientException.getDetail())) {
            return clientException.getDetail();
        }
        return StringUtils.hasText(ex.getMessage()) ? ex.getMessage() : "未知错误";
    }

    private MaterialVO toVO(BizMaterial material) {
        MaterialVO vo = new MaterialVO();
        BeanUtils.copyProperties(material, vo);
        return vo;
    }
}
