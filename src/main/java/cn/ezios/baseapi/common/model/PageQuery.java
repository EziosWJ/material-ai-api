package cn.ezios.baseapi.common.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 通用分页查询参数，作为分页接口的请求基类使用。
 */
@Data
public class PageQuery {

    /** 页码，从 1 开始 */
    @Min(value = 1, message = "页码不能小于 1")
    private long page = 1;

    /** 每页条数，最大 500 */
    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 500, message = "每页条数不能超过 500")
    private long pageSize = 10;
}
