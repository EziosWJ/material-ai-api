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
 * 字典类型实体
 * <p>对应数据库表 sys_dict_type，定义字典的分类</p>
 */
@Data
@TableName("sys_dict_type")
public class SysDictType {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 字典名称 */
    private String dictName;

    /** 字典编码，全局唯一 */
    private String dictCode;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 排序序号 */
    private Integer sortOrder;

    /** 是否内置：1-内置，0-非内置 */
    private Integer isBuiltin;

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
