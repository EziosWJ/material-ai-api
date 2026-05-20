package cn.ezios.baseapi.common.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 通用状态变更请求体，用于启用/禁用类的状态切换操作。
 * 0 表示禁用，1 表示启用。
 */
@Data
public class StatusUpdateRequest {

    /** 目标状态：0-禁用，1-启用 */
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能为 0 或 1")
    @Max(value = 1, message = "状态只能为 0 或 1")
    private Integer status;
}
