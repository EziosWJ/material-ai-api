package cn.ezios.baseapi.common.exception;

import cn.ezios.baseapi.common.enums.ResponseCode;
import lombok.Getter;

/**
 * 业务异常，用于在业务逻辑中抛出可预期的错误，
 * 由全局异常处理器捕获并转换为统一的 {@link cn.ezios.baseapi.common.model.ApiResponse} 返回。
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 业务状态码 */
    private final int code;

    /**
     * 使用默认的 400 状态码构造业务异常。
     *
     * @param message 错误描述
     */
    public BusinessException(String message) {
        this(ResponseCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 使用预定义的响应状态码构造业务异常。
     *
     * @param responseCode 响应状态码枚举
     */
    public BusinessException(ResponseCode responseCode) {
        this(responseCode.getCode(), responseCode.getMessage());
    }

    /**
     * 使用自定义状态码和消息构造业务异常。
     *
     * @param code    状态码
     * @param message 错误描述
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
