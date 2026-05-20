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
 * 写作任务实体，对应 biz_writing_task 表，记录一次写作任务的完整生命周期。
 */
@Data
@TableName("biz_writing_task")
public class BizWritingTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户 ID */
    private Long userId;

    /** 任务标题 */
    private String title;

    /** 写作类型：outline、draft、polished、title */
    private String writingType;

    /** 写作主题 */
    private String topic;

    /** 写作要求 */
    private String requirement;

    /** 用户输入内容 */
    private String inputContent;

    /** 任务状态：pending、running、success、failed */
    private String status;

    /** 失败时的错误信息 */
    private String errorMessage;

    /** 任务开始执行时间 */
    private LocalDateTime startedAt;

    /** 任务完成时间 */
    private LocalDateTime finishedAt;

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
