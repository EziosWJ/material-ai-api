package cn.ezios.baseapi.modules.system.role.dto;

import java.util.List;
import lombok.Data;

/**
 * 角色菜单分配请求
 */
@Data
public class RoleMenuRequest {

    /** 菜单ID列表 */
    private List<Long> menuIds;
}
