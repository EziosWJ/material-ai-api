package cn.ezios.baseapi.modules.qa.service;

import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.modules.qa.dto.QaAskRequest;
import cn.ezios.baseapi.modules.qa.dto.QaSessionCreateRequest;
import cn.ezios.baseapi.modules.qa.dto.QaSessionMaterialUpdateRequest;
import cn.ezios.baseapi.modules.qa.dto.QaSessionPageQuery;
import cn.ezios.baseapi.modules.qa.vo.QaAskVO;
import cn.ezios.baseapi.modules.qa.vo.QaMaterialVO;
import cn.ezios.baseapi.modules.qa.vo.QaMessageVO;
import cn.ezios.baseapi.modules.qa.vo.QaSessionVO;
import java.util.List;

public interface QaSessionService {

    QaSessionVO create(QaSessionCreateRequest request);

    PageResult<QaSessionVO> page(QaSessionPageQuery query);

    QaSessionVO detail(Long id);

    List<QaMaterialVO> updateMaterials(Long id, QaSessionMaterialUpdateRequest request);

    List<QaMessageVO> messages(Long id, boolean includeSystem);

    QaAskVO ask(Long id, QaAskRequest request);
}
