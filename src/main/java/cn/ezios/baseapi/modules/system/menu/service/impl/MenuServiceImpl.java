package cn.ezios.baseapi.modules.system.menu.service.impl;

import cn.ezios.baseapi.common.enums.ResponseCode;
import cn.ezios.baseapi.common.exception.BusinessException;
import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.common.model.StatusUpdateRequest;
import cn.ezios.baseapi.modules.system.menu.dto.MenuPageQuery;
import cn.ezios.baseapi.modules.system.menu.dto.MenuSaveRequest;
import cn.ezios.baseapi.modules.system.menu.entity.SysMenu;
import cn.ezios.baseapi.modules.system.menu.mapper.SysMenuMapper;
import cn.ezios.baseapi.modules.system.menu.service.MenuService;
import cn.ezios.baseapi.modules.system.menu.vo.MenuVO;
import cn.ezios.baseapi.modules.system.role.entity.SysRoleMenu;
import cn.ezios.baseapi.modules.system.role.mapper.SysRoleMenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 菜单管理服务实现
 * <p>提供菜单的树形查询、分页查询及增删改查等功能</p>
 */
@Service
public class MenuServiceImpl implements MenuService {

    /** 顶级菜单父ID */
    private static final long ROOT_PARENT_ID = 0L;

    /** 启用状态 */
    private static final int STATUS_ENABLED = 1;

    /** 可见状态 */
    private static final int VISIBLE = 1;

    /** 内置菜单标志 */
    private static final int BUILTIN = 1;

    /** 菜单数据访问 */
    private final SysMenuMapper menuMapper;

    /** 角色菜单关联数据访问（用于校验菜单是否绑定角色） */
    private final SysRoleMenuMapper roleMenuMapper;

    public MenuServiceImpl(SysMenuMapper menuMapper, SysRoleMenuMapper roleMenuMapper) {
        this.menuMapper = menuMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public List<MenuVO> tree() {
        List<MenuVO> menus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                        .orderByAsc(SysMenu::getSortOrder)
                        .orderByAsc(SysMenu::getId))
                .stream()
                .map(this::toVO)
                .toList();
        return buildTree(menus);
    }

    @Override
    public PageResult<MenuVO> page(MenuPageQuery query) {
        Page<SysMenu> page = menuMapper.selectPage(Page.of(query.getPage(), query.getPageSize()),
                new LambdaQueryWrapper<SysMenu>()
                        .like(StringUtils.hasText(query.getMenuName()), SysMenu::getMenuName, query.getMenuName())
                        .eq(StringUtils.hasText(query.getMenuType()), SysMenu::getMenuType, query.getMenuType())
                        .eq(query.getStatus() != null, SysMenu::getStatus, query.getStatus())
                        .eq(query.getVisible() != null, SysMenu::getVisible, query.getVisible())
                        .orderByAsc(SysMenu::getSortOrder)
                        .orderByAsc(SysMenu::getId));
        return new PageResult<>(page.getRecords().stream().map(this::toVO).toList(),
                page.getTotal(), query.getPage(), query.getPageSize());
    }

    @Override
    public MenuVO getDetail(Long id) {
        return toVO(requireMenu(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(MenuSaveRequest request) {
        ensurePermissionUnique(request.getPermissionCode(), null);
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(request, menu);
        menu.setStatus(request.getStatus() == null ? STATUS_ENABLED : request.getStatus());
        menu.setVisible(request.getVisible() == null ? VISIBLE : request.getVisible());
        menu.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        menu.setIsBuiltin(0);
        menuMapper.insert(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, MenuSaveRequest request) {
        SysMenu existing = requireMenu(id);
        if (Objects.equals(existing.getIsBuiltin(), BUILTIN)
                && !Objects.equals(existing.getPermissionCode(), request.getPermissionCode())) {
            throw new BusinessException("内置菜单禁止修改权限编码");
        }
        ensurePermissionUnique(request.getPermissionCode(), id);
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(request, menu);
        menu.setId(id);
        menuMapper.updateById(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysMenu menu = requireMenu(id);
        if (Objects.equals(menu.getIsBuiltin(), BUILTIN)) {
            throw new BusinessException("内置菜单禁止删除");
        }
        assertNoChildren(id);
        assertNoRoleBinding(id);
        menuMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(BatchIdsRequest request) {
        for (Long id : request.getIds()) {
            delete(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, StatusUpdateRequest request) {
        requireMenu(id);
        SysMenu menu = new SysMenu();
        menu.setId(id);
        menu.setStatus(request.getStatus());
        menuMapper.updateById(menu);
    }

    /**
     * 根据ID获取菜单，不存在则抛出异常
     */
    private SysMenu requireMenu(Long id) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        return menu;
    }

    /**
     * 校验权限编码唯一性
     */
    private void ensurePermissionUnique(String permissionCode, Long excludeId) {
        if (!StringUtils.hasText(permissionCode)) {
            return;
        }
        Long count = menuMapper.selectCount(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getPermissionCode, permissionCode)
                .ne(excludeId != null, SysMenu::getId, excludeId));
        if (count > 0) {
            throw new BusinessException("权限编码已存在");
        }
    }

    /**
     * 校验菜单是否存在子菜单
     */
    private void assertNoChildren(Long id) {
        if (menuMapper.selectCount(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id)) > 0) {
            throw new BusinessException("存在子菜单，禁止删除");
        }
    }

    /**
     * 校验菜单是否绑定角色
     */
    private void assertNoRoleBinding(Long id) {
        if (roleMenuMapper.selectCount(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, id)) > 0) {
            throw new BusinessException("菜单已绑定角色，禁止删除");
        }
    }

    /**
     * 构建菜单树形结构
     */
    private List<MenuVO> buildTree(List<MenuVO> menus) {
        Map<Long, MenuVO> menuMap = new LinkedHashMap<>();
        for (MenuVO menu : menus) {
            menuMap.put(menu.getId(), menu);
        }
        List<MenuVO> roots = new ArrayList<>();
        for (MenuVO menu : menus) {
            MenuVO parent = menuMap.get(menu.getParentId());
            if (parent == null || Objects.equals(menu.getParentId(), ROOT_PARENT_ID)) {
                roots.add(menu);
            } else {
                parent.getChildren().add(menu);
            }
        }
        sortTree(roots);
        return roots;
    }

    /**
     * 递归排序菜单树
     */
    private void sortTree(List<MenuVO> menus) {
        menus.sort(Comparator.comparing(MenuVO::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(MenuVO::getId, Comparator.nullsLast(Long::compareTo)));
        for (MenuVO menu : menus) {
            sortTree(menu.getChildren());
        }
    }

    /**
     * 实体转VO
     */
    private MenuVO toVO(SysMenu menu) {
        MenuVO vo = new MenuVO();
        BeanUtils.copyProperties(menu, vo);
        return vo;
    }
}
