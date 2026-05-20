package cn.ezios.baseapi.modules.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Python AI 服务错误响应。
 * <p>对应 Python 服务返回的 JSON 错误体，用于解析错误码和详细信息。</p>
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PythonAiErrorResponse {

    /** 错误码 */
    private String error;

    /** 错误详细描述 */
    private String detail;
}
