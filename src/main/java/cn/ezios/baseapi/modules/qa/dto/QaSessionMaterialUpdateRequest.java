package cn.ezios.baseapi.modules.qa.dto;

import java.util.List;
import lombok.Data;

@Data
public class QaSessionMaterialUpdateRequest {

    private List<Long> materialIds;
}
