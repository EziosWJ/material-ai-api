package cn.ezios.baseapi.modules.ai.dto;

import lombok.Data;

/**
 * Python AI 服务材料处理响应。
 * <p>材料处理会先删除该材料的旧片段和向量，再重新切分写入。</p>
 */
@Data
public class PythonAiMaterialProcessResponse {

    /** 删除的旧片段数量 */
    private Integer deletedCount;

    /** 新生成的片段数量 */
    private Integer segmentCount;
}
