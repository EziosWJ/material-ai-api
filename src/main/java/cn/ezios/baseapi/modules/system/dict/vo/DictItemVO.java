package cn.ezios.baseapi.modules.system.dict.vo;

import lombok.Data;

/**
 * 字典项视图对象
 * <p>用于前端下拉选择的简化数据结构</p>
 */
@Data
public class DictItemVO {

    /** 显示标签 */
    private String label;

    /** 实际值 */
    private String value;

    /** 排序序号 */
    private Integer sortOrder;
}
