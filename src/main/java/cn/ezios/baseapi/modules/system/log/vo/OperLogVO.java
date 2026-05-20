package cn.ezios.baseapi.modules.system.log.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 操作日志视图对象
 */
@Data
public class OperLogVO {

    /** 主键ID */
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
    private LocalDateTime createTime;
}
