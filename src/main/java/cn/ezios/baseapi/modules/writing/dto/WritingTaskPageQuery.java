package cn.ezios.baseapi.modules.writing.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 写作任务分页查询 DTO，支持按写作类型、状态和标题进行筛选。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WritingTaskPageQuery extends PageQuery {

    /** 写作类型筛选 */
    private String writingType;

    /** 任务状态筛选：pending、running、success、failed */
    private String status;

    /** 任务标题模糊搜索 */
    private String title;
}
