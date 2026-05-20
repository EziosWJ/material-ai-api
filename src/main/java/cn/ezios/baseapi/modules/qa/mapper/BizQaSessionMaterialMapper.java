package cn.ezios.baseapi.modules.qa.mapper;

import cn.ezios.baseapi.modules.qa.entity.BizQaSessionMaterial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问答会话材料关联 Mapper，提供 biz_qa_session_material 表的基础 CRUD 操作。
 */
@Mapper
public interface BizQaSessionMaterialMapper extends BaseMapper<BizQaSessionMaterial> {
}
