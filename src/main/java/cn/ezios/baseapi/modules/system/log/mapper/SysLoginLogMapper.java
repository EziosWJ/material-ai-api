package cn.ezios.baseapi.modules.system.log.mapper;

import cn.ezios.baseapi.modules.system.log.entity.SysLoginLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志数据访问层
 * <p>提供 sys_login_log 表的基本 CRUD 操作</p>
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {
}
