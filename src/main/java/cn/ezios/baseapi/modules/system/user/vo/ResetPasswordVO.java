package cn.ezios.baseapi.modules.system.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 密码重置响应
 * <p>包含重置后的新密码，供管理员告知用户</p>
 */
@Data
@AllArgsConstructor
public class ResetPasswordVO {

    /** 重置后的新密码 */
    private String password;
}
