package cn.ezios.baseapi.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("biz_ai_call_log")
public class BizAiCallLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String businessType;

    private Long businessId;

    private String endpoint;

    private String modelName;

    private String materialIdsJson;

    private String requestSummary;

    private String responseSummary;

    private String status;

    private Integer httpStatus;

    private String errorCode;

    private String errorMessage;

    private Integer sourceCount;

    private String traceId;

    private Long durationMs;

    private LocalDateTime startedAt;

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
