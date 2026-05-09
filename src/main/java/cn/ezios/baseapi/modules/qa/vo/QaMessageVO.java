package cn.ezios.baseapi.modules.qa.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class QaMessageVO {

    private Long id;

    private Long sessionId;

    private String role;

    private String content;

    private List<QaSourceSegmentVO> sourceSegments;

    private String modelName;

    private Long aiCallLogId;

    private LocalDateTime createTime;
}
