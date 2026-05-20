package cn.ezios.baseapi.modules.system.user.vo;

import lombok.Data;

/**
 * 用户所属部门信息
 * <p>嵌套在用户详情中展示</p>
 */
@Data
public class UserDeptVO {

    /** 部门ID */
    private Long id;

    /** 部门名称 */
    private String deptName;

    /** 部门编码 */
    private String deptCode;
}
