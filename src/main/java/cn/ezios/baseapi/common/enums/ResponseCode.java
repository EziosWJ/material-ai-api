package cn.ezios.baseapi.common.enums;

import lombok.Getter;

/**
 * 统一响应状态码枚举，定义接口返回的标准状态码和默认提示信息。
 */
@Getter
public enum ResponseCode {

    /** 成功 */
    SUCCESS(200, "success"),
    /** 请求参数错误 */
    BAD_REQUEST(400, "参数错误"),
    /** 未认证：未登录或 token 已失效 */
    UNAUTHORIZED(401, "未登录或 token 已失效"),
    /** 无权限访问 */
    FORBIDDEN(403, "无权限"),
    /** 请求的数据不存在 */
    NOT_FOUND(404, "数据不存在"),
    /** 系统内部错误 */
    INTERNAL_ERROR(500, "系统错误");

    /** 状态码 */
    private final int code;
    /** 默认提示信息 */
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
