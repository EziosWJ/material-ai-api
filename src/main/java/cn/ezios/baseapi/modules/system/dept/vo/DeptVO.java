package cn.ezios.baseapi.modules.system.dept.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 部门视图对象
 * <p>支持树形结构展示，包含子部门列表</p>
 */
@Data
public class DeptVO {

    /** 主键ID */
    private Long id;

    /** 父级部门ID */
    private Long parentId;

    /** 部门名称 */
    private String deptName;

    /** 部门编码 */
    private String deptCode;

    /** 负责人 */
    private String leader;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 排序序号 */
    private Integer sortOrder;

    /** 状态 */
    private Integer status;

    /** 是否内置 */
    private Integer isBuiltin;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 子部门列表 */
    private List<DeptVO> children = new ArrayList<>();
}
