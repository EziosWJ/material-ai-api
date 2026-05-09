package cn.ezios.baseapi.modules.material.controller;

import cn.ezios.baseapi.common.model.ApiResponse;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.modules.material.dto.MaterialProcessRecordPageQuery;
import cn.ezios.baseapi.modules.material.service.MaterialProcessRecordService;
import cn.ezios.baseapi.modules.material.vo.MaterialProcessRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "材料处理记录")
@Validated
@RestController
@RequestMapping("/api/material/process-record")
public class MaterialProcessRecordController {

    private final MaterialProcessRecordService processRecordService;

    public MaterialProcessRecordController(MaterialProcessRecordService processRecordService) {
        this.processRecordService = processRecordService;
    }

    @Operation(summary = "材料处理记录分页")
    @GetMapping("/page")
    public ApiResponse<PageResult<MaterialProcessRecordVO>> page(@Valid MaterialProcessRecordPageQuery query) {
        return ApiResponse.success(processRecordService.page(query));
    }

    @Operation(summary = "材料处理记录详情")
    @GetMapping("/{id}")
    public ApiResponse<MaterialProcessRecordVO> detail(@PathVariable Long id) {
        return ApiResponse.success(processRecordService.getDetail(id));
    }
}
