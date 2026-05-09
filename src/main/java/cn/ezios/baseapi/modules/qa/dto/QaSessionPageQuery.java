package cn.ezios.baseapi.modules.qa.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QaSessionPageQuery extends PageQuery {

    private String status;
}
