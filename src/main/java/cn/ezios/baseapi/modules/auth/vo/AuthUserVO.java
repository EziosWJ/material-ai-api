package cn.ezios.baseapi.modules.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 当前登录用户详细信息视图对象，包含部门和角色等关联数据。
 */
@Data
@Schema(description = "当前用户信息")
public class AuthUserVO {

    /** 用户 ID */
    @Schema(description = "用户 ID")
    private Long id;

    /** 用户名 */
    @Schema(description = "用户名")
    private String username;

    /** 昵称 */
    @Schema(description = "昵称")
    private String nickname;

    /** 头像地址 */
    @Schema(description = "头像")
    private String avatar;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /** 所属部门信息 */
    @Schema(description = "部门信息")
    private AuthDeptVO dept;

    /** 已启用的角色列表 */
    @Schema(description = "角色信息")
    private List<AuthRoleVO> roles;

    /** 最后登录时间 */
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    /** 最后登录 IP */
    @Schema(description = "最后登录 IP")
    private String lastLoginIp;
}
