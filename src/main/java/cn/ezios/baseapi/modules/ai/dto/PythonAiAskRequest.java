package cn.ezios.baseapi.modules.ai.dto;

import java.util.List;
import lombok.Data;

@Data
public class PythonAiAskRequest {

    private String query;

    private Long userId;

    private List<Long> materialIds;

    private Integer topK;
}
