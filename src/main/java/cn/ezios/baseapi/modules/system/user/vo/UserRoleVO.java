package cn.ezios.baseapi.modules.system.user.vo;

import lombok.Data;

/**
 * 用户角色信息
 * <p>嵌套在用户详情中展示</p>
 */
@Data
public class UserRoleVO {

    /** 角色ID */
    private Long id;

    /** 角色名称 */
    private String roleName;

    /** 角色编码 */
    private String roleCode;

    /** 状态 */
    private Integer status;
}
