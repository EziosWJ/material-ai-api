package cn.ezios.baseapi.modules.writing.service;

import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.modules.writing.dto.WritingTaskCreateRequest;
import cn.ezios.baseapi.modules.writing.dto.WritingTaskPageQuery;
import cn.ezios.baseapi.modules.writing.vo.WritingTaskVO;

/**
 * 写作任务服务接口，提供写作任务的创建、分页查询和详情查看能力。
 */
public interface WritingTaskService {

    /**
     * 创建写作任务并同步调用 AI 服务生成内容。
     *
     * @param request 创建请求参数
     * @return 写作任务详情（含生成结果或失败信息）
     */
    WritingTaskVO create(WritingTaskCreateRequest request);

    /**
     * 分页查询当前用户的写作任务。
     *
     * @param query 分页及筛选条件
     * @return 分页结果
     */
    PageResult<WritingTaskVO> page(WritingTaskPageQuery query);

    /**
     * 查看写作任务详情，仅限当前用户自己的任务。
     *
     * @param id 任务 ID
     * @return 写作任务详情
     */
    WritingTaskVO getDetail(Long id);
}
