package cn.ezios.baseapi.modules.material.service;

import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.modules.material.dto.MaterialPageQuery;
import cn.ezios.baseapi.modules.material.dto.MaterialProcessRequest;
import cn.ezios.baseapi.modules.material.dto.MaterialSaveRequest;
import cn.ezios.baseapi.modules.material.dto.MaterialUpdateRequest;
import cn.ezios.baseapi.modules.material.vo.MaterialVO;

public interface MaterialService {

    MaterialVO create(MaterialSaveRequest request);

    PageResult<MaterialVO> page(MaterialPageQuery query);

    MaterialVO getDetail(Long id);

    void update(Long id, MaterialUpdateRequest request);

    MaterialVO process(Long id, MaterialProcessRequest request);

    void deleteMaterialVectors(Long id);

    void delete(Long id);

    void deleteBatch(BatchIdsRequest request);
}
