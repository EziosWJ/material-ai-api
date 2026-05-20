package cn.ezios.baseapi.modules.system.dict.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictDataPageQuery extends PageQuery {

    /** 字典类型ID */
    private Long dictTypeId;

    /** 字典编码（用于通过编码查找类型下的数据） */
    private String dictCode;

    /** 字典标签，模糊匹配 */
    private String dictLabel;

    /** 字典值，模糊匹配 */
    private String dictValue;
}
