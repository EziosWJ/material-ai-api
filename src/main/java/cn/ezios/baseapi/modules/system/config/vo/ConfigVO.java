package cn.ezios.baseapi.modules.system.config.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 配置详情视图对象
 */
@Data
public class ConfigVO {

    /** 主键ID */
    private Long id;

    /** 配置名称 */
    private String configName;

    /** 配置键 */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 配置类型 */
    private String configType;

    /** 值类型 */
    private String valueType;

    /** 状态 */
    private Integer status;

    /** 是否内置 */
    private Integer isBuiltin;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
