package cn.ezios.baseapi.modules.system.dept.service.impl;

import cn.ezios.baseapi.common.enums.ResponseCode;
import cn.ezios.baseapi.common.exception.BusinessException;
import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.common.model.StatusUpdateRequest;
import cn.ezios.baseapi.modules.system.dept.dto.DeptPageQuery;
import cn.ezios.baseapi.modules.system.dept.dto.DeptSaveRequest;
import cn.ezios.baseapi.modules.system.dept.entity.SysDept;
import cn.ezios.baseapi.modules.system.dept.mapper.SysDeptMapper;
import cn.ezios.baseapi.modules.system.dept.service.DeptService;
import cn.ezios.baseapi.modules.system.dept.vo.DeptVO;
import cn.ezios.baseapi.modules.system.user.entity.SysUser;
import cn.ezios.baseapi.modules.system.user.mapper.SysUserMapper;
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
 * 部门管理服务实现
 * <p>提供部门的树形查询、分页查询及增删改查等功能</p>
 */
@Service
public class DeptServiceImpl implements DeptService {

    /** 顶级部门父ID */
    private static final long ROOT_PARENT_ID = 0L;

    /** 启用状态 */
    private static final int STATUS_ENABLED = 1;

    /** 内置部门标志 */
    private static final int BUILTIN = 1;

    /** 部门数据访问 */
    private final SysDeptMapper deptMapper;

    /** 用户数据访问（用于校验部门是否关联用户） */
    private final SysUserMapper userMapper;

    public DeptServiceImpl(SysDeptMapper deptMapper, SysUserMapper userMapper) {
        this.deptMapper = deptMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<DeptVO> tree() {
        return buildTree(deptMapper.selectList(new LambdaQueryWrapper<SysDept>()
                        .orderByAsc(SysDept::getSortOrder)
                        .orderByAsc(SysDept::getId))
                .stream()
                .map(this::toVO)
                .toList());
    }

    @Override
    public PageResult<DeptVO> page(DeptPageQuery query) {
        Page<SysDept> page = deptMapper.selectPage(Page.of(query.getPage(), query.getPageSize()),
                new LambdaQueryWrapper<SysDept>()
                        .like(StringUtils.hasText(query.getDeptName()), SysDept::getDeptName, query.getDeptName())
                        .like(StringUtils.hasText(query.getDeptCode()), SysDept::getDeptCode, query.getDeptCode())
                        .eq(query.getStatus() != null, SysDept::getStatus, query.getStatus())
                        .orderByAsc(SysDept::getSortOrder)
                        .orderByAsc(SysDept::getId));
        return new PageResult<>(page.getRecords().stream().map(this::toVO).toList(),
                page.getTotal(), query.getPage(), query.getPageSize());
    }

    @Override
    public List<DeptVO> options() {
        return buildTree(deptMapper.selectList(new LambdaQueryWrapper<SysDept>()
                        .eq(SysDept::getStatus, STATUS_ENABLED)
                        .orderByAsc(SysDept::getSortOrder)
                        .orderByAsc(SysDept::getId))
                .stream()
                .map(this::toVO)
                .toList());
    }

    @Override
    public DeptVO getDetail(Long id) {
        return toVO(requireDept(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DeptSaveRequest request) {
        ensureDeptCodeUnique(request.getDeptCode(), null);
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(request, dept);
        dept.setStatus(request.getStatus() == null ? STATUS_ENABLED : request.getStatus());
        dept.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        dept.setIsBuiltin(0);
        deptMapper.insert(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, DeptSaveRequest request) {
        SysDept existing = requireDept(id);
        if (Objects.equals(existing.getIsBuiltin(), BUILTIN)
                && !Objects.equals(existing.getDeptCode(), request.getDeptCode())) {
            throw new BusinessException("内置部门禁止修改编码");
        }
        ensureDeptCodeUnique(request.getDeptCode(), id);
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(request, dept);
        dept.setId(id);
        deptMapper.updateById(dept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysDept dept = requireDept(id);
        if (Objects.equals(dept.getIsBuiltin(), BUILTIN)) {
            throw new BusinessException("内置部门禁止删除");
        }
        if (deptMapper.selectCount(new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, id)) > 0) {
            throw new BusinessException("存在子部门，禁止删除");
        }
        if (userMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeptId, id)) > 0) {
            throw new BusinessException("部门已关联用户，禁止删除");
        }
        deptMapper.deleteById(id);
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
        requireDept(id);
        SysDept dept = new SysDept();
        dept.setId(id);
        dept.setStatus(request.getStatus());
        deptMapper.updateById(dept);
    }

    /**
     * 根据ID获取部门，不存在则抛出异常
     */
    private SysDept requireDept(Long id) {
        SysDept dept = deptMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        return dept;
    }

    /**
     * 校验部门编码唯一性
     */
    private void ensureDeptCodeUnique(String deptCode, Long excludeId) {
        Long count = deptMapper.selectCount(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getDeptCode, deptCode)
                .ne(excludeId != null, SysDept::getId, excludeId));
        if (count > 0) {
            throw new BusinessException("部门编码已存在");
        }
    }

    /**
     * 构建部门树形结构
     */
    private List<DeptVO> buildTree(List<DeptVO> depts) {
        Map<Long, DeptVO> deptMap = new LinkedHashMap<>();
        for (DeptVO dept : depts) {
            deptMap.put(dept.getId(), dept);
        }
        List<DeptVO> roots = new ArrayList<>();
        for (DeptVO dept : depts) {
            DeptVO parent = deptMap.get(dept.getParentId());
            if (parent == null || Objects.equals(dept.getParentId(), ROOT_PARENT_ID)) {
                roots.add(dept);
            } else {
                parent.getChildren().add(dept);
            }
        }
        sortTree(roots);
        return roots;
    }

    /**
     * 递归排序部门树
     */
    private void sortTree(List<DeptVO> depts) {
        depts.sort(Comparator.comparing(DeptVO::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(DeptVO::getId, Comparator.nullsLast(Long::compareTo)));
        for (DeptVO dept : depts) {
            sortTree(dept.getChildren());
        }
    }

    /**
     * 实体转VO
     */
    private DeptVO toVO(SysDept dept) {
        DeptVO vo = new DeptVO();
        BeanUtils.copyProperties(dept, vo);
        return vo;
    }
}
