package cn.ezios.baseapi.modules.qa.controller;

import cn.ezios.baseapi.common.model.ApiResponse;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.framework.log.OperLog;
import cn.ezios.baseapi.modules.qa.dto.QaAskRequest;
import cn.ezios.baseapi.modules.qa.dto.QaSessionCreateRequest;
import cn.ezios.baseapi.modules.qa.dto.QaSessionMaterialUpdateRequest;
import cn.ezios.baseapi.modules.qa.dto.QaSessionPageQuery;
import cn.ezios.baseapi.modules.qa.service.QaSessionService;
import cn.ezios.baseapi.modules.qa.vo.QaAskVO;
import cn.ezios.baseapi.modules.qa.vo.QaMaterialVO;
import cn.ezios.baseapi.modules.qa.vo.QaMessageVO;
import cn.ezios.baseapi.modules.qa.vo.QaSessionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "问答会话")
@Validated
@RestController
@RequestMapping("/api/qa/session")
public class QaSessionController {

    private final QaSessionService qaSessionService;

    public QaSessionController(QaSessionService qaSessionService) {
        this.qaSessionService = qaSessionService;
    }

    @OperLog(title = "问答会话", type = "CREATE")
    @Operation(summary = "创建问答会话")
    @PostMapping
    public ApiResponse<QaSessionVO> create(@Valid @RequestBody QaSessionCreateRequest request) {
        return ApiResponse.success(qaSessionService.create(request));
    }

    @Operation(summary = "问答会话分页")
    @GetMapping("/page")
    public ApiResponse<PageResult<QaSessionVO>> page(@Valid QaSessionPageQuery query) {
        return ApiResponse.success(qaSessionService.page(query));
    }

    @Operation(summary = "问答会话详情")
    @GetMapping("/{id}")
    public ApiResponse<QaSessionVO> detail(@PathVariable Long id) {
        return ApiResponse.success(qaSessionService.detail(id));
    }

    @OperLog(title = "问答会话材料", type = "UPDATE")
    @Operation(summary = "维护会话材料集合")
    @PutMapping("/{id}/material")
    public ApiResponse<List<QaMaterialVO>> updateMaterials(@PathVariable Long id,
            @RequestBody QaSessionMaterialUpdateRequest request) {
        return ApiResponse.success(qaSessionService.updateMaterials(id, request));
    }

    @Operation(summary = "查询会话消息")
    @GetMapping("/{id}/message")
    public ApiResponse<List<QaMessageVO>> messages(@PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean includeSystem) {
        return ApiResponse.success(qaSessionService.messages(id, includeSystem));
    }

    @OperLog(title = "问答消息", type = "CREATE")
    @Operation(summary = "发送问题")
    @PostMapping("/{id}/ask")
    public ApiResponse<QaAskVO> ask(@PathVariable Long id, @Valid @RequestBody QaAskRequest request) {
        return ApiResponse.success(qaSessionService.ask(id, request));
    }
}
