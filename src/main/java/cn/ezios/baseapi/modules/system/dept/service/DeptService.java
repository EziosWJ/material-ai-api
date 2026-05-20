package cn.ezios.baseapi.modules.system.dept.service;

import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.common.model.StatusUpdateRequest;
import cn.ezios.baseapi.modules.system.dept.dto.DeptPageQuery;
import cn.ezios.baseapi.modules.system.dept.dto.DeptSaveRequest;
import cn.ezios.baseapi.modules.system.dept.vo.DeptVO;
import java.util.List;

/**
 * 部门管理服务接口
 * <p>定义部门的树形查询、分页查询及增删改查等业务操作</p>
 */
public interface DeptService {

    /**
     * 获取部门树（包含所有状态）
     *
     * @return 部门树形结构
     */
    List<DeptVO> tree();

    /**
     * 分页查询部门列表
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<DeptVO> page(DeptPageQuery query);

    /**
     * 获取部门选择树（仅启用状态）
     *
     * @return 部门树形结构
     */
    List<DeptVO> options();

    /**
     * 获取部门详情
     *
     * @param id 部门ID
     * @return 部门详情
     */
    DeptVO getDetail(Long id);

    /**
     * 新增部门
     *
     * @param request 部门保存请求
     */
    void create(DeptSaveRequest request);

    /**
     * 修改部门
     *
     * @param id      部门ID
     * @param request 部门保存请求
     */
    void update(Long id, DeptSaveRequest request);

    /**
     * 删除部门
     *
     * @param id 部门ID
     */
    void delete(Long id);

    /**
     * 批量删除部门
     *
     * @param request 包含部门ID列表的请求
     */
    void deleteBatch(BatchIdsRequest request);

    /**
     * 修改部门状态
     *
     * @param id      部门ID
     * @param request 状态更新请求
     */
    void updateStatus(Long id, StatusUpdateRequest request);
}
