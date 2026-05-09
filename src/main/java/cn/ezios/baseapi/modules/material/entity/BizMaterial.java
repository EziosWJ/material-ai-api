package cn.ezios.baseapi.modules.material.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("biz_material")
public class BizMaterial {

    @TableId(type = IdType.AUTO)
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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @TableLogic
    private Integer deleted;
}
