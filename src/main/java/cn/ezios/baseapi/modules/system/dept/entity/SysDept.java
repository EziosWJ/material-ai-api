package cn.ezios.baseapi.modules.system.dept.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 部门实体
 * <p>对应数据库表 sys_dept，支持树形层级结构</p>
 */
@Data
@TableName("sys_dept")
public class SysDept {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父级部门ID，0表示顶级部门 */
    private Long parentId;

    /** 部门名称 */
    private String deptName;

    /** 部门编码，全局唯一 */
    private String deptCode;

    /** 负责人 */
    private String leader;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 排序序号 */
    private Integer sortOrder;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 是否内置：1-内置，0-非内置 */
    private Integer isBuiltin;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 创建人ID */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /** 更新人ID */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /** 逻辑删除标志：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;
}
