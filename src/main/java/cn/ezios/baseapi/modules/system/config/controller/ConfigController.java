package cn.ezios.baseapi.modules.system.config.controller;

import cn.ezios.baseapi.common.model.ApiResponse;
import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.common.model.StatusUpdateRequest;
import cn.ezios.baseapi.framework.log.OperLog;
import cn.ezios.baseapi.modules.system.config.dto.ConfigPageQuery;
import cn.ezios.baseapi.modules.system.config.dto.ConfigSaveRequest;
import cn.ezios.baseapi.modules.system.config.service.ConfigService;
import cn.ezios.baseapi.modules.system.config.vo.ConfigByKeyVO;
import cn.ezios.baseapi.modules.system.config.vo.ConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置管理控制器
 * <p>提供系统参数配置的增删改查及状态管理接口</p>
 */
@Tag(name = "配置管理")
@Validated
@RestController
@RequestMapping("/api/system/config")
public class ConfigController {

    /** 配置管理服务 */
    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * 配置分页查询
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    @Operation(summary = "配置分页")
    @GetMapping("/page")
    public ApiResponse<PageResult<ConfigVO>> page(@Valid ConfigPageQuery query) {
        return ApiResponse.success(configService.page(query));
    }

    /**
     * 获取配置详情
     *
     * @param id 配置ID
     * @return 配置详情
     */
    @Operation(summary = "配置详情")
    @GetMapping("/{id}")
    public ApiResponse<ConfigVO> detail(@PathVariable Long id) {
        return ApiResponse.success(configService.getDetail(id));
    }

    /**
     * 按配置键查询配置值
     *
     * @param configKey 配置键
     * @return 配置信息
     */
    @Operation(summary = "按配置键查询")
    @GetMapping("/key/{configKey}")
    public ApiResponse<ConfigByKeyVO> getByKey(@PathVariable String configKey) {
        return ApiResponse.success(configService.getByKey(configKey));
    }

    /**
     * 新增配置
     *
     * @param request 配置保存请求
     * @return 操作结果
     */
    @OperLog(title = "配置管理", type = "CREATE")
    @Operation(summary = "新增配置")
    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody ConfigSaveRequest request) {
        configService.create(request);
        return ApiResponse.success();
    }

    /**
     * 修改配置
     *
     * @param id      配置ID
     * @param request 配置保存请求
     * @return 操作结果
     */
    @OperLog(title = "配置管理", type = "UPDATE")
    @Operation(summary = "修改配置")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ConfigSaveRequest request) {
        configService.update(id, request);
        return ApiResponse.success();
    }

    /**
     * 删除配置
     *
     * @param id 配置ID
     * @return 操作结果
     */
    @OperLog(title = "配置管理", type = "DELETE")
    @Operation(summary = "删除配置")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        configService.delete(id);
        return ApiResponse.success();
    }

    /**
     * 批量删除配置
     *
     * @param request 包含配置ID列表的请求
     * @return 操作结果
     */
    @OperLog(title = "配置管理", type = "DELETE")
    @Operation(summary = "批量删除配置")
    @PostMapping("/batch-delete")
    public ApiResponse<Void> deleteBatch(@Valid @RequestBody BatchIdsRequest request) {
        configService.deleteBatch(request);
        return ApiResponse.success();
    }

    /**
     * 修改配置状态
     *
     * @param id      配置ID
     * @param request 状态更新请求
     * @return 操作结果
     */
    @OperLog(title = "配置管理", type = "UPDATE")
    @Operation(summary = "修改配置状态")
    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        configService.updateStatus(id, request);
        return ApiResponse.success();
    }
}
