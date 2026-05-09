package cn.ezios.baseapi.modules.ai.service;

import cn.ezios.baseapi.modules.ai.dto.AiCallLogCreateRequest;
import cn.ezios.baseapi.modules.ai.entity.BizAiCallLog;

public interface AiCallLogService {

    BizAiCallLog create(AiCallLogCreateRequest request);
}
