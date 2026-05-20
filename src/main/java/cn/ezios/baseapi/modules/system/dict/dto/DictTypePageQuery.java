package cn.ezios.baseapi.modules.system.dict.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictTypePageQuery extends PageQuery {

    /** 字典名称，模糊匹配 */
    private String dictName;

    /** 字典编码，模糊匹配 */
    private String dictCode;

    /** 状态：1-启用，0-禁用 */
    private Integer status;
}
