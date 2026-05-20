package cn.ezios.baseapi.modules.ai.dto;

import java.util.List;
import lombok.Data;

/**
 * Python AI 服务内容生成响应。
 */
@Data
public class PythonAiGenerateResponse {

    /** 大模型生成的文本内容 */
    private String generatedText;

    /** 生成过程中引用的来源片段列表，用于溯源展示 */
    private List<PythonAiSourceSegment> sourceSegments;
}
