package cn.ezios.baseapi.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * AI 调用日志实体。
 * <p>记录每次调用 Python AI 服务的完整信息，用于审计和问题排查。对应数据库表 {@code biz_ai_call_log}。</p>
 */
@Data
@TableName("biz_ai_call_log")
public class BizAiCallLog {

    /** 主键 ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发起调用的用户 ID */
    private Long userId;

    /** 业务类型，如 writing、qa */
    private String businessType;

    /** 关联的业务 ID */
    private Long businessId;

    /** 调用的 Python AI 服务端点路径 */
    private String endpoint;

    /** 使用的模型名称 */
    private String modelName;

    /** 涉及的材料 ID 列表，JSON 格式存储 */
    private String materialIdsJson;

    /** 请求摘要 */
    private String requestSummary;

    /** 响应摘要 */
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @TableLogic
    private Integer deleted;
}
