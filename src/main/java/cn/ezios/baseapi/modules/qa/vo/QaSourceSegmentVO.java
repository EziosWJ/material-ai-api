package cn.ezios.baseapi.modules.qa.vo;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class QaSourceSegmentVO {

    private String text;

    private Long materialId;

    private Integer segmentIndex;

    private BigDecimal score;

    private String materialTitle;

    private String originalFilename;
}
