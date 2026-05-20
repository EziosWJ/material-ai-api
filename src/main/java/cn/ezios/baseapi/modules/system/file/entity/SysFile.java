package cn.ezios.baseapi.modules.system.file.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 文件实体
 * <p>对应数据库表 sys_file，记录上传文件的元信息</p>
 */
@Data
@TableName("sys_file")
public class SysFile {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 原始文件名 */
    private String originalName;

    /** 存储文件名（UUID生成） */
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

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 创建人ID */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /** 更新人ID */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /** 逻辑删除标志：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;
}
