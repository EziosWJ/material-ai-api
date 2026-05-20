package cn.ezios.baseapi.modules.system.user.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQuery extends PageQuery {

    /** 用户名，模糊匹配 */
    private String username;

    /** 昵称，模糊匹配 */
    private String nickname;

    /** 手机号，模糊匹配 */
    private String phone;

    /** 邮箱，模糊匹配 */
    private String email;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 部门ID */
    private Long deptId;
}
