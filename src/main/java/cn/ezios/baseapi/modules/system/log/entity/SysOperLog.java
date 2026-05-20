package cn.ezios.baseapi.modules.system.log.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 操作日志实体
 * <p>对应数据库表 sys_oper_log，记录用户操作行为</p>
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 模块名称 */
    private String moduleName;

    /** 操作类型 */
    private String operationType;

    /** 请求方式 */
    private String requestMethod;

    /** 请求URL */
    private String requestUrl;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人名称 */
    private String operatorName;

    /** 操作人IP */
    private String operatorIp;

    /** 操作人地点 */
    private String operatorLocation;

    /** 请求参数 */
    private String requestParams;

    /** 响应结果 */
    private String responseResult;

    /** 耗时（毫秒） */
    private Long costTime;

    /** 操作状态 */
    private String operationStatus;

    /** 错误信息 */
    private String errorMessage;

    /** 操作时间 */
    private LocalDateTime operationTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
