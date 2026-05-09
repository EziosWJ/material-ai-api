package cn.ezios.baseapi.modules.ai.dto;

import java.util.List;
import lombok.Data;

@Data
public class PythonAiGenerateRequest {

    private String type;

    private String topic;

    private Long userId;

    private String content;

    private List<Long> materialIds;

    private Integer topK;
}
