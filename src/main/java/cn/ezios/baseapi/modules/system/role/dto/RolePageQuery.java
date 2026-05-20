package cn.ezios.baseapi.modules.system.role.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RolePageQuery extends PageQuery {

    /** 角色名称，模糊匹配 */
    private String roleName;

    /** 角色编码，模糊匹配 */
    private String roleCode;

    /** 状态：1-启用，0-禁用 */
    private Integer status;
}
