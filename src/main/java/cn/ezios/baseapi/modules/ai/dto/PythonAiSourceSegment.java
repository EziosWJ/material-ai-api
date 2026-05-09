package cn.ezios.baseapi.modules.ai.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PythonAiSourceSegment {

    private String text;

    private Long materialId;

    private Integer segmentIndex;

    private BigDecimal score;
}
