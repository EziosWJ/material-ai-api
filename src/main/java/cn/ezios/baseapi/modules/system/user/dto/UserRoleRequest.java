package cn.ezios.baseapi.modules.system.user.dto;

import java.util.List;
import lombok.Data;

/**
 * 用户角色分配请求
 */
@Data
public class UserRoleRequest {

    /** 角色ID列表 */
    private List<Long> roleIds;
}
