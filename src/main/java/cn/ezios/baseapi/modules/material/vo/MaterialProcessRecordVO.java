package cn.ezios.baseapi.modules.material.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MaterialProcessRecordVO {

    private Long id;

    private Long materialId;

    private Long userId;

    private Long fileId;

    private String fileMd5;

    private String originalFilename;

    private String processType;

    private String status;

    private Integer deletedCount;

    private Integer segmentCount;

    private String errorMessage;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private Long durationMs;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
