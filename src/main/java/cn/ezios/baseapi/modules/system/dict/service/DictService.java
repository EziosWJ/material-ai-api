package cn.ezios.baseapi.modules.system.dict.service;

import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.common.model.StatusUpdateRequest;
import cn.ezios.baseapi.modules.system.dict.dto.DictDataPageQuery;
import cn.ezios.baseapi.modules.system.dict.dto.DictDataSaveRequest;
import cn.ezios.baseapi.modules.system.dict.dto.DictTypePageQuery;
import cn.ezios.baseapi.modules.system.dict.dto.DictTypeSaveRequest;
import cn.ezios.baseapi.modules.system.dict.vo.DictDataVO;
import cn.ezios.baseapi.modules.system.dict.vo.DictItemVO;
import cn.ezios.baseapi.modules.system.dict.vo.DictTypeVO;
import java.util.List;

/**
 * 字典管理服务接口
 * <p>定义字典类型和字典数据的业务操作</p>
 */
public interface DictService {

    /**
     * 分页查询字典类型
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<DictTypeVO> typePage(DictTypePageQuery query);

    /**
     * 获取字典类型详情
     *
     * @param id 字典类型ID
     * @return 字典类型详情
     */
    DictTypeVO typeDetail(Long id);

    /**
     * 新增字典类型
     *
     * @param request 字典类型保存请求
     */
    void createType(DictTypeSaveRequest request);

    /**
     * 修改字典类型
     *
     * @param id      字典类型ID
     * @param request 字典类型保存请求
     */
    void updateType(Long id, DictTypeSaveRequest request);

    /**
     * 删除字典类型
     *
     * @param id 字典类型ID
     */
    void deleteType(Long id);

    /**
     * 批量删除字典类型
     *
     * @param request 包含字典类型ID列表的请求
     */
    void deleteTypeBatch(BatchIdsRequest request);

    /**
     * 修改字典类型状态
     *
     * @param id      字典类型ID
     * @param request 状态更新请求
     */
    void updateTypeStatus(Long id, StatusUpdateRequest request);

    /**
     * 分页查询字典数据
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<DictDataVO> dataPage(DictDataPageQuery query);

    /**
     * 获取字典数据详情
     *
     * @param id 字典数据ID
     * @return 字典数据详情
     */
    DictDataVO dataDetail(Long id);

    /**
     * 新增字典数据
     *
     * @param request 字典数据保存请求
     */
    void createData(DictDataSaveRequest request);

    /**
     * 修改字典数据
     *
     * @param id      字典数据ID
     * @param request 字典数据保存请求
     */
    void updateData(Long id, DictDataSaveRequest request);

    /**
     * 删除字典数据
     *
     * @param id 字典数据ID
     */
    void deleteData(Long id);

    /**
     * 批量删除字典数据
     *
     * @param request 包含字典数据ID列表的请求
     */
    void deleteDataBatch(BatchIdsRequest request);

    /**
     * 按字典编码查询字典项列表（供前端下拉选择使用）
     *
     * @param dictCode 字典编码
     * @return 字典项列表
     */
    List<DictItemVO> items(String dictCode);
}
