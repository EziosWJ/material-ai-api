package cn.ezios.baseapi.modules.ai.dto;

import java.util.List;
import lombok.Data;

/**
 * Python AI 服务内容生成请求。
 * <p>用于发起写作任务，支持多种写作类型（如 polished 润色），可指定参考材料进行 RAG 生成。</p>
 */
@Data
public class PythonAiGenerateRequest {

    /** 写作类型，如 polished（润色） */
    private String type;

    /** 写作主题 */
    private String topic;

    /** 用户 ID，用于限定材料检索范围 */
    private Long userId;

    /** 待润色的原文内容，润色类型时必填 */
    private String content;

    /** 限定检索的材料 ID 列表，为空则检索用户全部材料 */
    private List<Long> materialIds;

    /** 向量检索返回的最大片段数量，范围 1-20 */
    private Integer topK;
}
