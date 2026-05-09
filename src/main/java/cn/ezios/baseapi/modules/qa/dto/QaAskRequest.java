package cn.ezios.baseapi.modules.qa.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QaAskRequest {

    @NotBlank(message = "问题不能为空")
    private String question;

    @Min(value = 1, message = "topK 范围必须为 1-20")
    @Max(value = 20, message = "topK 范围必须为 1-20")
    private Integer topK;
}
