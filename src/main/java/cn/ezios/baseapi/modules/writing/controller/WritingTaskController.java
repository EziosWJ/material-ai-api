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

@Tag(name = "写作任务")
@Validated
@RestController
@RequestMapping("/api/writing/task")
public class WritingTaskController {

    private final WritingTaskService writingTaskService;

    public WritingTaskController(WritingTaskService writingTaskService) {
        this.writingTaskService = writingTaskService;
    }

    @OperLog(title = "写作任务", type = "CREATE")
    @Operation(summary = "创建写作任务")
    @PostMapping
    public ApiResponse<WritingTaskVO> create(@Valid @RequestBody WritingTaskCreateRequest request) {
        return ApiResponse.success(writingTaskService.create(request));
    }

    @Operation(summary = "写作任务分页")
    @GetMapping("/page")
    public ApiResponse<PageResult<WritingTaskVO>> page(@Valid WritingTaskPageQuery query) {
        return ApiResponse.success(writingTaskService.page(query));
    }

    @Operation(summary = "写作任务详情")
    @GetMapping("/{id}")
    public ApiResponse<WritingTaskVO> detail(@PathVariable Long id) {
        return ApiResponse.success(writingTaskService.getDetail(id));
    }
}
