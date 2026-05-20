package cn.ezios.baseapi.modules.system.dict.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典数据保存请求
 */
@Data
public class DictDataSaveRequest {

    /** 字典类型ID */
    @NotNull(message = "字典类型不能为空")
    private Long dictTypeId;

    /** 字典标签（显示名称） */
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过 100")
    private String dictLabel;

    /** 字典值（实际存储值） */
    @NotBlank(message = "字典值不能为空")
    @Size(max = 100, message = "字典值长度不能超过 100")
    private String dictValue;

    /** 排序序号 */
    private Integer sortOrder;

    /** 备注 */
    private String remark;
}
