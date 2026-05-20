package cn.ezios.baseapi.modules.system.dept.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 部门保存请求
 */
@Data
public class DeptSaveRequest {

    /** 父级部门ID，0表示顶级部门 */
    @NotNull(message = "父级部门不能为空")
    private Long parentId;

    /** 部门名称 */
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 100, message = "部门名称长度不能超过 100")
    private String deptName;

    /** 部门编码，全局唯一 */
    @NotBlank(message = "部门编码不能为空")
    @Size(max = 50, message = "部门编码长度不能超过 50")
    private String deptCode;

    /** 负责人 */
    private String leader;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 排序序号 */
    private Integer sortOrder;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 备注 */
    private String remark;
}
