package cn.ezios.baseapi.modules.ai.service.impl;

import cn.ezios.baseapi.modules.ai.dto.AiCallLogCreateRequest;
import cn.ezios.baseapi.modules.ai.entity.BizAiCallLog;
import cn.ezios.baseapi.modules.ai.mapper.BizAiCallLogMapper;
import cn.ezios.baseapi.modules.ai.service.AiCallLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class AiCallLogServiceImpl implements AiCallLogService {

    private static final int DEFAULT_SOURCE_COUNT = 0;

    private final BizAiCallLogMapper aiCallLogMapper;
    private final ObjectMapper objectMapper;

    public AiCallLogServiceImpl(BizAiCallLogMapper aiCallLogMapper, ObjectMapper objectMapper) {
        this.aiCallLogMapper = aiCallLogMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public BizAiCallLog create(AiCallLogCreateRequest request) {
        BizAiCallLog log = new BizAiCallLog();
        BeanUtils.copyProperties(request, log);
        log.setSourceCount(Objects.requireNonNullElse(request.getSourceCount(), DEFAULT_SOURCE_COUNT));
        log.setMaterialIdsJson(toJson(request.getMaterialIds()));
        aiCallLogMapper.insert(log);
        return log;
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
}
