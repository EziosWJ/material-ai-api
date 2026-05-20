package cn.ezios.baseapi.modules.ai.dto;

import java.util.List;
import lombok.Data;

/**
 * Python AI 服务材料问答响应。
 */
@Data
public class PythonAiAskResponse {

    /** 大模型生成的回答文本 */
    private String answer;

    /** 回答引用的来源片段列表，用于溯源展示 */
    private List<PythonAiSourceSegment> sourceSegments;
}
