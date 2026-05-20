package cn.ezios.baseapi.modules.system.user.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 用户视图对象
 * <p>包含用户基本信息、所属部门及角色列表</p>
 */
@Data
public class UserVO {

    /** 主键ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 头像 */
    private String avatar;

    /** 性别 */
    private String gender;

    /** 部门ID */
    private Long deptId;

    /** 状态 */
    private Integer status;

    /** 是否内置 */
    private Integer isBuiltin;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 最后登录IP */
    private String lastLoginIp;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 所属部门信息 */
    private UserDeptVO dept;

    /** 角色列表 */
    private List<UserRoleVO> roles = new ArrayList<>();
}
