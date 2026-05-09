package cn.ezios.baseapi.modules.ai.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class AiCallLogCreateRequest {

    private Long userId;

    private String businessType;

    private Long businessId;

    private String endpoint;

    private String modelName;

    private List<Long> materialIds;

    private String requestSummary;

    private String responseSummary;

    private String status;

    private Integer httpStatus;

    private String errorCode;

    private String errorMessage;

    private Integer sourceCount;

    private String traceId;

    private Long durationMs;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;
}
