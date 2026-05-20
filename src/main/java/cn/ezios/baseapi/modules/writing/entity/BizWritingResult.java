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
 * 写作结果实体，对应 biz_writing_result 表，存储 AI 生成的写作内容和来源片段快照。
 */
@Data
@TableName("biz_writing_result")
public class BizWritingResult {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联的写作任务 ID */
    private Long taskId;

    /** 用户 ID */
    private Long userId;

    /** 结果版本号，同一任务可能有多次生成 */
    private Integer versionNo;

    /** AI 生成的写作内容 */
    private String content;

    /** 来源片段快照，JSON 格式存储 */
    private String sourceSegmentsJson;

    /** 使用的模型名称 */
    private String modelName;

    /** 关联的 AI 调用日志 ID */
    private Long aiCallLogId;

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
