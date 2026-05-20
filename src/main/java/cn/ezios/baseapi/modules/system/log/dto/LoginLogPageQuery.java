package cn.ezios.baseapi.modules.system.log.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginLogPageQuery extends PageQuery {

    /** 用户名，模糊匹配 */
    private String username;

    /** 登录状态 */
    private String loginStatus;

    /** 登录IP，模糊匹配 */
    private String loginIp;
}
