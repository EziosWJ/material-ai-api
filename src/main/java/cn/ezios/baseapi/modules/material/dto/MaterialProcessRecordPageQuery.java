package cn.ezios.baseapi.modules.material.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MaterialProcessRecordPageQuery extends PageQuery {

    private Long materialId;

    private Long userId;

    private Long fileId;

    private String processType;

    private String status;
}
