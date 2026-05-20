package cn.ezios.baseapi.common.model;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

/**
 * 批量操作通用请求体，用于接收一组业务实体 ID。
 */
@Data
public class BatchIdsRequest {

    /** 待操作的 ID 列表 */
    @NotEmpty(message = "ID 列表不能为空")
    private List<Long> ids;
}
