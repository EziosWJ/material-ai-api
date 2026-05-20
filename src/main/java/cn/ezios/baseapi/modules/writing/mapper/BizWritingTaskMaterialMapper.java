package cn.ezios.baseapi.modules.writing.mapper;

import cn.ezios.baseapi.modules.writing.entity.BizWritingTaskMaterial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 写作任务与材料关联 Mapper，提供 biz_writing_task_material 表的数据库操作。
 */
@Mapper
public interface BizWritingTaskMaterialMapper extends BaseMapper<BizWritingTaskMaterial> {
}
