package cn.ezios.baseapi.modules.ai.dto;

import lombok.Data;

/**
 * Python AI 服务向量删除响应。
 */
@Data
public class PythonAiVectorDeleteResponse {

    /** 删除的向量数量 */
    private Integer deletedCount;
}
