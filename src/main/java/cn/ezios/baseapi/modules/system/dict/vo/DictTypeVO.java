package cn.ezios.baseapi.modules.system.dict.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 字典类型视图对象
 */
@Data
public class DictTypeVO {

    /** 主键ID */
    private Long id;

    /** 字典名称 */
    private String dictName;

    /** 字典编码 */
    private String dictCode;

    /** 状态 */
    private Integer status;

    /** 排序序号 */
    private Integer sortOrder;

    /** 是否内置 */
    private Integer isBuiltin;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
