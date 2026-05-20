package cn.ezios.baseapi.modules.system.role.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 角色视图对象
 * <p>包含角色基本信息及已分配的菜单ID列表</p>
 */
@Data
public class RoleVO {

    /** 主键ID */
    private Long id;

    /** 角色名称 */
    private String roleName;

    /** 角色编码 */
    private String roleCode;

    /** 状态 */
    private Integer status;

    /** 排序序号 */
    private Integer sortOrder;

    /** 是否内置 */
    private Integer isBuiltin;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 已分配的菜单ID列表 */
    private List<Long> menuIds = new ArrayList<>();
}
