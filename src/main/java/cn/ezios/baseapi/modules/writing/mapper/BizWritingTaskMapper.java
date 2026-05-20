package cn.ezios.baseapi.modules.writing.mapper;

import cn.ezios.baseapi.modules.writing.entity.BizWritingTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 写作任务 Mapper，提供 biz_writing_task 表的数据库操作。
 */
@Mapper
public interface BizWritingTaskMapper extends BaseMapper<BizWritingTask> {
}
