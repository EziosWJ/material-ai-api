package cn.ezios.baseapi.modules.system.log.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OperLogPageQuery extends PageQuery {

    /** 模块名称，模糊匹配 */
    private String moduleName;

    /** 操作类型 */
    private String operationType;

    /** 操作人名称，模糊匹配 */
    private String operatorName;

    /** 操作状态 */
    private String operationStatus;
}
