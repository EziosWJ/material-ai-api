package cn.ezios.baseapi.modules.ai.client;

import cn.ezios.baseapi.modules.ai.dto.PythonAiAskRequest;
import cn.ezios.baseapi.modules.ai.dto.PythonAiAskResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiGenerateRequest;
import cn.ezios.baseapi.modules.ai.dto.PythonAiGenerateResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiHealthResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiMaterialProcessResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiVectorDeleteResponse;
import org.springframework.core.io.Resource;

/**
 * Python AI 服务客户端接口。
 * <p>封装对 Python AI 服务的 HTTP 调用，包括健康检查、材料处理、向量删除、内容生成和材料问答。</p>
 */
public interface PythonAiClient {

    /**
     * 检查 Python AI 服务健康状态。
     *
     * @param deep 是否执行深度检查（如依赖服务连通性）
     * @return 健康状态响应
     */
    PythonAiHealthResponse health(boolean deep);

    /**
     * 调用 Python AI 服务处理材料文件，完成片段切分和向量写入。
     *
     * @param userId     用户 ID
     * @param materialId 材料 ID
     * @param file       材料文件资源
     * @return 处理结果，包含删除旧片段数和新增片段数
     */
    PythonAiMaterialProcessResponse processMaterial(Long userId, Long materialId, Resource file);

    /**
     * 删除指定材料的所有向量数据。
     *
     * @param userId     用户 ID
     * @param materialId 材料 ID
     * @return 删除结果，包含删除的向量数量
     */
    PythonAiVectorDeleteResponse deleteMaterialVectors(Long userId, Long materialId);

    /**
     * 调用 AI 服务生成写作内容。
     *
     * @param request 生成请求，包含写作类型、主题、参考材料等
     * @return 生成结果，包含生成文本和来源片段
     */
    PythonAiGenerateResponse generate(PythonAiGenerateRequest request);

    /**
     * 基于材料进行问答。
     *
     * @param request 问答请求，包含用户问题和参考材料
     * @return 问答结果，包含回答文本和来源片段
     */
    PythonAiAskResponse ask(PythonAiAskRequest request);
}
