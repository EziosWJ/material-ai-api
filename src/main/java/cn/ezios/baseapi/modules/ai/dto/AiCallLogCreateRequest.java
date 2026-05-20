package cn.ezios.baseapi.modules.ai.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * AI 调用日志创建请求。
 * <p>用于记录每次调用 Python AI 服务的完整信息，包括调用方、业务关联、请求响应摘要和耗时等。</p>
 */
@Data
public class AiCallLogCreateRequest {

    /** 发起调用的用户 ID */
    private Long userId;

    /** 业务类型，如 writing、qa */
    private String businessType;

    /** 关联的业务 ID，如写作任务 ID 或问答记录 ID */
    private Long businessId;

    /** 调用的 Python AI 服务端点路径 */
    private String endpoint;

    /** 使用的模型名称 */
    private String modelName;

    /** 涉及的材料 ID 列表 */
    private List<Long> materialIds;

    /** 请求摘要，用于快速回溯请求内容 */
    private String requestSummary;

    /** 响应摘要，用于快速回溯响应内容 */
    private String responseSummary;

    /** 调用状态，如 success、failed */
    private String status;

    /** HTTP 响应状态码 */
    private Integer httpStatus;

    /** 错误码 */
    private String errorCode;

    /** 错误信息 */
    private String errorMessage;

    /** 返回的来源片段数量 */
    private Integer sourceCount;

    /** 链路追踪 ID */
    private String traceId;

    /** 调用耗时（毫秒） */
    private Long durationMs;

    /** 调用开始时间 */
    private LocalDateTime startedAt;

    /** 调用结束时间 */
    private LocalDateTime finishedAt;
}
