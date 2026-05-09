package cn.ezios.baseapi.modules.qa.dto;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class QaSessionCreateRequest {

    @Size(max = 200, message = "会话标题不能超过 200 个字符")
    private String title;

    private List<Long> materialIds;
}
