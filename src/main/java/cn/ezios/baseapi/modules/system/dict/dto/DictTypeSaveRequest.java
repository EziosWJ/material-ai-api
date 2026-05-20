package cn.ezios.baseapi.modules.system.dict.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典类型保存请求
 */
@Data
public class DictTypeSaveRequest {

    /** 字典名称 */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称长度不能超过 100")
    private String dictName;

    /** 字典编码，全局唯一 */
    @NotBlank(message = "字典编码不能为空")
    @Size(max = 100, message = "字典编码长度不能超过 100")
    private String dictCode;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 排序序号 */
    private Integer sortOrder;

    /** 备注 */
    private String remark;
}
