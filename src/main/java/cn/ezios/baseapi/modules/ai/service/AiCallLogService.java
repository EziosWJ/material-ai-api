package cn.ezios.baseapi.modules.ai.service;

import cn.ezios.baseapi.modules.ai.dto.AiCallLogCreateRequest;
import cn.ezios.baseapi.modules.ai.entity.BizAiCallLog;

/**
 * AI 调用日志服务接口。
 * <p>负责记录每次 Python AI 服务调用的审计日志。</p>
 */
public interface AiCallLogService {

    /**
     * 创建一条 AI 调用日志记录。
     *
     * @param request 调用日志信息
     * @return 持久化后的日志实体
     */
    BizAiCallLog create(AiCallLogCreateRequest request);
}
