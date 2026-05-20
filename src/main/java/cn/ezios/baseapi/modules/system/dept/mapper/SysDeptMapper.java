package cn.ezios.baseapi.modules.system.dept.mapper;

import cn.ezios.baseapi.modules.system.dept.entity.SysDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 部门数据访问层
 * <p>提供 sys_dept 表的基本 CRUD 操作</p>
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {
}
