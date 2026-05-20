package cn.ezios.baseapi.modules.system.user.mapper;

import cn.ezios.baseapi.modules.system.user.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层
 * <p>提供 sys_user 表的基本 CRUD 操作</p>
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
