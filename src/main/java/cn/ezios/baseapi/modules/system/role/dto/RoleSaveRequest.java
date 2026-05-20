package cn.ezios.baseapi.modules.system.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 角色保存请求
 */
@Data
public class RoleSaveRequest {

    /** 角色名称 */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过 50")
    private String roleName;

    /** 角色编码，全局唯一 */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过 50")
    private String roleCode;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 排序序号 */
    private Integer sortOrder;

    /** 备注 */
    private String remark;
}
