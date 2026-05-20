package cn.ezios.baseapi.modules.writing.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 写作任务视图对象，向前端展示写作任务的完整信息，包括关联材料和生成结果。
 */
@Data
public class WritingTaskVO {

    private Long id;

    private Long userId;

    private String title;

    /** 写作类型：outline、draft、polished、title */
    private String writingType;

    private String topic;

    private String requirement;

    private String inputContent;

    /** 任务状态：pending、running、success、failed */
    private String status;

    /** 失败时的错误信息 */
    private String errorMessage;

    /** 关联的材料 ID 列表 */
    private List<Long> materialIds;

    /** 最新写作结果 */
    private WritingResultVO result;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
