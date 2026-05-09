package cn.ezios.baseapi.modules.material.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MaterialVO {

    private Long id;

    private Long userId;

    private String title;

    private String originalFilename;

    private Long fileId;

    private String fileType;

    private Long fileSize;

    private String fileMd5;

    private String storagePath;

    private String status;

    private Integer segmentCount;

    private LocalDateTime lastProcessTime;

    private String errorMessage;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
