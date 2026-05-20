package cn.ezios.baseapi.modules.writing.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 写作任务与材料关联实体，对应 biz_writing_task_material 表，记录任务引用了哪些材料。
 */
@Data
@TableName("biz_writing_task_material")
public class BizWritingTaskMaterial {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 写作任务 ID */
    private Long taskId;

    /** 材料 ID */
    private Long materialId;

    /** 用户 ID */
    private Long userId;

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
