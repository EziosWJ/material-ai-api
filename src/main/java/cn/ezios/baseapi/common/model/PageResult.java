package cn.ezios.baseapi.common.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一分页结果结构，用于封装分页查询的返回数据。
 *
 * @param <T> 列表元素类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 当前页数据列表 */
    private List<T> records;
    /** 符合条件的总记录数 */
    private long total;
    /** 当前页码 */
    private long page;
    /** 每页条数 */
    private long pageSize;
}
