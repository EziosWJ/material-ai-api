package cn.ezios.baseapi.modules.system.menu.service;

import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.common.model.StatusUpdateRequest;
import cn.ezios.baseapi.modules.system.menu.dto.MenuPageQuery;
import cn.ezios.baseapi.modules.system.menu.dto.MenuSaveRequest;
import cn.ezios.baseapi.modules.system.menu.vo.MenuVO;
import java.util.List;

/**
 * 菜单管理服务接口
 * <p>定义菜单的树形查询、分页查询及增删改查等业务操作</p>
 */
public interface MenuService {

    /**
     * 获取菜单树
     *
     * @return 菜单树形结构
     */
    List<MenuVO> tree();

    /**
     * 分页查询菜单列表
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<MenuVO> page(MenuPageQuery query);

    /**
     * 获取菜单详情
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    MenuVO getDetail(Long id);

    /**
     * 新增菜单
     *
     * @param request 菜单保存请求
     */
    void create(MenuSaveRequest request);

    /**
     * 修改菜单
     *
     * @param id      菜单ID
     * @param request 菜单保存请求
     */
    void update(Long id, MenuSaveRequest request);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    void delete(Long id);

    /**
     * 批量删除菜单
     *
     * @param request 包含菜单ID列表的请求
     */
    void deleteBatch(BatchIdsRequest request);

    /**
     * 修改菜单状态
     *
     * @param id      菜单ID
     * @param request 状态更新请求
     */
    void updateStatus(Long id, StatusUpdateRequest request);
}
