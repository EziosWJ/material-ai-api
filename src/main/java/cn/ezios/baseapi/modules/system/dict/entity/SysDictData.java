package cn.ezios.baseapi.modules.system.dict.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 字典数据实体
 * <p>对应数据库表 sys_dict_data，存储字典类型下的具体数据项</p>
 */
@Data
@TableName("sys_dict_data")
public class SysDictData {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 字典类型ID */
    private Long dictTypeId;

    /** 字典标签（显示名称） */
    private String dictLabel;

    /** 字典值（实际存储值） */
    private String dictValue;

    /** 排序序号 */
    private Integer sortOrder;

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
