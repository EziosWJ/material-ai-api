package cn.ezios.baseapi.modules.system.log.mapper;

import cn.ezios.baseapi.modules.system.log.entity.SysOperLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志数据访问层
 * <p>提供 sys_oper_log 表的基本 CRUD 操作</p>
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}
