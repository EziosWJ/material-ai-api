package cn.ezios.baseapi.modules.qa.dto;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

/**
 * 创建问答会话请求 DTO。
 */
@Data
public class QaSessionCreateRequest {

    /** 会话标题，最多 200 字符，为空时使用默认标题 */
    @Size(max = 200, message = "会话标题不能超过 200 个字符")
    private String title;

    /** 初始关联的材料 ID 列表，可为空 */
    private List<Long> materialIds;
}
