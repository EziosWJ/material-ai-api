package cn.ezios.baseapi.modules.writing.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WritingTaskPageQuery extends PageQuery {

    private String writingType;

    private String status;

    private String title;
}
