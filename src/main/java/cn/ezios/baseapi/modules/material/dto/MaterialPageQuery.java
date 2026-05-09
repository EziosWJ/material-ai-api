package cn.ezios.baseapi.modules.material.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MaterialPageQuery extends PageQuery {

    private String title;

    private Long fileId;

    private String fileType;

    private String status;
}
