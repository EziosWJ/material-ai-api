package cn.ezios.baseapi.modules.qa.mapper;

import cn.ezios.baseapi.modules.qa.entity.BizQaSession;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问答会话 Mapper，提供 biz_qa_session 表的基础 CRUD 操作。
 */
@Mapper
public interface BizQaSessionMapper extends BaseMapper<BizQaSession> {
}
