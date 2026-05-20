package cn.ezios.baseapi.modules.system.config.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 配置保存请求
 */
@Data
public class ConfigSaveRequest {

    /** 配置名称 */
    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称长度不能超过 100")
    private String configName;

    /** 配置键，全局唯一 */
    @NotBlank(message = "配置键不能为空")
    @Size(max = 100, message = "配置键长度不能超过 100")
    private String configKey;

    /** 配置值 */
    @Size(max = 500, message = "配置值长度不能超过 500")
    private String configValue;

    /** 配置类型，如 SYSTEM、BUSINESS */
    private String configType;

    /** 值类型，如 TEXT、NUMBER、JSON */
    private String valueType;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 备注 */
    private String remark;
}
