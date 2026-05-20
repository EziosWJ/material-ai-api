package cn.ezios.baseapi.modules.system.log.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 登录日志实体
 * <p>对应数据库表 sys_login_log，记录用户登录行为</p>
 */
@Data
@TableName("sys_login_log")
public class SysLoginLog {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
