package cn.ezios.baseapi.modules.writing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

/**
 * 写作任务创建请求 DTO，用于接收前端提交的写作任务参数。
 */
@Data
public class WritingTaskCreateRequest {

    /** 任务标题 */
    @NotBlank(message = "任务标题不能为空")
    @Size(max = 200, message = "任务标题长度不能超过 200")
    private String title;

    /** 写作类型：outline（大纲）、draft（草稿）、polished（润色）、title（标题） */
    @NotBlank(message = "写作类型不能为空")
    private String writingType;

    /** 写作主题 */
    @NotBlank(message = "写作主题不能为空")
    @Size(max = 500, message = "写作主题长度不能超过 500")
    private String topic;

    /** 写作要求，可选 */
    private String requirement;

    /** 用户输入内容，润色类型写作时必填 */
    private String inputContent;

    /** 关联的材料 ID 列表，用于向量检索提供参考 */
    private List<Long> materialIds;

    /** 向量检索返回片段数量上限，范围 1-20 */
    private Integer topK;
}
