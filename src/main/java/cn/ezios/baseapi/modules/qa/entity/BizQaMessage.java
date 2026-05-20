package cn.ezios.baseapi.modules.qa.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 问答消息实体，对应 biz_qa_message 表，记录会话中每条用户或助手的消息。
 */
@Data
@TableName("biz_qa_message")
public class BizQaMessage {

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属会话 ID */
    private Long sessionId;

    /** 所属用户 ID */
    private Long userId;

    /** 消息角色：user / assistant / system */
    private String role;

    /** 消息内容 */
    private String content;

    /** 来源片段 JSON，仅助手消息携带 */
    private String sourceSegmentsJson;

    /** 使用的模型名称 */
    private String modelName;

    /** 关联的 AI 调用日志 ID */
    private Long aiCallLogId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /** 逻辑删除标记：0 未删除，1 已删除 */
    @TableLogic
    private Integer deleted;
}
