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

/**
 * 问答会话控制器，提供问答会话的创建、查询、材料维护和提问等 REST 接口。
 */
@Tag(name = "问答会话")
@Validated
@RestController
@RequestMapping("/api/qa/session")
public class QaSessionController {

    private final QaSessionService qaSessionService;

    public QaSessionController(QaSessionService qaSessionService) {
        this.qaSessionService = qaSessionService;
    }

    /**
     * 创建问答会话，可选关联初始材料集合。
     *
     * @param request 创建请求，包含标题和材料 ID 列表
     * @return 创建后的会话详情
     */
    @OperLog(title = "问答会话", type = "CREATE")
    @Operation(summary = "创建问答会话")
    @PostMapping
    public ApiResponse<QaSessionVO> create(@Valid @RequestBody QaSessionCreateRequest request) {
        return ApiResponse.success(qaSessionService.create(request));
    }

    /**
     * 分页查询当前用户的问答会话列表。
     *
     * @param query 分页与过滤条件
     * @return 分页结果
     */
    @Operation(summary = "问答会话分页")
    @GetMapping("/page")
    public ApiResponse<PageResult<QaSessionVO>> page(@Valid QaSessionPageQuery query) {
        return ApiResponse.success(qaSessionService.page(query));
    }

    /**
     * 查询问答会话详情。
     *
     * @param id 会话 ID
     * @return 会话详情
     */
    @Operation(summary = "问答会话详情")
    @GetMapping("/{id}")
    public ApiResponse<QaSessionVO> detail(@PathVariable Long id) {
        return ApiResponse.success(qaSessionService.detail(id));
    }

    /**
     * 维护问答会话关联的材料集合，执行全量替换。
     *
     * @param id      会话 ID
     * @param request 包含新的材料 ID 列表
     * @return 替换后的材料列表
     */
    @OperLog(title = "问答会话材料", type = "UPDATE")
    @Operation(summary = "维护会话材料集合")
    @PutMapping("/{id}/material")
    public ApiResponse<List<QaMaterialVO>> updateMaterials(@PathVariable Long id,
            @RequestBody QaSessionMaterialUpdateRequest request) {
        return ApiResponse.success(qaSessionService.updateMaterials(id, request));
    }

    /**
     * 查询指定会话的消息列表。
     *
     * @param id            会话 ID
     * @param includeSystem 是否包含系统角色消息
     * @return 消息列表
     */
    @Operation(summary = "查询会话消息")
    @GetMapping("/{id}/message")
    public ApiResponse<List<QaMessageVO>> messages(@PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean includeSystem) {
        return ApiResponse.success(qaSessionService.messages(id, includeSystem));
    }

    /**
     * 向会话发送问题，触发 AI 回答并返回用户消息和助手回复。
     *
     * @param id      会话 ID
     * @param request 提问请求，包含问题内容和检索参数
     * @return 包含用户消息和助手回复的问答结果
     */
    @OperLog(title = "问答消息", type = "CREATE")
    @Operation(summary = "发送问题")
    @PostMapping("/{id}/ask")
    public ApiResponse<QaAskVO> ask(@PathVariable Long id, @Valid @RequestBody QaAskRequest request) {
        return ApiResponse.success(qaSessionService.ask(id, request));
    }
}
