package cn.ezios.baseapi.modules.system.config.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 系统配置实体
 * <p>对应数据库表 sys_config，存储系统参数配置信息</p>
 */
@Data
@TableName("sys_config")
public class SysConfig {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 配置名称 */
    private String configName;

    /** 配置键，全局唯一 */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 配置类型，如 SYSTEM、BUSINESS */
    private String configType;

    /** 值类型，如 TEXT、NUMBER、JSON */
    private String valueType;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 是否内置：1-内置，0-非内置（内置配置禁止修改和删除） */
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
