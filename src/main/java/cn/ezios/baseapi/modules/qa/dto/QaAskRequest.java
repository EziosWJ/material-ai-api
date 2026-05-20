package cn.ezios.baseapi.modules.qa.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 问答提问请求 DTO，包含用户问题和向量检索参数。
 */
@Data
public class QaAskRequest {

    /** 用户提出的问题内容 */
    @NotBlank(message = "问题不能为空")
    private String question;

    /** 向量检索返回的最大片段数量，范围 1-20 */
    @Min(value = 1, message = "topK 范围必须为 1-20")
    @Max(value = 20, message = "topK 范围必须为 1-20")
    private Integer topK;
}
