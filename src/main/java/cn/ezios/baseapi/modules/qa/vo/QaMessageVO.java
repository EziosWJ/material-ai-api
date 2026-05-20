package cn.ezios.baseapi.modules.qa.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 问答消息 VO，用于展示会话中的单条消息。
 */
@Data
public class QaMessageVO {

    /** 消息 ID */
    private Long id;

    /** 所属会话 ID */
    private Long sessionId;

    /** 消息角色：user / assistant / system */
    private String role;

    /** 消息内容 */
    private String content;

    /** 来源片段列表，仅助手消息携带，用于溯源展示 */
    private List<QaSourceSegmentVO> sourceSegments;

    /** 使用的模型名称 */
    private String modelName;

    /** 关联的 AI 调用日志 ID */
    private Long aiCallLogId;

    /** 创建时间 */
    private LocalDateTime createTime;
}
