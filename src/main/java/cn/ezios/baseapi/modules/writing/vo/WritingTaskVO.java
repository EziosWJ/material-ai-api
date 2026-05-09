package cn.ezios.baseapi.modules.writing.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class WritingTaskVO {

    private Long id;

    private Long userId;

    private String title;

    private String writingType;

    private String topic;

    private String requirement;

    private String inputContent;

    private String status;

    private String errorMessage;

    private List<Long> materialIds;

    private WritingResultVO result;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
