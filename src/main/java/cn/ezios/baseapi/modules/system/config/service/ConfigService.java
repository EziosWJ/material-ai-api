package cn.ezios.baseapi.modules.system.config.service;

import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.common.model.StatusUpdateRequest;
import cn.ezios.baseapi.modules.system.config.vo.ConfigByKeyVO;
import cn.ezios.baseapi.modules.system.config.dto.ConfigPageQuery;
import cn.ezios.baseapi.modules.system.config.dto.ConfigSaveRequest;
import cn.ezios.baseapi.modules.system.config.vo.ConfigVO;

/**
 * 系统配置服务接口
 * <p>定义系统参数配置的业务操作</p>
 */
public interface ConfigService {

    /**
     * 分页查询配置列表
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<ConfigVO> page(ConfigPageQuery query);

    /**
     * 获取配置详情
     *
     * @param id 配置ID
     * @return 配置详情
     */
    ConfigVO getDetail(Long id);

    /**
     * 按配置键查询配置
     *
     * @param configKey 配置键
     * @return 配置信息
     */
    ConfigByKeyVO getByKey(String configKey);

    /**
     * 新增配置
     *
     * @param request 配置保存请求
     */
    void create(ConfigSaveRequest request);

    /**
     * 修改配置
     *
     * @param id      配置ID
     * @param request 配置保存请求
     */
    void update(Long id, ConfigSaveRequest request);

    /**
     * 删除配置
     *
     * @param id 配置ID
     */
    void delete(Long id);

    /**
     * 批量删除配置
     *
     * @param request 包含配置ID列表的请求
     */
    void deleteBatch(BatchIdsRequest request);

    /**
     * 修改配置状态
     *
     * @param id      配置ID
     * @param request 状态更新请求
     */
    void updateStatus(Long id, StatusUpdateRequest request);
}
