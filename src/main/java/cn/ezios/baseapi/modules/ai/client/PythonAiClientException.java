package cn.ezios.baseapi.modules.ai.client;

import lombok.Getter;

@Getter
public class PythonAiClientException extends RuntimeException {

    private final Integer httpStatus;

    private final String errorCode;

    private final String detail;

    public PythonAiClientException(Integer httpStatus, String errorCode, String detail) {
        super(detail);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.detail = detail;
    }
}
