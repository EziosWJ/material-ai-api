package cn.ezios.baseapi.modules.writing.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class WritingSourceSegmentVO {

    private String text;

    private Long materialId;

    private String materialTitle;

    private String originalFilename;

    private Integer segmentIndex;

    private BigDecimal score;
}
