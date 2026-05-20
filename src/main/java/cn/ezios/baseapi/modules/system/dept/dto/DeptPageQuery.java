package cn.ezios.baseapi.modules.system.dept.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptPageQuery extends PageQuery {

    /** 部门名称，模糊匹配 */
    private String deptName;

    /** 部门编码，模糊匹配 */
    private String deptCode;

    /** 状态：1-启用，0-禁用 */
    private Integer status;
}
