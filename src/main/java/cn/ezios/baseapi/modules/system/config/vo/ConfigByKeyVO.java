package cn.ezios.baseapi.modules.system.config.vo;

import lombok.Data;

/**
 * 按配置键查询返回的配置信息
 */
@Data
public class ConfigByKeyVO {

    /** 配置键 */
    private String configKey;

    /** 配置值 */
    private String configValue;

    /** 值类型 */
    private String valueType;

    /** 配置名称 */
    private String configName;
}
