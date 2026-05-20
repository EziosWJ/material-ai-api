package cn.ezios.baseapi.modules.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 当前用户角色信息视图对象。
 */
@Data
@Schema(description = "当前用户角色信息")
public class AuthRoleVO {

    /** 角色 ID */
    @Schema(description = "角色 ID")
    private Long id;

    /** 角色名称 */
    @Schema(description = "角色名称")
    private String roleName;

    /** 角色编码，用于权限判断 */
    @Schema(description = "角色编码")
    private String roleCode;
}
