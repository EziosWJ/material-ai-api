package cn.ezios.baseapi.modules.writing.mapper;

import cn.ezios.baseapi.modules.writing.entity.BizWritingResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 写作结果 Mapper，提供 biz_writing_result 表的数据库操作。
 */
@Mapper
public interface BizWritingResultMapper extends BaseMapper<BizWritingResult> {
}
