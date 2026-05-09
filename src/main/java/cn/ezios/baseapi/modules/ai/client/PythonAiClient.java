package cn.ezios.baseapi.modules.ai.client;

import cn.ezios.baseapi.modules.ai.dto.PythonAiAskRequest;
import cn.ezios.baseapi.modules.ai.dto.PythonAiAskResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiGenerateRequest;
import cn.ezios.baseapi.modules.ai.dto.PythonAiGenerateResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiHealthResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiMaterialProcessResponse;
import cn.ezios.baseapi.modules.ai.dto.PythonAiVectorDeleteResponse;
import org.springframework.core.io.Resource;

public interface PythonAiClient {

    PythonAiHealthResponse health(boolean deep);

    PythonAiMaterialProcessResponse processMaterial(Long userId, Long materialId, Resource file);

    PythonAiVectorDeleteResponse deleteMaterialVectors(Long userId, Long materialId);

    PythonAiGenerateResponse generate(PythonAiGenerateRequest request);

    PythonAiAskResponse ask(PythonAiAskRequest request);
}
