package cn.ezios.baseapi.modules.ai.dto;

import java.math.BigDecimal;
import lombok.Data;

/**
 * 来源片段。
 * <p>AI 生成或问答结果中引用的材料片段，用于向用户展示溯源信息。</p>
 */
@Data
public class PythonAiSourceSegment {

    /** 片段文本内容 */
    private String text;

    /** 所属材料 ID */
    private Long materialId;

    /** 片段在材料中的序号 */
    private Integer segmentIndex;

    /** 向量检索相似度分数 */
    private BigDecimal score;
}
