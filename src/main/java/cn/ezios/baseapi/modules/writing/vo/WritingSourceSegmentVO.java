package cn.ezios.baseapi.modules.writing.vo;

import java.math.BigDecimal;
import lombok.Data;

/**
 * 写作来源片段视图对象，展示 AI 生成内容所引用的材料片段及溯源信息。
 */
@Data
public class WritingSourceSegmentVO {

    /** 片段文本内容 */
    private String text;

    /** 来源材料 ID */
    private Long materialId;

    /** 来源材料标题 */
    private String materialTitle;

    /** 来源材料原始文件名 */
    private String originalFilename;

    /** 片段在材料中的序号 */
    private Integer segmentIndex;

    /** 向量检索相似度得分 */
    private BigDecimal score;
}
