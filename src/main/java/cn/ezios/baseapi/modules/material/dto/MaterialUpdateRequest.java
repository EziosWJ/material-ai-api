package cn.ezios.baseapi.modules.material.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaterialUpdateRequest {

    @Size(max = 200, message = "材料标题长度不能超过 200")
    private String title;

    @Size(max = 32, message = "材料状态长度不能超过 32")
    private String status;

    private Integer segmentCount;

    private String errorMessage;

    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;
}
