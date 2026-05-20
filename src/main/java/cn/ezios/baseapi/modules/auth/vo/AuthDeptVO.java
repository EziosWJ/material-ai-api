package cn.ezios.baseapi.modules.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 当前用户所属部门信息视图对象。
 */
@Data
@Schema(description = "当前用户部门信息")
public class AuthDeptVO {

    /** 部门 ID */
    @Schema(description = "部门 ID")
    private Long id;

    /** 部门名称 */
    @Schema(description = "部门名称")
    private String deptName;

    /** 部门编码 */
    @Schema(description = "部门编码")
    private String deptCode;
}
