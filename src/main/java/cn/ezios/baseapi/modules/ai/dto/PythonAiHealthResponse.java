package cn.ezios.baseapi.modules.ai.dto;

import lombok.Data;

/**
 * Python AI 服务健康检查响应。
 */
@Data
public class PythonAiHealthResponse {

    /** 健康状态，如 ok、degraded */
    private String status;

    /** 详细状态描述 */
    private String detail;
}
