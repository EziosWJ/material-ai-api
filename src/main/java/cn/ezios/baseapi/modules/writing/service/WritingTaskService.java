package cn.ezios.baseapi.modules.writing.service;

import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.modules.writing.dto.WritingTaskCreateRequest;
import cn.ezios.baseapi.modules.writing.dto.WritingTaskPageQuery;
import cn.ezios.baseapi.modules.writing.vo.WritingTaskVO;

public interface WritingTaskService {

    WritingTaskVO create(WritingTaskCreateRequest request);

    PageResult<WritingTaskVO> page(WritingTaskPageQuery query);

    WritingTaskVO getDetail(Long id);
}
