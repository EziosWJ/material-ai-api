package cn.ezios.baseapi.modules.system.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户修改请求
 */
@Data
public class UserUpdateRequest {

    /** 昵称 */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过 50")
    private String nickname;

    /** 手机号 */
    @Size(max = 20, message = "手机号长度不能超过 20")
    private String phone;

    /** 邮箱 */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100")
    private String email;

    /** 头像 */
    private String avatar;

    /** 性别 */
    private String gender;

    /** 部门ID */
    private Long deptId;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 备注 */
    private String remark;
}
