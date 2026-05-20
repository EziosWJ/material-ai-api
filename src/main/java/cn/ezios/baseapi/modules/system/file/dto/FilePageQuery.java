package cn.ezios.baseapi.modules.system.file.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FilePageQuery extends PageQuery {

    /** 原始文件名，模糊匹配 */
    private String originalName;

    /** 业务模块 */
    private String businessModule;

    /** MIME类型，模糊匹配 */
    private String mimeType;

    /** 状态：1-启用，0-禁用 */
    private Integer status;
}
