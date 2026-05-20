package cn.ezios.baseapi.modules.system.config.mapper;

import cn.ezios.baseapi.modules.system.config.entity.SysConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置数据访问层
 * <p>提供 sys_config 表的基本 CRUD 操作</p>
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {
}
