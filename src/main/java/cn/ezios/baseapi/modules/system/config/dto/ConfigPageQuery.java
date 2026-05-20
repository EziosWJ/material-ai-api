package cn.ezios.baseapi.modules.system.config.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 配置分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConfigPageQuery extends PageQuery {

    /** 配置名称，模糊匹配 */
    private String configName;

    /** 配置键，模糊匹配 */
    private String configKey;

    /** 配置类型 */
    private String configType;

    /** 状态：1-启用，0-禁用 */
    private Integer status;
}
