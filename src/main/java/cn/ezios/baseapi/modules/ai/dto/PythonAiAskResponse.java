package cn.ezios.baseapi.modules.ai.dto;

import java.util.List;
import lombok.Data;

@Data
public class PythonAiAskResponse {

    private String answer;

    private List<PythonAiSourceSegment> sourceSegments;
}
