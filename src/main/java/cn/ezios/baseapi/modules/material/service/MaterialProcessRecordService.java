package cn.ezios.baseapi.modules.material.service;

import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.modules.material.dto.MaterialProcessRecordPageQuery;
import cn.ezios.baseapi.modules.material.entity.BizMaterialProcessRecord;
import cn.ezios.baseapi.modules.material.vo.MaterialProcessRecordVO;

public interface MaterialProcessRecordService {

    MaterialProcessRecordVO create(BizMaterialProcessRecord record);

    PageResult<MaterialProcessRecordVO> page(MaterialProcessRecordPageQuery query);

    MaterialProcessRecordVO getDetail(Long id);
}
