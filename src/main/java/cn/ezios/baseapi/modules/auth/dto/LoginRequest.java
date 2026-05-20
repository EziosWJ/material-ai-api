package cn.ezios.baseapi.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求参数，包含用户名和密码。
 */
@Data
@Schema(description = "登录请求")
public class LoginRequest {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "admin")
    private String username;

    /** 密码（明文，由后端校验加密后的密码） */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "admin123")
    private String password;
}
