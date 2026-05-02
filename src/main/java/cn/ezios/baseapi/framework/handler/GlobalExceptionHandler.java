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

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException exception) {
        return ApiResponse.fail(exception.getCode(), exception.getMessage());
    }

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

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBadRequest(Exception exception) {
        return ApiResponse.fail(ResponseCode.BAD_REQUEST.getCode(), exception.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleNotLoginException(NotLoginException exception) {
        return ApiResponse.fail(ResponseCode.UNAUTHORIZED);
    }

    @ExceptionHandler(NotPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleNotPermissionException(NotPermissionException exception) {
        return ApiResponse.fail(ResponseCode.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception exception) {
        logger.error("系统异常", exception);
        return ApiResponse.fail(ResponseCode.INTERNAL_ERROR);
    }

    private void putFieldError(Map<String, String> errors, FieldError error) {
        errors.put(error.getField(), error.getDefaultMessage());
    }
}
