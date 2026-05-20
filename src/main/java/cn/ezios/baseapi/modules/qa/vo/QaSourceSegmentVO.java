package cn.ezios.baseapi.modules.qa.vo;

import java.math.BigDecimal;
import lombok.Data;

/**
 * 来源片段 VO，展示 AI 回答所引用的材料片段，用于溯源展示。
 */
@Data
public class QaSourceSegmentVO {

    /** 片段文本内容 */
    private String text;

    /** 来源材料 ID */
    private Long materialId;

    /** 片段在材料中的序号 */
    private Integer segmentIndex;

    /** 向量检索相似度分数 */
    private BigDecimal score;

    /** 来源材料标题 */
    private String materialTitle;

    /** 来源材料原始文件名 */
    private String originalFilename;
}
