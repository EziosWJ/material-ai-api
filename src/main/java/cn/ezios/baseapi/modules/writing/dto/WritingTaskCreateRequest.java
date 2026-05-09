package cn.ezios.baseapi.modules.writing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class WritingTaskCreateRequest {

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 200, message = "任务标题长度不能超过 200")
    private String title;

    @NotBlank(message = "写作类型不能为空")
    private String writingType;

    @NotBlank(message = "写作主题不能为空")
    @Size(max = 500, message = "写作主题长度不能超过 500")
    private String topic;

    private String requirement;

    private String inputContent;

    private List<Long> materialIds;

    private Integer topK;
}
