package cn.ezios.baseapi.modules.qa.mapper;

import cn.ezios.baseapi.modules.qa.entity.BizQaMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问答消息 Mapper，提供 biz_qa_message 表的基础 CRUD 操作。
 */
@Mapper
public interface BizQaMessageMapper extends BaseMapper<BizQaMessage> {
}
