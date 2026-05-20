package cn.ezios.baseapi.modules.system.file.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 文件视图对象
 */
@Data
public class FileVO {

    /** 主键ID */
    private Long id;

    /** 原始文件名 */
    private String originalName;

    /** 存储文件名 */
    private String storageName;

    /** 文件扩展名 */
    private String extension;

    /** MIME类型 */
    private String mimeType;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件MD5摘要 */
    private String fileMd5;

    /** 存储相对路径 */
    private String storagePath;

    /** 访问URL */
    private String accessUrl;

    /** 业务模块标识 */
    private String businessModule;

    /** 状态 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
