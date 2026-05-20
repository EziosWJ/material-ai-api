package cn.ezios.baseapi.modules.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功后返回的令牌信息，前端据此设置请求头。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录令牌信息")
public class LoginTokenVO {

    /** Token 请求头名称（如 Authorization） */
    @Schema(description = "Token 请求头名称")
    private String tokenName;

    /** Token 请求头值，已包含前缀 */
    @Schema(description = "Token 请求头值")
    private String tokenValue;

    /** Token 剩余有效期，单位秒 */
    @Schema(description = "Token 剩余有效期，单位秒")
    private long expiresIn;
}
