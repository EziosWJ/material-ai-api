package cn.ezios.baseapi.modules.writing.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class WritingResultVO {

    private Long id;

    private Long taskId;

    private Long userId;

    private Integer versionNo;

    private String content;

    private String sourceSegmentsJson;

    private List<WritingSourceSegmentVO> sourceSegments;

    private String modelName;

    private Long aiCallLogId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
