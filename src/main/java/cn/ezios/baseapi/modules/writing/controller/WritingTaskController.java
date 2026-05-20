package cn.ezios.baseapi.modules.writing.controller;

import cn.ezios.baseapi.common.model.ApiResponse;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.framework.log.OperLog;
import cn.ezios.baseapi.modules.writing.dto.WritingTaskCreateRequest;
import cn.ezios.baseapi.modules.writing.dto.WritingTaskPageQuery;
import cn.ezios.baseapi.modules.writing.service.WritingTaskService;
import cn.ezios.baseapi.modules.writing.vo.WritingTaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 写作任务控制器，提供写作任务的创建、分页查询和详情查看接口。
 */
@Tag(name = "写作任务")
@Validated
@RestController
@RequestMapping("/api/writing/task")
public class WritingTaskController {

    private final WritingTaskService writingTaskService;

    public WritingTaskController(WritingTaskService writingTaskService) {
        this.writingTaskService = writingTaskService;
    }

    /**
     * 创建写作任务，同步调用 Python AI 服务生成内容并返回结果。
     *
     * @param request 创建请求，包含标题、写作类型、主题等参数
     * @return 写作任务详情（含生成结果）
     */
    @OperLog(title = "写作任务", type = "CREATE")
    @Operation(summary = "创建写作任务")
    @PostMapping
    public ApiResponse<WritingTaskVO> create(@Valid @RequestBody WritingTaskCreateRequest request) {
        return ApiResponse.success(writingTaskService.create(request));
    }

    /**
     * 分页查询当前用户的写作任务列表。
     *
     * @param query 分页查询条件，支持按写作类型、状态、标题筛选
     * @return 分页结果
     */
    @Operation(summary = "写作任务分页")
    @GetMapping("/page")
    public ApiResponse<PageResult<WritingTaskVO>> page(@Valid WritingTaskPageQuery query) {
        return ApiResponse.success(writingTaskService.page(query));
    }

    /**
     * 查看写作任务详情，仅限当前用户自己的任务。
     *
     * @param id 任务 ID
     * @return 写作任务详情
     */
    @Operation(summary = "写作任务详情")
    @GetMapping("/{id}")
    public ApiResponse<WritingTaskVO> detail(@PathVariable Long id) {
        return ApiResponse.success(writingTaskService.getDetail(id));
    }
}
