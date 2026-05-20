package cn.ezios.baseapi.modules.system.role.service;

import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.common.model.StatusUpdateRequest;
import cn.ezios.baseapi.modules.system.role.dto.RoleMenuRequest;
import cn.ezios.baseapi.modules.system.role.dto.RolePageQuery;
import cn.ezios.baseapi.modules.system.role.dto.RoleSaveRequest;
import cn.ezios.baseapi.modules.system.role.vo.RoleVO;
import java.util.List;

/**
 * 角色管理服务接口
 * <p>定义角色的增删改查、状态管理及菜单分配等业务操作</p>
 */
public interface RoleService {

    /**
     * 分页查询角色列表
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<RoleVO> page(RolePageQuery query);

    /**
     * 获取角色详情（含已分配菜单ID列表）
     *
     * @param id 角色ID
     * @return 角色详情
     */
    RoleVO getDetail(Long id);

    /**
     * 新增角色
     *
     * @param request 角色保存请求
     */
    void create(RoleSaveRequest request);

    /**
     * 修改角色
     *
     * @param id      角色ID
     * @param request 角色保存请求
     */
    void update(Long id, RoleSaveRequest request);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void delete(Long id);

    /**
     * 批量删除角色
     *
     * @param request 包含角色ID列表的请求
     */
    void deleteBatch(BatchIdsRequest request);

    /**
     * 修改角色状态
     *
     * @param id      角色ID
     * @param request 状态更新请求
     */
    void updateStatus(Long id, StatusUpdateRequest request);

    /**
     * 分配角色菜单
     *
     * @param id      角色ID
     * @param request 角色菜单分配请求
     */
    void assignMenus(Long id, RoleMenuRequest request);

    /**
     * 获取角色选择列表（仅启用状态）
     *
     * @return 角色列表
     */
    List<RoleVO> options();
}
