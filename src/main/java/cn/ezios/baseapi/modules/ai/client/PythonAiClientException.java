package cn.ezios.baseapi.modules.ai.client;

import lombok.Getter;

/**
 * Python AI 服务调用异常。
 * <p>封装调用 Python AI 服务时返回的错误信息，包括 HTTP 状态码、错误码和详细描述。</p>
 */
@Getter
public class PythonAiClientException extends RuntimeException {

    /** HTTP 状态码 */
    private final Integer httpStatus;

    /** Python AI 服务返回的错误码 */
    private final String errorCode;

    /** 错误详细描述 */
    private final String detail;

    public PythonAiClientException(Integer httpStatus, String errorCode, String detail) {
        super(detail);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.detail = detail;
    }
}
