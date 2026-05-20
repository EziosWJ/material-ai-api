package cn.ezios.baseapi.common.model;

import cn.ezios.baseapi.common.enums.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应结构，所有接口返回值均使用此类包装。
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** 状态码 */
    private int code;
    /** 提示信息 */
    private String message;
    /** 响应数据 */
    private T data;

    /**
     * 返回无数据的成功响应。
     *
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 返回携带数据的成功响应。
     *
     * @param data 响应数据
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    /**
     * 根据预定义状态码返回失败响应。
     *
     * @param responseCode 响应状态码枚举
     * @return 失败响应
     */
    public static <T> ApiResponse<T> fail(ResponseCode responseCode) {
        return fail(responseCode.getCode(), responseCode.getMessage(), null);
    }

    /**
     * 使用自定义状态码和消息返回失败响应。
     *
     * @param code    状态码
     * @param message 错误描述
     * @return 失败响应
     */
    public static <T> ApiResponse<T> fail(int code, String message) {
        return fail(code, message, null);
    }

    /**
     * 使用自定义状态码、消息和数据返回失败响应。
     *
     * @param code    状态码
     * @param message 错误描述
     * @param data    附加数据
     * @return 失败响应
     */
    public static <T> ApiResponse<T> fail(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }
}
