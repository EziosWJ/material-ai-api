package cn.ezios.baseapi.modules.system.log.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 登录日志视图对象
 */
@Data
public class LoginLogVO {

    /** 主键ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 登录状态 */
    private String loginStatus;

    /** 登录IP */
    private String loginIp;

    /** 登录地点 */
    private String loginLocation;

    /** 浏览器 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** User-Agent */
    private String userAgent;

    /** 提示消息 */
    private String message;

    /** 登录时间 */
    private LocalDateTime loginTime;

    /** 创建时间 */
    private LocalDateTime createTime;
}
