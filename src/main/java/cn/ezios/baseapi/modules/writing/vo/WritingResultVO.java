package cn.ezios.baseapi.modules.writing.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 写作结果视图对象，用于向前端展示 AI 生成的写作内容及来源片段。
 */
@Data
public class WritingResultVO {

    private Long id;

    private Long taskId;

    private Long userId;

    /** 结果版本号 */
    private Integer versionNo;

    /** AI 生成的写作内容 */
    private String content;

    /** 来源片段 JSON（原始存储格式） */
    private String sourceSegmentsJson;

    /** 来源片段列表（解析后用于前端展示溯源信息） */
    private List<WritingSourceSegmentVO> sourceSegments;

    /** 使用的模型名称 */
    private String modelName;

    /** 关联的 AI 调用日志 ID */
    private Long aiCallLogId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
