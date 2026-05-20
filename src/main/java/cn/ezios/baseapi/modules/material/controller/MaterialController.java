package cn.ezios.baseapi.modules.material.controller;

import cn.ezios.baseapi.common.model.ApiResponse;
import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.framework.log.OperLog;
import cn.ezios.baseapi.modules.material.dto.MaterialPageQuery;
import cn.ezios.baseapi.modules.material.dto.MaterialProcessRequest;
import cn.ezios.baseapi.modules.material.dto.MaterialSaveRequest;
import cn.ezios.baseapi.modules.material.dto.MaterialUpdateRequest;
import cn.ezios.baseapi.modules.material.service.MaterialService;
import cn.ezios.baseapi.modules.material.vo.MaterialVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 材料管理 REST 控制器
 * <p>提供材料的增删改查、材料处理、向量删除等 HTTP 接口</p>
 */
@Tag(name = "材料管理")
@Validated
@RestController
@RequestMapping("/api/material")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @OperLog(title = "材料管理", type = "CREATE")
    @Operation(summary = "新增材料")
    @PostMapping
    public ApiResponse<MaterialVO> create(@Valid @RequestBody MaterialSaveRequest request) {
        return ApiResponse.success(materialService.create(request));
    }

    @Operation(summary = "材料分页")
    @GetMapping("/page")
    public ApiResponse<PageResult<MaterialVO>> page(@Valid MaterialPageQuery query) {
        return ApiResponse.success(materialService.page(query));
    }

    @Operation(summary = "材料详情")
    @GetMapping("/{id}")
    public ApiResponse<MaterialVO> detail(@PathVariable Long id) {
        return ApiResponse.success(materialService.getDetail(id));
    }

    @OperLog(title = "材料管理", type = "UPDATE")
    @Operation(summary = "修改材料")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody MaterialUpdateRequest request) {
        materialService.update(id, request);
        return ApiResponse.success();
    }

    @OperLog(title = "材料处理", type = "PROCESS")
    @Operation(summary = "处理材料")
    @PostMapping("/{id}/process")
    public ApiResponse<MaterialVO> process(@PathVariable Long id,
            @Valid @RequestBody(required = false) MaterialProcessRequest request) {
        return ApiResponse.success(materialService.process(id, request));
    }

    @OperLog(title = "材料向量维护", type = "DELETE")
    @Operation(summary = "删除材料向量")
    @PostMapping("/{id}/vector-delete")
    public ApiResponse<Void> deleteMaterialVectors(@PathVariable Long id) {
        materialService.deleteMaterialVectors(id);
        return ApiResponse.success();
    }

    @OperLog(title = "材料管理", type = "DELETE")
    @Operation(summary = "删除材料")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        materialService.delete(id);
        return ApiResponse.success();
    }

    @OperLog(title = "材料管理", type = "DELETE")
    @Operation(summary = "批量删除材料")
    @PostMapping("/batch-delete")
    public ApiResponse<Void> deleteBatch(@Valid @RequestBody BatchIdsRequest request) {
        materialService.deleteBatch(request);
        return ApiResponse.success();
    }
}
