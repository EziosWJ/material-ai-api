package cn.ezios.baseapi.modules.material.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MaterialProcessRequest {

    @Pattern(regexp = "initial|reprocess", message = "处理类型只能为 initial 或 reprocess")
    private String processType;
}
