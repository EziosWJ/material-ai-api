package cn.ezios.baseapi.framework.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.ezios.baseapi.common.enums.ResponseCode;
import cn.ezios.baseapi.common.exception.BusinessException;
import cn.ezios.baseapi.common.model.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 * <p>统一捕获各类异常并转换为 {@link ApiResponse} 格式返回，确保前端获得一致的错误响应结构。</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常，返回对应的错误码和消息。
     *
     * @param exception 业务异常
     * @return 统一错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException exception) {
        return ApiResponse.fail(exception.getCode(), exception.getMessage());
    }

    /**
     * 处理请求参数绑定校验异常（@Valid / @Validated），返回字段级错误信息。
     *
     * @param exception 参数绑定异常
     * @return 包含字段错误映射的错误响应
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleBindException(Exception exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (exception instanceof MethodArgumentNotValidException validException) {
            validException.getBindingResult().getFieldErrors()
                    .forEach(error -> putFieldError(errors, error));
        } else if (exception instanceof BindException bindException) {
            bindException.getBindingResult().getFieldErrors()
                    .forEach(error -> putFieldError(errors, error));
        }
        return ApiResponse.fail(ResponseCode.BAD_REQUEST.getCode(), ResponseCode.BAD_REQUEST.getMessage(), errors);
    }

    /**
     * 处理方法参数约束校验异常（@Validated 单参数校验），返回字段级错误信息。
     *
     * @param exception 约束违反异常
     * @return 包含字段错误映射的错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            String field = path.substring(path.lastIndexOf('.') + 1);
            errors.put(field, violation.getMessage());
        });
        return ApiResponse.fail(ResponseCode.BAD_REQUEST.getCode(), ResponseCode.BAD_REQUEST.getMessage(), errors);
    }

    /**
     * 处理请求参数缺失和 HTTP 方法不支持异常。
     *
     * @param exception 请求异常
     * @return 错误响应
     */
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBadRequest(Exception exception) {
        return ApiResponse.fail(ResponseCode.BAD_REQUEST.getCode(), exception.getMessage());
    }

    /**
     * 处理 Sa-Token 未登录异常，返回 401。
     *
     * @param exception 未登录异常
     * @return 未授权错误响应
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleNotLoginException(NotLoginException exception) {
        return ApiResponse.fail(ResponseCode.UNAUTHORIZED);
    }

    /**
     * 处理 Sa-Token 权限不足异常，返回 403。
     *
     * @param exception 权限不足异常
     * @return 禁止访问错误响应
     */
    @ExceptionHandler(NotPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleNotPermissionException(NotPermissionException exception) {
        return ApiResponse.fail(ResponseCode.FORBIDDEN);
    }

    /**
     * 兜底处理所有未被捕获的异常，记录日志并返回 500。
     *
     * @param exception 未知异常
     * @return 内部错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception exception) {
        logger.error("系统异常", exception);
        return ApiResponse.fail(ResponseCode.INTERNAL_ERROR);
    }

    /**
     * 将单个字段校验错误放入错误映射。
     */
    private void putFieldError(Map<String, String> errors, FieldError error) {
        errors.put(error.getField(), error.getDefaultMessage());
    }
}
