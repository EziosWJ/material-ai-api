package cn.ezios.baseapi.modules.qa.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 问答会话分页查询条件 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QaSessionPageQuery extends PageQuery {

    /** 会话状态过滤条件 */
    private String status;
}
