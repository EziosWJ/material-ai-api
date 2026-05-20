package cn.ezios.baseapi.modules.material.service;

import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.modules.material.dto.MaterialPageQuery;
import cn.ezios.baseapi.modules.material.dto.MaterialProcessRequest;
import cn.ezios.baseapi.modules.material.dto.MaterialSaveRequest;
import cn.ezios.baseapi.modules.material.dto.MaterialUpdateRequest;
import cn.ezios.baseapi.modules.material.vo.MaterialVO;

/**
 * 材料业务服务接口
 * <p>定义材料主数据的增删改查、材料处理及向量维护等核心业务操作</p>
 */
public interface MaterialService {

    /**
     * 创建材料
     *
     * @param request 材料保存请求，包含标题、文件信息等
     * @return 创建成功的材料视图对象
     */
    MaterialVO create(MaterialSaveRequest request);

    /**
     * 分页查询材料列表
     *
     * @param query 分页查询条件，支持标题、文件类型、状态筛选
     * @return 分页结果
     */
    PageResult<MaterialVO> page(MaterialPageQuery query);

    /**
     * 获取材料详情
     *
     * @param id 材料ID
     * @return 材料视图对象
     * @throws BusinessException 材料不存在时抛出 NOT_FOUND
     */
    MaterialVO getDetail(Long id);

    /**
     * 更新材料信息
     *
     * @param id      材料ID
     * @param request 更新请求
     */
    void update(Long id, MaterialUpdateRequest request);

    /**
     * 处理材料（调用 Python AI 服务进行片段切分和向量化）
     * <p>处理流程：更新状态为 processing -> 调用 Python 服务 -> 根据结果更新状态和片段数</p>
     *
     * @param id      材料ID
     * @param request 处理请求（可选，指定处理类型）
     * @return 处理后的材料视图对象
     * @throws BusinessException 处理失败时抛出异常
     */
    MaterialVO process(Long id, MaterialProcessRequest request);

    /**
     * 删除材料的向量数据（调用 Python AI 服务）
     *
     * @param id 材料ID
     */
    void deleteMaterialVectors(Long id);

    /**
     * 删除材料（逻辑删除）
     *
     * @param id 材料ID
     */
    void delete(Long id);

    /**
     * 批量删除材料
     *
     * @param request 包含材料ID列表的请求
     */
    void deleteBatch(BatchIdsRequest request);
}
