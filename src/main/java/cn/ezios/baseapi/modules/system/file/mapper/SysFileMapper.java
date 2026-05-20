package cn.ezios.baseapi.modules.system.file.mapper;

import cn.ezios.baseapi.modules.system.file.entity.SysFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件数据访问层
 * <p>提供 sys_file 表的基本 CRUD 操作</p>
 */
@Mapper
public interface SysFileMapper extends BaseMapper<SysFile> {
}
