package cn.ezios.baseapi.modules.qa.dto;

import java.util.List;
import lombok.Data;

/**
 * 更新问答会话材料集合请求 DTO，执行全量替换。
 */
@Data
public class QaSessionMaterialUpdateRequest {

    /** 新的材料 ID 列表 */
    private List<Long> materialIds;
}
