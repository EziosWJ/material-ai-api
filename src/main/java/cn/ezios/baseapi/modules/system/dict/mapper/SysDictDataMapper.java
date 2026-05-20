package cn.ezios.baseapi.modules.system.dict.mapper;

import cn.ezios.baseapi.modules.system.dict.entity.SysDictData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 字典数据访问层
 * <p>提供 sys_dict_data 表的基本 CRUD 操作</p>
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {
}
