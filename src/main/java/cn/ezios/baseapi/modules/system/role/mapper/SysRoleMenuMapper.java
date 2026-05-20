package cn.ezios.baseapi.modules.system.role.mapper;

import cn.ezios.baseapi.modules.system.role.entity.SysRoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色菜单关联数据访问层
 * <p>提供 sys_role_menu 表的基本 CRUD 操作</p>
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
}
