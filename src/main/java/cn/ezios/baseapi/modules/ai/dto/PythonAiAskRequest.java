package cn.ezios.baseapi.modules.ai.dto;

import java.util.List;
import lombok.Data;

/**
 * Python AI 服务材料问答请求。
 * <p>用户基于已上传的材料提出问题，由 AI 服务检索相关片段并生成回答。</p>
 */
@Data
public class PythonAiAskRequest {

    /** 用户提出的问题 */
    private String query;

    /** 用户 ID，用于限定材料检索范围 */
    private Long userId;

    /** 限定检索的材料 ID 列表，为空则检索用户全部材料 */
    private List<Long> materialIds;

    /** 向量检索返回的最大片段数量，范围 1-20 */
    private Integer topK;
}
