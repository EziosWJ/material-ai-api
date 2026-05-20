package cn.ezios.baseapi.modules.system.dict.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 字典数据视图对象
 */
@Data
public class DictDataVO {

    /** 主键ID */
    private Long id;

    /** 字典类型ID */
    private Long dictTypeId;

    /** 字典编码（关联查询时填充） */
    private String dictCode;

    /** 字典标签 */
    private String dictLabel;

    /** 字典值 */
    private String dictValue;

    /** 排序序号 */
    private Integer sortOrder;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
