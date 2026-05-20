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

/**
 * 问答会话业务接口，定义会话创建、查询、材料维护和提问等核心能力。
 */
public interface QaSessionService {

    /**
     * 创建问答会话。
     *
     * @param request 创建请求
     * @return 创建后的会话详情
     */
    QaSessionVO create(QaSessionCreateRequest request);

    /**
     * 分页查询当前用户的问答会话。
     *
     * @param query 分页与过滤条件
     * @return 分页结果
     */
    PageResult<QaSessionVO> page(QaSessionPageQuery query);

    /**
     * 查询问答会话详情。
     *
     * @param id 会话 ID
     * @return 会话详情
     */
    QaSessionVO detail(Long id);

    /**
     * 维护会话关联的材料集合，全量替换。
     *
     * @param id      会话 ID
     * @param request 包含新的材料 ID 列表
     * @return 替换后的材料列表
     */
    List<QaMaterialVO> updateMaterials(Long id, QaSessionMaterialUpdateRequest request);

    /**
     * 查询会话消息列表。
     *
     * @param id            会话 ID
     * @param includeSystem 是否包含系统角色消息
     * @return 消息列表
     */
    List<QaMessageVO> messages(Long id, boolean includeSystem);

    /**
     * 向会话发送问题，触发 AI 回答。
     *
     * @param id      会话 ID
     * @param request 提问请求
     * @return 包含用户消息和助手回复的问答结果
     */
    QaAskVO ask(Long id, QaAskRequest request);
}
