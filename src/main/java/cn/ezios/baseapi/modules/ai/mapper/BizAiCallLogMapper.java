package cn.ezios.baseapi.modules.ai.mapper;

import cn.ezios.baseapi.modules.ai.entity.BizAiCallLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 调用日志 MyBatis-Plus Mapper。
 */
@Mapper
public interface BizAiCallLogMapper extends BaseMapper<BizAiCallLog> {
}
