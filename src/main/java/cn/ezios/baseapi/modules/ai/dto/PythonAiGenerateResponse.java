package cn.ezios.baseapi.modules.ai.dto;

import java.util.List;
import lombok.Data;

@Data
public class PythonAiGenerateResponse {

    private String generatedText;

    private List<PythonAiSourceSegment> sourceSegments;
}
